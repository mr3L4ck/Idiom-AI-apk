package com.example.data

/**
 * Centralized branding configuration for the application.
 *
 * All user-visible brand names, tutor titles, voice feature names, and taglines
 * are defined here in one single place so that any future changes can be made
 * seamlessly without hunting through individual composables.
 */
object BrandConfig {
  /** The application name */
  const val APP_NAME = "Idiom AI"

  /** The AI tutor's user-visible identity */
  const val AI_TUTOR_NAME = "Idiom AI"

  /** The voice feature's user-visible label */
  const val VOICE_FEATURE_NAME = "Idiom Voice"

  /** Brand tagline */
  const val TAGLINE = "Learn naturally. Speak confidently."

  /** Phonetic pronunciation guide */
  const val PRONUNCIATION_GUIDE = "uh-VEN-lee"

  /** Status label when service is ready */
  const val AI_STATUS_READY = "AI Ready"

  /** Technical provider disclosure retained in privacy & technical notes */
  const val TECHNICAL_PROVIDER_DISCLOSURE = "Cloud AI and neural speech services are provided via Google API services."
}
