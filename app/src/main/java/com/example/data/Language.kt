package com.example.data

/**
 * Granular capability disclosures for a language.
 * Ensures the app never misleads learners about what speech or AI features are actually supported.
 */
enum class TtsCapabilityLevel(val label: String, val description: String) {
  NEURAL_AND_DEVICE("Idiom Voice + System", "Expressive neural voice synthesis and on-device offline voice"),
  DEVICE_ONLY("System Voice Only", "On-device system speech synthesis"),
  NOT_SUPPORTED("Text Only", "Speech synthesis currently not available for this language")
}

data class LanguageCapabilities(
  val interfaceTranslation: Boolean = false,
  val aiTextTutoring: Boolean = true, // High quality multilingual generation via Gemini 3.5 Flash
  val ttsLevel: TtsCapabilityLevel = TtsCapabilityLevel.DEVICE_ONLY,
  val speechRecognition: Boolean = true,
  val pronunciationFeedback: Boolean = true,
  val voiceConversation: Boolean = true
) {
  val hasSpeechOutput: Boolean get() = ttsLevel != TtsCapabilityLevel.NOT_SUPPORTED
  val hasNeuralVoice: Boolean get() = ttsLevel == TtsCapabilityLevel.NEURAL_AND_DEVICE
}

/**
 * Data model for a world language in the 200+ language system.
 * Uses standard BCP 47 language identifiers, script classifications, and honest capability metadata.
 */
data class Language(
  val id: String, // BCP 47 code (e.g. "en", "es", "bn", "hy", "ar")
  val name: String,
  val nativeName: String,
  val flagEmoji: String,
  val region: String,
  val speakers: String,
  val difficulty: String = "Moderate",
  val sampleGreeting: String = "Hello",
  val greetingMeaning: String = "Hello / Greetings",
  val isPopular: Boolean = false,
  val scriptType: String = "Latin",
  val isRtl: Boolean = false,
  val bcp47: String = id,
  val capabilities: LanguageCapabilities = LanguageCapabilities()
)
