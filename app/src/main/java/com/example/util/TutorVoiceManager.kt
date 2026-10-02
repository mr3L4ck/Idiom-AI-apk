package com.example.util

import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.example.data.GeminiService
import com.example.data.Language
import com.example.data.SpeechSynthesisResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class TutorVoiceManager(
  private val context: Context,
  private val coroutineScope: CoroutineScope,
  private val ttsHelper: TextToSpeechHelper,
  private val geminiService: GeminiService = GeminiService()
) {
  private var mediaPlayer: MediaPlayer? = null
  private var currentPlaybackJob: Job? = null
  private var currentCachedAudioFile: File? = null

  private val _isPlaying = mutableStateOf(false)
  val isPlaying: State<Boolean> = _isPlaying

  private val _isBuffering = mutableStateOf(false)
  val isBuffering: State<Boolean> = _isBuffering

  private val _playingMessageId = mutableStateOf<String?>(null)
  val playingMessageId: State<String?> = _playingMessageId

  private val _activeEngineBadge = mutableStateOf<String?>(null)
  val activeEngineBadge: State<String?> = _activeEngineBadge

  private val _playbackNotice = mutableStateOf<String?>(null)
  val playbackNotice: State<String?> = _playbackNotice

  fun isApiKeyConfigured(): Boolean {
    return geminiService.isApiKeyConfigured()
  }

  fun playTutorResponse(
    messageId: String,
    text: String,
    targetLanguage: Language,
    engineMode: String = "gemini_neural",
    geminiVoice: String = "Kore",
    speechRate: Float = 1.0f
  ) {
    // If tapping the already-playing message, stop playback (pause/stop toggle)
    if (_isPlaying.value && _playingMessageId.value == messageId) {
      stop()
      return
    }

    // Stop any existing playback first to prevent overlapping audio
    stop()

    _playingMessageId.value = messageId
    _playbackNotice.value = null

    if (engineMode == "gemini_neural" && geminiService.isApiKeyConfigured()) {
      // Use Gemini Neural Voice via REST TTS
      _isBuffering.value = true
      currentPlaybackJob = coroutineScope.launch {
        val result = geminiService.generateTutorSpeech(
          text = text,
          targetLanguage = targetLanguage,
          voiceName = geminiVoice
        )

        withContext(Dispatchers.Main) {
          _isBuffering.value = false
          when (result) {
            is SpeechSynthesisResult.Success -> {
              playWavAudio(result.audioData, speechRate, "${com.example.data.BrandConfig.VOICE_FEATURE_NAME} ($geminiVoice)")
            }
            is SpeechSynthesisResult.RateLimited -> {
              _playbackNotice.value = "${com.example.data.BrandConfig.VOICE_FEATURE_NAME} quota limit reached. Falling back to device speech engine."
              fallbackToDeviceTts(text, targetLanguage.id, speechRate)
            }
            is SpeechSynthesisResult.MissingApiKey -> {
              _playbackNotice.value = "API key required for ${com.example.data.BrandConfig.VOICE_FEATURE_NAME}. Using on-device voice engine."
              fallbackToDeviceTts(text, targetLanguage.id, speechRate)
            }
            is SpeechSynthesisResult.Unsupported, is SpeechSynthesisResult.Error -> {
              _playbackNotice.value = "${com.example.data.BrandConfig.VOICE_FEATURE_NAME} unavailable. Falling back to device speech engine."
              fallbackToDeviceTts(text, targetLanguage.id, speechRate)
            }
          }
        }
      }
    } else {
      // Local Android System TextToSpeech fallback
      if (engineMode == "gemini_neural") {
        _playbackNotice.value = "Configure AI API key in the Secrets panel to activate ${com.example.data.BrandConfig.VOICE_FEATURE_NAME}. Using on-device voice."
      }
      fallbackToDeviceTts(text, targetLanguage.id, speechRate)
    }
  }

  private fun fallbackToDeviceTts(text: String, languageCode: String, speechRate: Float) {
    _activeEngineBadge.value = "Android System TTS"
    ttsHelper.speak(text, languageCode, speechRate)
    _isPlaying.value = true
  }

  private fun playWavAudio(wavBytes: ByteArray, speechRate: Float, engineName: String) {
    try {
      // Clean up previous temp file
      deleteTempAudioFile()

      val audioFile = PcmWavUtils.writeWavToCache(context, wavBytes, "tutor_speech")
      currentCachedAudioFile = audioFile

      val player = MediaPlayer().apply {
        setDataSource(audioFile.absolutePath)
        prepare()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
          try {
            playbackParams = playbackParams.setSpeed(speechRate)
          } catch (_: Exception) {
            // Safe ignore if unsupported by platform audio codec
          }
        }

        setOnCompletionListener {
          _isPlaying.value = false
          _playingMessageId.value = null
          deleteTempAudioFile()
        }

        setOnErrorListener { _, _, _ ->
          _isPlaying.value = false
          _playingMessageId.value = null
          deleteTempAudioFile()
          true
        }
      }

      mediaPlayer = player
      player.start()
      _isPlaying.value = true
      _activeEngineBadge.value = engineName
    } catch (_: Exception) {
      _isPlaying.value = false
      _playingMessageId.value = null
      deleteTempAudioFile()
    }
  }

  fun previewVoice(
    sampleText: String,
    targetLanguage: Language,
    engineMode: String,
    geminiVoice: String,
    speechRate: Float
  ) {
    playTutorResponse(
      messageId = "preview_voice",
      text = sampleText,
      targetLanguage = targetLanguage,
      engineMode = engineMode,
      geminiVoice = geminiVoice,
      speechRate = speechRate
    )
  }

  fun stop() {
    currentPlaybackJob?.cancel()
    currentPlaybackJob = null
    _isBuffering.value = false

    try {
      mediaPlayer?.let { player ->
        if (player.isPlaying) {
          player.stop()
        }
        player.reset()
        player.release()
      }
      mediaPlayer = null
    } catch (_: Exception) {
      mediaPlayer = null
    }

    ttsHelper.stop()
    _isPlaying.value = false
    _playingMessageId.value = null
    deleteTempAudioFile()
  }

  private fun deleteTempAudioFile() {
    try {
      currentCachedAudioFile?.let { file ->
        if (file.exists()) {
          file.delete()
        }
      }
      currentCachedAudioFile = null
    } catch (_: Exception) {
      // Safe ignore
    }
  }

  fun release() {
    stop()
  }
}

@Composable
fun rememberTutorVoiceManager(
  ttsHelper: TextToSpeechHelper
): TutorVoiceManager {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
  val manager = remember(context, ttsHelper) {
    TutorVoiceManager(
      context = context.applicationContext,
      coroutineScope = coroutineScope,
      ttsHelper = ttsHelper
    )
  }

  DisposableEffect(manager) {
    onDispose {
      manager.release()
    }
  }

  return manager
}
