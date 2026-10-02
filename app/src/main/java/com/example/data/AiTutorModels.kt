package com.example.data

enum class MessageSender {
  USER,
  TUTOR,
  SYSTEM
}

enum class TutorLevel(val label: String, val cefr: String) {
  BEGINNER("Beginner", "A1-A2"),
  INTERMEDIATE("Intermediate", "B1-B2"),
  ADVANCED("Advanced", "C1-C2")
}

data class ChatMessage(
  val id: String,
  val sender: MessageSender,
  val text: String,
  val timestamp: Long = System.currentTimeMillis(),
  val targetLanguageId: String = "es",
  val isError: Boolean = false,
  val canRetry: Boolean = false
)

sealed interface AiResult {
  data class Success(val text: String) : AiResult
  data class MissingApiKey(val message: String) : AiResult
  data class RateLimited(val message: String) : AiResult
  data class TransientError(val message: String, val statusCode: Int = 503) : AiResult
  data class Error(val message: String) : AiResult
}

enum class VoiceEngineMode(val id: String, val label: String, val description: String) {
  GEMINI_NEURAL("gemini_neural", "Idiom Voice", "Expressive neural voice with authentic conversational cadence"),
  DEVICE_TTS("device_tts", "Android System Voice", "On-device speech engine, 100% offline with zero latency")
}

data class GeminiVoiceOption(
  val voiceName: String,
  val displayName: String,
  val description: String,
  val toneTrait: String
)

val AVAILABLE_GEMINI_VOICES = listOf(
  GeminiVoiceOption("Kore", "Kore", "Balanced, articulate, and friendly tutor tone", "Balanced & Clear"),
  GeminiVoiceOption("Aoede", "Aoede", "Gentle, calm, and encouraging inflection", "Gentle & Expressive"),
  GeminiVoiceOption("Fenrir", "Fenrir", "Warm, deeper resonance with clear pacing", "Warm & Deep"),
  GeminiVoiceOption("Puck", "Puck", "Lively, upbeat conversational energy", "Friendly & Lively"),
  GeminiVoiceOption("Charon", "Charon", "Deliberate, grounded, and measured cadence", "Calm & Measured")
)

sealed interface SpeechSynthesisResult {
  data class Success(
    val audioData: ByteArray,
    val mimeType: String,
    val modelName: String,
    val voiceName: String
  ) : SpeechSynthesisResult
  data class MissingApiKey(val message: String) : SpeechSynthesisResult
  data class RateLimited(val message: String) : SpeechSynthesisResult
  data class Unsupported(val message: String) : SpeechSynthesisResult
  data class Error(val message: String) : SpeechSynthesisResult
}
