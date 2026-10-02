package com.example.data

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import com.example.util.PcmWavUtils
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class GeminiService {

  private val okHttpClient = OkHttpClient.Builder()
    .connectTimeout(30, TimeUnit.SECONDS)
    .readTimeout(45, TimeUnit.SECONDS)
    .writeTimeout(30, TimeUnit.SECONDS)
    .build()

  companion object {
    // Primary model tested for high availability and interactive conversation
    private const val PRIMARY_MODEL = "gemini-3.5-flash"
    // Candidate models in fallback order if primary encounters high-demand spikes (503) or rate limits
    private val CANDIDATE_MODELS = listOf(
      "gemini-3.5-flash",
      "gemini-3.1-flash-lite-preview",
      "gemini-3.5-flash-lite",
      "gemini-3.8-flash"
    )
    private const val TTS_MODEL_NAME = "gemini-2.5-flash-preview-tts"

    private const val BASE_URL_PREFIX = "https://generativelanguage.googleapis.com/v1beta/models/"
    private const val TTS_BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/$TTS_MODEL_NAME:generateContent"

    private const val MAX_TRANSIENT_RETRIES = 3
  }

  fun isApiKeyConfigured(): Boolean {
    val key = BuildConfig.GEMINI_API_KEY
    return key.isNotBlank() && key != "MY_GEMINI_API_KEY"
  }

  suspend fun sendChatMessage(
    history: List<ChatMessage>,
    newUserMessage: String,
    targetLanguage: Language,
    nativeLanguage: Language,
    level: TutorLevel,
    explainInNativeLanguage: Boolean
  ): AiResult = withContext(Dispatchers.IO) {
    val apiKey = BuildConfig.GEMINI_API_KEY
    if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
      return@withContext AiResult.MissingApiKey(
        "To enable live conversational tutoring, configure your AI API key in the Secrets panel in AI Studio. Offline lessons and practice remain fully accessible."
      )
    }

    val trimmedUserMsg = newUserMessage.trim()
    if (trimmedUserMsg.isBlank()) {
      return@withContext AiResult.Error("Please enter a message to send.")
    }

    val systemPrompt = buildSystemPrompt(targetLanguage, nativeLanguage, level, explainInNativeLanguage)
    val contentsArray = buildAlternatingContents(history, trimmedUserMsg)

    // Build root JSON payload
    val rootJson = JSONObject().apply {
      put("systemInstruction", JSONObject().apply {
        put("parts", JSONArray().put(JSONObject().put("text", systemPrompt)))
      })
      put("generationConfig", JSONObject().apply {
        put("temperature", 0.7)
        put("topP", 0.95)
      })
      put("contents", contentsArray)
    }

    val requestBodyString = rootJson.toString()
    var currentModel = PRIMARY_MODEL
    var lastStatusCode = 0
    var lastErrorMessage = ""

    // Retry loop with exponential backoff & model rotation for transient errors (500, 502, 503, 504, 408, 404, timeouts)
    for (attempt in 0 until MAX_TRANSIENT_RETRIES) {
      try {
        val requestBody = requestBodyString.toRequestBody("application/json".toMediaType())
        val url = "$BASE_URL_PREFIX$currentModel:generateContent?key=$apiKey"

        val httpRequest = Request.Builder()
          .url(url)
          .post(requestBody)
          .build()

        val response = okHttpClient.newCall(httpRequest).execute()
        val responseBodyString = response.body?.string().orEmpty()
        lastStatusCode = response.code

        when (response.code) {
          200 -> {
            val responseJson = JSONObject(responseBodyString)
            val candidates = responseJson.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val text = parts?.optJSONObject(0)?.optString("text").orEmpty().trim()

            return@withContext if (text.isNotBlank()) {
              AiResult.Success(text)
            } else {
              AiResult.Error("The AI tutor returned an empty reply. Please tap Retry.")
            }
          }

          // Transient server errors: rotate to next candidate model and retry with exponential backoff
          500, 502, 503, 504, 408 -> {
            lastErrorMessage = when (response.code) {
              503 -> "The AI service is temporarily busy (HTTP 503)."
              504 -> "Gateway timed out (HTTP 504)."
              408 -> "Request timed out (HTTP 408)."
              else -> "The AI service encountered a temporary error (HTTP ${response.code})."
            }

            // Rotate model on transient failure
            val nextModelIndex = (attempt + 1) % CANDIDATE_MODELS.size
            currentModel = CANDIDATE_MODELS[nextModelIndex]

            // Backoff with jitter if more retries remain
            if (attempt < MAX_TRANSIENT_RETRIES - 1) {
              val backoffMs = (400L * (1L shl attempt)) + Random.nextLong(100, 300)
              delay(backoffMs)
              continue
            }
          }

          429 -> {
            return@withContext AiResult.RateLimited(
              "AI usage quota temporarily reached. Please wait a moment before trying again."
            )
          }

          400 -> {
            return@withContext AiResult.Error(
              "The message could not be processed due to formatting. Please try rephrasing."
            )
          }

          401, 403 -> {
            return@withContext AiResult.MissingApiKey(
              "Authentication error (HTTP ${response.code}). Please verify your AI API key in the Secrets panel."
            )
          }

          404 -> {
            // Model endpoint not found: immediately rotate to next candidate model
            val nextModelIndex = (attempt + 1) % CANDIDATE_MODELS.size
            currentModel = CANDIDATE_MODELS[nextModelIndex]
            if (attempt < MAX_TRANSIENT_RETRIES - 1) {
              delay(200)
              continue
            }
            return@withContext AiResult.Error("AI model service endpoint was not found (HTTP 404).")
          }

          else -> {
            return@withContext AiResult.Error(
              "Unable to reach the AI Tutor service (HTTP ${response.code}). Please try again."
            )
          }
        }
      } catch (e: SocketTimeoutException) {
        lastStatusCode = 408
        lastErrorMessage = "Connection timed out while reaching the AI tutor service."
        val nextModelIndex = (attempt + 1) % CANDIDATE_MODELS.size
        currentModel = CANDIDATE_MODELS[nextModelIndex]
        if (attempt < MAX_TRANSIENT_RETRIES - 1) {
          val backoffMs = (400L * (1L shl attempt)) + Random.nextLong(100, 300)
          delay(backoffMs)
          continue
        }
      } catch (e: IOException) {
        lastStatusCode = 0
        lastErrorMessage = "Network connection error. Offline lessons and practice remain accessible. Check your connection and tap Retry."
        if (attempt < MAX_TRANSIENT_RETRIES - 1) {
          val backoffMs = (300L * (1L shl attempt)) + Random.nextLong(100, 200)
          delay(backoffMs)
          continue
        }
      } catch (e: Exception) {
        return@withContext AiResult.Error(
          "Error contacting AI tutor service: ${e.localizedMessage ?: "Unknown error"}. Please try again."
        )
      }
    }

    // Exhausted retries
    val finalNotice = if (lastErrorMessage.isNotBlank()) {
      "$lastErrorMessage Please tap Retry to resend."
    } else {
      "The AI tutor is temporarily unavailable (HTTP $lastStatusCode). Please tap Retry."
    }
    return@withContext AiResult.TransientError(finalNotice, if (lastStatusCode != 0) lastStatusCode else 503)
  }

  /**
   * Builds alternating turns (user -> model -> user -> model -> user) for multi-turn Gemini API.
   * Guarantees:
   * 1. No duplicate consecutive turns of the same role.
   * 2. Ends with exactly ONE user message containing the new user input.
   * 3. Drops system/error messages.
   */
  fun buildAlternatingContents(history: List<ChatMessage>, newUserMessage: String): JSONArray {
    val contentsArray = JSONArray()

    // Filter valid chat turns
    val validHistory = history.filter { it.sender != MessageSender.SYSTEM && !it.isError }

    // Build an alternating list from prior history
    val cleanTurns = mutableListOf<Pair<String, String>>() // role to text
    for (msg in validHistory) {
      val role = if (msg.sender == MessageSender.USER) "user" else "model"
      val lastRole = cleanTurns.lastOrNull()?.first
      if (lastRole == null) {
        cleanTurns.add(role to msg.text)
      } else if (lastRole != role) {
        cleanTurns.add(role to msg.text)
      } else {
        // If consecutive turns have the same role, update the last one or append text
        val prev = cleanTurns.removeAt(cleanTurns.size - 1)
        cleanTurns.add(role to "${prev.second}\n${msg.text}")
      }
    }

    // If the last history turn is a user turn that matches newUserMessage, remove it so we don't repeat
    if (cleanTurns.isNotEmpty() && cleanTurns.last().first == "user") {
      cleanTurns.removeAt(cleanTurns.size - 1)
    }

    // Include up to last 10 alternating historical turns for context
    val recentTurns = cleanTurns.takeLast(10)
    for (turn in recentTurns) {
      val turnObj = JSONObject().apply {
        put("role", turn.first)
        put("parts", JSONArray().put(JSONObject().put("text", turn.second)))
      }
      contentsArray.put(turnObj)
    }

    // Append the current new user turn as the final turn
    val currentTurnObj = JSONObject().apply {
      put("role", "user")
      put("parts", JSONArray().put(JSONObject().put("text", newUserMessage)))
    }
    contentsArray.put(currentTurnObj)

    return contentsArray
  }

  private fun buildSystemPrompt(
    targetLanguage: Language,
    nativeLanguage: Language,
    level: TutorLevel,
    explainInNativeLanguage: Boolean
  ): String {
    val isBengali = targetLanguage.id.lowercase() == "bn"
    val isArmenian = targetLanguage.id.lowercase() == "hy"

    val scriptGuidance = when {
      isBengali -> """
        - Bengali Language Instruction: Always use natural, culturally polite Bengali script (বাংলা).
        - Phonetics for Beginners: In addition to the Bengali script, provide a clear romanized phonetic pronunciation in parentheses (e.g. নমস্কার (Nomoshkar)) so learners can read and pronounce accurately.
      """.trimIndent()
      isArmenian -> """
        - Armenian Language Instruction: Use authentic Armenian script (Հայերեն) with accurate spelling.
        - Phonetics for Beginners: Provide a phonetic romanization in parentheses (e.g. Բարև (Barev)) alongside Armenian text.
      """.trimIndent()
      targetLanguage.isRtl -> """
        - Right-to-Left (RTL) Script Instruction: Use authentic ${targetLanguage.scriptType} script with standard punctuation and provide Latin transliteration in parentheses.
      """.trimIndent()
      targetLanguage.scriptType != "Latin" -> """
        - Non-Latin Script Guidance: Always provide the authentic script (${targetLanguage.name}), followed by a romanized phonetic pronunciation in parentheses for beginner ease.
      """.trimIndent()
      else -> "- Natural Language: Provide authentic phrases with clear accents and colloquial courtesy."
    }

    val explanationGuidance = if (explainInNativeLanguage) {
      """
      - Language Split: Provide interactive conversation dialogue in ${targetLanguage.name}, but provide grammar rules, word definitions, and corrections in the learner's native language (${nativeLanguage.name}).
      """.trimIndent()
    } else {
      """
      - Immersion Mode: Respond entirely in simple, accessible ${targetLanguage.name} suited for a ${level.label} learner, using ${nativeLanguage.name} only if explicitly asked.
      """.trimIndent()
    }

    return """
      You are ${BrandConfig.AI_TUTOR_NAME}, an encouraging, patient, and knowledgeable language tutor in ${BrandConfig.APP_NAME}.
      Target Language to teach: ${targetLanguage.name} (${targetLanguage.nativeName})
      Learner's Native / Interface Language: ${nativeLanguage.name}
      Current Proficiency Level: ${level.label} (${level.cefr})

      Pedagogical Guidelines:
      1. Default Dialogue: Converse in ${targetLanguage.name}, calibrated precisely for a ${level.label} (${level.cefr}) learner.
      2. Script & Pronunciation:
      $scriptGuidance
      3. Explanations & Corrections:
      $explanationGuidance
      - If the learner makes grammatical or vocabulary mistakes, offer a gentle, warm correction formatted as '💡 Tip:' or '✨ Correction:' with 1 clear example sentence.
      4. Brevity & Flow: Keep responses concise (2 to 4 sentences maximum) and always end with an open, friendly follow-up question in ${targetLanguage.name} to keep the conversation going.
      5. Accuracy: Teach real, authentic everyday language. Never invent synthetic phrases.
      6. Tone: Warm, supportive, culturally respectful, and beginner-friendly.
    """.trimIndent()
  }

  suspend fun generateTutorSpeech(
    text: String,
    targetLanguage: Language,
    voiceName: String = "Kore"
  ): SpeechSynthesisResult = withContext(Dispatchers.IO) {
    val apiKey = BuildConfig.GEMINI_API_KEY
    if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
      return@withContext SpeechSynthesisResult.MissingApiKey(
        "API key is not configured. Falling back to on-device speech engine."
      )
    }

    if (text.isBlank()) {
      return@withContext SpeechSynthesisResult.Error("No text provided for speech synthesis.")
    }

    try {
      val rootJson = JSONObject().apply {
        put("contents", JSONArray().put(JSONObject().apply {
          put("parts", JSONArray().put(JSONObject().put("text", text)))
        }))
        put("generationConfig", JSONObject().apply {
          put("responseModalities", JSONArray().put("AUDIO"))
          put("speechConfig", JSONObject().apply {
            put("voiceConfig", JSONObject().apply {
              put("prebuiltVoiceConfig", JSONObject().apply {
                put("voiceName", voiceName)
              })
            })
          })
        })
      }

      val requestBody = rootJson.toString().toRequestBody("application/json".toMediaType())
      val url = "$TTS_BASE_URL?key=$apiKey"

      val httpRequest = Request.Builder()
        .url(url)
        .post(requestBody)
        .build()

      val response = okHttpClient.newCall(httpRequest).execute()
      val responseBodyString = response.body?.string().orEmpty()

      when (response.code) {
        200 -> {
          val responseJson = JSONObject(responseBodyString)
          val candidates = responseJson.optJSONArray("candidates")
          val firstCandidate = candidates?.optJSONObject(0)
          val content = firstCandidate?.optJSONObject("content")
          val parts = content?.optJSONArray("parts")
          val firstPart = parts?.optJSONObject(0)

          val inlineData = firstPart?.optJSONObject("inlineData")
          val mimeType = inlineData?.optString("mimeType").orEmpty()
          val base64Data = inlineData?.optString("data").orEmpty()

          if (base64Data.isNotBlank()) {
            val rawBytes = android.util.Base64.decode(base64Data, android.util.Base64.DEFAULT)
            val wavBytes = PcmWavUtils.ensureWavBytes(
              rawBytes = rawBytes,
              sampleRate = 24000,
              channels = 1,
              bitsPerSample = 16
            )

            SpeechSynthesisResult.Success(
              audioData = wavBytes,
              mimeType = if (mimeType.isNotBlank()) mimeType else "audio/wav",
              modelName = TTS_MODEL_NAME,
              voiceName = voiceName
            )
          } else {
            SpeechSynthesisResult.Error("No audio payload returned from voice synthesis service.")
          }
        }
        429 -> {
          SpeechSynthesisResult.RateLimited(
            "Neural voice quota temporarily reached. Using on-device speech engine."
          )
        }
        400, 401, 403 -> {
          SpeechSynthesisResult.Error(
            "Authentication or permission error with neural speech service (HTTP ${response.code})."
          )
        }
        else -> {
          SpeechSynthesisResult.Error(
            "Neural voice service returned HTTP ${response.code}."
          )
        }
      }
    } catch (e: Exception) {
      SpeechSynthesisResult.Error(
        "Network connection error during voice synthesis: ${e.localizedMessage ?: "Unknown error"}"
      )
    }
  }
}
