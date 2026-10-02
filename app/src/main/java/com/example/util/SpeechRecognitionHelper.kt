package com.example.util

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

enum class SpeechState {
  IDLE,
  PREPARING,
  LISTENING,
  PROCESSING,
  SUCCESS,
  ERROR
}

class SpeechRecognitionHelper(private val context: Context) {

  private var speechRecognizer: SpeechRecognizer? = null

  private val _state = mutableStateOf(SpeechState.IDLE)
  val state: State<SpeechState> = _state

  private val _recognizedText = mutableStateOf("")
  val recognizedText: State<String> = _recognizedText

  private val _partialText = mutableStateOf("")
  val partialText: State<String> = _partialText

  private val _errorMessage = mutableStateOf<String?>(null)
  val errorMessage: State<String?> = _errorMessage

  private val _rmsLevel = mutableFloatStateOf(0f)
  val rmsLevel: State<Float> = _rmsLevel

  val isAvailable: Boolean by lazy {
    SpeechRecognizer.isRecognitionAvailable(context)
  }

  fun getSpeechLanguageTag(languageCode: String): String {
    return when (languageCode.lowercase()) {
      "es" -> "es-ES"
      "fr" -> "fr-FR"
      "de" -> "de-DE"
      "it" -> "it-IT"
      "pt" -> "pt-BR"
      "ja" -> "ja-JP"
      "ko" -> "ko-KR"
      "zh" -> "zh-CN"
      "hi" -> "hi-IN"
      "ar" -> "ar-SA"
      "hy" -> "hy-AM"
      "en" -> "en-US"
      else -> languageCode
    }
  }

  private val recognitionListener = object : RecognitionListener {
    override fun onReadyForSpeech(params: Bundle?) {
      _state.value = SpeechState.LISTENING
      _errorMessage.value = null
    }

    override fun onBeginningOfSpeech() {
      _state.value = SpeechState.LISTENING
    }

    override fun onRmsChanged(rmsdB: Float) {
      // Normalize RMS dB (typically -2 to 10 dB) to 0.0f - 1.0f range for animations
      val normalized = ((rmsdB + 2f) / 12f).coerceIn(0f, 1f)
      _rmsLevel.floatValue = normalized
    }

    override fun onBufferReceived(buffer: ByteArray?) {
      // No-op
    }

    override fun onEndOfSpeech() {
      _state.value = SpeechState.PROCESSING
      _rmsLevel.floatValue = 0f
    }

    override fun onError(error: Int) {
      _rmsLevel.floatValue = 0f
      _state.value = SpeechState.ERROR
      _errorMessage.value = getReadableErrorMessage(error)
    }

    override fun onResults(results: Bundle?) {
      _rmsLevel.floatValue = 0f
      val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
      if (!matches.isNullOrEmpty()) {
        _recognizedText.value = matches[0].trim()
        _partialText.value = ""
        _state.value = SpeechState.SUCCESS
      } else {
        _state.value = SpeechState.ERROR
        _errorMessage.value = "No speech was detected. Please try speaking again."
      }
    }

    override fun onPartialResults(partialResults: Bundle?) {
      val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
      if (!matches.isNullOrEmpty()) {
        _partialText.value = matches[0]
      }
    }

    override fun onEvent(eventType: Int, params: Bundle?) {
      // No-op
    }
  }

  fun startListening(languageCode: String) {
    if (!isAvailable) {
      _state.value = SpeechState.ERROR
      _errorMessage.value = "Speech recognition is not available on this device. Please ensure Google Speech Services or an engine is installed."
      return
    }

    try {
      stopListening()
      _state.value = SpeechState.PREPARING
      _recognizedText.value = ""
      _partialText.value = ""
      _errorMessage.value = null
      _rmsLevel.floatValue = 0f

      if (speechRecognizer == null) {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context.applicationContext)
        speechRecognizer?.setRecognitionListener(recognitionListener)
      }

      val languageTag = getSpeechLanguageTag(languageCode)
      val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageTag)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, languageTag)
        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
      }

      speechRecognizer?.startListening(intent)
    } catch (e: Exception) {
      _state.value = SpeechState.ERROR
      _errorMessage.value = "Failed to initialize microphone: ${e.localizedMessage ?: "Unknown error"}"
    }
  }

  fun stopListening() {
    try {
      speechRecognizer?.stopListening()
      _rmsLevel.floatValue = 0f
      if (_state.value == SpeechState.LISTENING) {
        _state.value = SpeechState.PROCESSING
      }
    } catch (_: Exception) {
      // Safe ignore
    }
  }

  fun cancel() {
    try {
      speechRecognizer?.cancel()
      _rmsLevel.floatValue = 0f
      _state.value = SpeechState.IDLE
    } catch (_: Exception) {
      // Safe ignore
    }
  }

  fun reset() {
    cancel()
    _recognizedText.value = ""
    _partialText.value = ""
    _errorMessage.value = null
    _state.value = SpeechState.IDLE
  }

  fun destroy() {
    try {
      cancel()
      speechRecognizer?.destroy()
      speechRecognizer = null
      _state.value = SpeechState.IDLE
      _rmsLevel.floatValue = 0f
    } catch (_: Exception) {
      // Safe ignore
    }
  }

  companion object {
    fun getReadableErrorMessage(errorCode: Int): String {
      return when (errorCode) {
        SpeechRecognizer.ERROR_AUDIO ->
          "Microphone audio recording issue. Please check microphone settings."
        SpeechRecognizer.ERROR_CLIENT ->
          "Speech recognizer client error. Please tap to try again."
        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS ->
          "Microphone permission is required to practice speaking."
        SpeechRecognizer.ERROR_NETWORK ->
          "Network connection required for online speech recognition."
        SpeechRecognizer.ERROR_NETWORK_TIMEOUT ->
          "Network connection timed out. Please check your connection and retry."
        SpeechRecognizer.ERROR_NO_MATCH ->
          "No matching speech detected. Please speak clearly into the microphone."
        SpeechRecognizer.ERROR_RECOGNIZER_BUSY ->
          "Speech recognition engine is busy. Please wait a moment and retry."
        SpeechRecognizer.ERROR_SERVER ->
          "Recognition server error. Please try again in a few moments."
        SpeechRecognizer.ERROR_SPEECH_TIMEOUT ->
          "No speech heard before timeout. Please tap the mic and start speaking."
        13 -> // SpeechRecognizer.ERROR_LANGUAGE_NOT_SUPPORTED in API 33+
          "The selected language is not supported by your device speech recognizer."
        14 -> // SpeechRecognizer.ERROR_LANGUAGE_UNAVAILABLE in API 33+
          "Language pack is not installed. You may install it in device speech settings."
        15 -> // SpeechRecognizer.ERROR_SERVER_DISCONNECTED in API 34+
          "Recognition server disconnected. Please retry."
        16 -> // SpeechRecognizer.ERROR_CANNOT_LISTEN_TO_DOWNLOAD_EVENTS in API 34+
          "Speech engine download event error. Please retry."
        else ->
          "Speech recognition encountered an issue (code $errorCode). Please tap to try again."
      }
    }
  }
}

@Composable
fun rememberSpeechRecognitionHelper(): SpeechRecognitionHelper {
  val context = LocalContext.current
  val helper = remember { SpeechRecognitionHelper(context) }

  DisposableEffect(helper) {
    onDispose {
      helper.destroy()
    }
  }

  return helper
}
