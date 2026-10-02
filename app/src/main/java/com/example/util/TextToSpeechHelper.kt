package com.example.util

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

class TextToSpeechHelper(context: Context) {
  private var textToSpeech: TextToSpeech? = null
  private val _isReady = mutableStateOf(false)
  val isReady: State<Boolean> = _isReady

  private val _isSpeaking = mutableStateOf(false)
  val isSpeaking: State<Boolean> = _isSpeaking

  init {
    try {
      textToSpeech = TextToSpeech(context.applicationContext) { status ->
        if (status == TextToSpeech.SUCCESS) {
          _isReady.value = true
        }
      }
      textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
        override fun onStart(utteranceId: String?) {
          _isSpeaking.value = true
        }

        override fun onDone(utteranceId: String?) {
          _isSpeaking.value = false
        }

        override fun onError(utteranceId: String?) {
          _isSpeaking.value = false
        }
      })
    } catch (_: Exception) {
      _isReady.value = false
    }
  }

  private val _speechRate = mutableStateOf(1.0f)
  val speechRate: State<Float> = _speechRate

  private val _voiceStatusMessage = mutableStateOf<String?>(null)
  val voiceStatusMessage: State<String?> = _voiceStatusMessage

  fun getLocaleForLanguage(languageCode: String): Locale {
    return when (languageCode.lowercase()) {
      "es" -> Locale.forLanguageTag("es-ES")
      "fr" -> Locale.FRENCH
      "de" -> Locale.GERMAN
      "it" -> Locale.ITALIAN
      "pt" -> Locale.forLanguageTag("pt-BR")
      "ja" -> Locale.JAPANESE
      "ko" -> Locale.KOREAN
      "zh" -> Locale.CHINESE
      "hi" -> Locale.forLanguageTag("hi-IN")
      "ar" -> Locale.forLanguageTag("ar-SA")
      "hy" -> Locale.forLanguageTag("hy-AM")
      "en" -> Locale.ENGLISH
      else -> Locale.forLanguageTag(languageCode)
    }
  }

  fun isLanguageSupported(languageCode: String): Boolean {
    val tts = textToSpeech ?: return false
    val locale = getLocaleForLanguage(languageCode)
    val availability = tts.isLanguageAvailable(locale)
    return availability == TextToSpeech.LANG_AVAILABLE ||
      availability == TextToSpeech.LANG_COUNTRY_AVAILABLE ||
      availability == TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE
  }

  fun setSpeechRate(rate: Float) {
    _speechRate.value = rate
    try {
      textToSpeech?.setSpeechRate(rate)
    } catch (_: Exception) {
      // Safe ignore
    }
  }

  fun speak(text: String, languageCode: String = "es", rate: Float? = null) {
    val tts = textToSpeech ?: return
    if (!_isReady.value) return

    try {
      val targetRate = rate ?: _speechRate.value
      tts.setSpeechRate(targetRate)

      val locale = getLocaleForLanguage(languageCode)
      val availability = tts.isLanguageAvailable(locale)

      if (availability == TextToSpeech.LANG_AVAILABLE ||
        availability == TextToSpeech.LANG_COUNTRY_AVAILABLE ||
        availability == TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE
      ) {
        tts.language = locale
        _voiceStatusMessage.value = null
      } else {
        // Fallback to default language if specific locale not present
        tts.language = Locale.getDefault()
        _voiceStatusMessage.value = "Selected accent voice for $languageCode is not installed on this device. Using system default voice."
      }

      tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "utterance_${System.currentTimeMillis()}")
    } catch (_: Exception) {
      _isSpeaking.value = false
    }
  }

  fun stop() {
    try {
      textToSpeech?.stop()
      _isSpeaking.value = false
    } catch (_: Exception) {
      // Safe ignore
    }
  }

  fun shutdown() {
    try {
      textToSpeech?.stop()
      textToSpeech?.shutdown()
      textToSpeech = null
      _isReady.value = false
      _isSpeaking.value = false
    } catch (_: Exception) {
      // Safe ignore
    }
  }
}

@Composable
fun rememberTextToSpeechHelper(): TextToSpeechHelper {
  val context = LocalContext.current
  val helper = remember { TextToSpeechHelper(context) }

  DisposableEffect(helper) {
    onDispose {
      helper.shutdown()
    }
  }

  return helper
}
