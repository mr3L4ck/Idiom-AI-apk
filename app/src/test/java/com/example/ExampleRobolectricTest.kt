package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.AiTutorRepository
import com.example.data.ChatMessage
import com.example.data.CourseCatalog
import com.example.data.GeminiService
import com.example.data.LanguageCatalog
import com.example.data.LessonCatalog
import com.example.data.MessageSender
import com.example.data.TutorLevel
import com.example.data.UserPreferencesRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals(com.example.data.BrandConfig.APP_NAME, appName)
    assertEquals("Idiom AI", appName)
  }

  @Test
  fun `brand configuration constants are properly defined`() {
    assertEquals("Idiom AI", com.example.data.BrandConfig.APP_NAME)
    assertEquals("Idiom AI", com.example.data.BrandConfig.AI_TUTOR_NAME)
    assertEquals("Idiom Voice", com.example.data.BrandConfig.VOICE_FEATURE_NAME)
    assertEquals("AI Ready", com.example.data.BrandConfig.AI_STATUS_READY)
    assertTrue(com.example.data.BrandConfig.TAGLINE.isNotBlank())
    assertTrue(com.example.data.BrandConfig.PRONUNCIATION_GUIDE.isNotBlank())
    assertTrue(com.example.data.BrandConfig.TECHNICAL_PROVIDER_DISCLOSURE.isNotBlank())
  }

  @Test
  fun `design tokens contain valid non-zero dimensions`() {
    assertTrue(com.example.ui.theme.DesignTokens.Spacing8.value > 0)
    assertTrue(com.example.ui.theme.DesignTokens.MinTouchTarget.value >= 48f)
    assertTrue(com.example.ui.theme.DesignTokens.MaxContentWidth.value >= 600f)
  }

  @Test
  fun `language catalog contains required languages`() {
    val requiredIds = listOf("en", "es", "fr", "de", "ja", "ko", "ar", "hi", "zh", "hy", "pt")
    requiredIds.forEach { id ->
      val language = LanguageCatalog.getLanguageById(id)
      assertNotNull("Expected language $id in catalog", language)
      assertEquals(id, language.id)
    }
  }

  @Test
  fun `sample lesson exists for languages`() {
    val lesson = LessonCatalog.getSampleLessonForLanguage("es")
    assertNotNull(lesson)
    assertTrue(lesson.phrases.isNotEmpty())
  }

  @Test
  fun `spanish course contains everyday greetings with verified vocabulary and questions`() {
    val course = CourseCatalog.getCourseForLanguage("es")
    assertNotNull("Spanish course should be present", course)
    val unit1 = course!!.units.firstOrNull()
    assertNotNull("Unit 1 should exist", unit1)
    val lesson1 = unit1!!.lessons.firstOrNull()
    assertNotNull("Everyday Greetings lesson should exist", lesson1)
    assertEquals("Everyday Greetings", lesson1!!.title)
    assertEquals(5, lesson1.vocabulary.size)
    assertTrue("Should have at least 5 questions", lesson1.questions.size >= 5)
  }

  @Test
  fun `unsupported course returns null for transparent coming-soon handling`() {
    val course = CourseCatalog.getCourseForLanguage("fr")
    assertNull("French course should be null in initial phase to prevent fake content", course)
  }

  @Test
  fun `recordCourseLessonCompletion saves score and marks completed`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repo = UserPreferencesRepository(context)
    repo.recordCourseLessonCompletion(
      lessonId = "es_u1_l1",
      scorePercent = 100,
      correctCount = 6,
      totalQuestions = 6,
      minutesSpent = 5,
      xpEarned = 30
    )

    val prefs = repo.preferences.value
    assertTrue("Lesson should be marked completed", "es_u1_l1" in prefs.completedLessons)
    assertEquals(100, prefs.lessonScores["es_u1_l1"])
  }

  @Test
  fun `ai tutor repository persists messages and level`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repo = AiTutorRepository(context)
    val testMsg = ChatMessage(
      id = "test_1",
      sender = MessageSender.USER,
      text = "¡Hola!",
      targetLanguageId = "es"
    )

    repo.saveMessages("es", listOf(testMsg))
    val loaded = repo.loadMessages("es")
    assertEquals(1, loaded.size)
    assertEquals("¡Hola!", loaded.first().text)

    repo.setTutorLevel(TutorLevel.INTERMEDIATE)
    assertEquals(TutorLevel.INTERMEDIATE, repo.getTutorLevel())

    repo.clearMessages("es")
    assertTrue(repo.loadMessages("es").isEmpty())
  }

  @Test
  fun `gemini service detects placeholder key without crashing`() {
    val service = GeminiService()
    // In test environment, GEMINI_API_KEY is either placeholder or empty
    assertFalse(service.isApiKeyConfigured())
  }

  @Test
  fun `voice learning catalog provides speaking phrases and listening exercises`() {
    val supportedLangs = listOf("es", "fr", "de", "ja", "ko", "pt", "it", "ar")
    supportedLangs.forEach { langId ->
      val speakingPhrases = com.example.data.VoiceLearningCatalog.getSpeakingPhrases(langId)
      assertTrue("Speaking phrases should not be empty for $langId", speakingPhrases.isNotEmpty())
      speakingPhrases.forEach { phrase ->
        assertTrue("Phrase original should not be empty", phrase.original.isNotBlank())
        assertTrue("Phrase translation should not be empty", phrase.translation.isNotBlank())
      }

      val listeningExercises = com.example.data.VoiceLearningCatalog.getListeningExercises(langId)
      assertTrue("Listening exercises should not be empty for $langId", listeningExercises.isNotEmpty())
      listeningExercises.forEach { ex ->
        assertTrue("Exercise prompt should not be empty", ex.promptQuestion.isNotBlank())
        assertTrue("Exercise should have options", ex.options.isNotEmpty())
        assertTrue("Correct index should be within bounds", ex.correctOptionIndex in ex.options.indices)
      }
    }
  }

  @Test
  fun `pronunciation evaluator detects exact match with punctuation normalization`() {
    val expected = "¡Hola! ¿Cómo estás?"
    val recognizedExact = "Hola, cómo estás"
    val eval = com.example.data.PronunciationEvaluator.evaluate(expected, recognizedExact)

    assertTrue("Should detect exact or 100% word match", eval.isExactMatch || eval.matchPercentage == 100)
    assertEquals(3, eval.matchedWordCount)
    assertEquals(3, eval.totalWordCount)
    assertTrue("Should include disclaimer", eval.disclaimerText.isNotBlank())
  }

  @Test
  fun `pronunciation evaluator handles partial and approximate matches`() {
    val expected = "Buenos días, que tengas un buen día"
    val recognizedApproximate = "Buenos días que tengas buen día"
    val eval = com.example.data.PronunciationEvaluator.evaluate(expected, recognizedApproximate)

    assertTrue("Match percentage should be above 80%", eval.matchPercentage >= 80)
    assertTrue("Approximate or exact match should be true", eval.isApproximateMatch || eval.isExactMatch)
    assertTrue("Feedback summary should be encouraging", eval.feedbackSummary.isNotBlank())
  }

  @Test
  fun `speech recognition helper maps errors gracefully without crash`() {
    val errorCodes = listOf(
      android.speech.SpeechRecognizer.ERROR_AUDIO,
      android.speech.SpeechRecognizer.ERROR_CLIENT,
      android.speech.SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS,
      android.speech.SpeechRecognizer.ERROR_NETWORK,
      android.speech.SpeechRecognizer.ERROR_NETWORK_TIMEOUT,
      android.speech.SpeechRecognizer.ERROR_NO_MATCH,
      android.speech.SpeechRecognizer.ERROR_RECOGNIZER_BUSY,
      android.speech.SpeechRecognizer.ERROR_SERVER,
      android.speech.SpeechRecognizer.ERROR_SPEECH_TIMEOUT,
      13, // ERROR_LANGUAGE_NOT_SUPPORTED
      999 // Unknown fallback
    )

    errorCodes.forEach { code ->
      val msg = com.example.util.SpeechRecognitionHelper.getReadableErrorMessage(code)
      assertNotNull("Error message should not be null for code $code", msg)
      assertTrue("Error message should be non-empty", msg.isNotBlank())
    }
  }

  @Test
  fun `recordPracticeSession updates daily minutes and xp`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repo = com.example.data.UserPreferencesRepository(context)

    val initialMinutes = repo.preferences.value.currentDailyMinutes
    val initialXp = repo.preferences.value.xpPoints

    repo.recordPracticeSession(minutesSpent = 4, xpEarned = 25)

    assertEquals(initialMinutes + 4, repo.preferences.value.currentDailyMinutes)
    assertEquals(initialXp + 25, repo.preferences.value.xpPoints)
  }

  @Test
  fun `pcm wav utils creates valid RIFF-WAVE header for raw pcm`() {
    val rawPcm = ByteArray(100) { 0 }
    val wavBytes = com.example.util.PcmWavUtils.ensureWavBytes(rawPcm, sampleRate = 24000)

    assertEquals(144, wavBytes.size) // 44 bytes header + 100 bytes PCM
    assertEquals('R'.code.toByte(), wavBytes[0])
    assertEquals('I'.code.toByte(), wavBytes[1])
    assertEquals('F'.code.toByte(), wavBytes[2])
    assertEquals('F'.code.toByte(), wavBytes[3])
    assertEquals('W'.code.toByte(), wavBytes[8])
    assertEquals('A'.code.toByte(), wavBytes[9])
    assertEquals('V'.code.toByte(), wavBytes[10])
    assertEquals('E'.code.toByte(), wavBytes[11])
  }

  @Test
  fun `pcm wav utils preserves intact WAV bytes`() {
    val existingWav = ByteArray(50) { 0 }
    existingWav[0] = 'R'.code.toByte()
    existingWav[1] = 'I'.code.toByte()
    existingWav[2] = 'F'.code.toByte()
    existingWav[3] = 'F'.code.toByte()

    val result = com.example.util.PcmWavUtils.ensureWavBytes(existingWav)
    assertEquals(50, result.size)
    assertEquals('R'.code.toByte(), result[0])
  }

  @Test
  fun `user preferences persists tutor voice engine persona and speed`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repo = com.example.data.UserPreferencesRepository(context)

    repo.setTutorVoiceSettings(
      engineMode = "gemini_neural",
      geminiVoice = "Fenrir",
      speechRate = 0.75f,
      autoPlay = true
    )

    val prefs = repo.preferences.value
    assertEquals("gemini_neural", prefs.tutorVoiceEngine)
    assertEquals("Fenrir", prefs.tutorGeminiVoice)
    assertEquals(0.75f, prefs.tutorSpeechRate, 0.01f)
    assertTrue("Auto-play should be true", prefs.autoPlayTutorAudio)
  }

  @Test
  fun `gemini voices list contains verified tutor personas`() {
    val voices = com.example.data.AVAILABLE_GEMINI_VOICES
    assertTrue("Should have 5 verified prebuilt voices", voices.size >= 5)
    val names = voices.map { it.voiceName }
    assertTrue("Should include Kore", "Kore" in names)
    assertTrue("Should include Aoede", "Aoede" in names)
    assertTrue("Should include Fenrir", "Fenrir" in names)
    assertTrue("Should include Puck", "Puck" in names)
    assertTrue("Should include Charon", "Charon" in names)
  }

  @Test
  fun `gemini service handles empty api key gracefully for speech synthesis`() = kotlinx.coroutines.runBlocking {
    val service = com.example.data.GeminiService()
    val targetLang = com.example.data.LanguageCatalog.getLanguageById("es")
    val result = service.generateTutorSpeech("¡Hola!", targetLang, "Kore")

    if (!service.isApiKeyConfigured()) {
      assertTrue(
        "Expected MissingApiKey result when API key is not configured",
        result is com.example.data.SpeechSynthesisResult.MissingApiKey
      )
    }
  }

  @Test
  fun `user profile full updates name bio avatar level and country`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repo = com.example.data.UserPreferencesRepository(context)

    repo.updateProfileFull(
      name = "Elena Rostova",
      bio = "Passionate language explorer",
      avatar = "🚀",
      level = "B2",
      country = "Spain",
      photoUri = ""
    )

    val prefs = repo.preferences.value
    assertEquals("Elena Rostova", prefs.userName)
    assertEquals("Passionate language explorer", prefs.userBio)
    assertEquals("🚀", prefs.userAvatar)
    assertEquals("B2", prefs.userProficiencyLevel)
    assertEquals("Spain", prefs.userCountry)
  }

  @Test
  fun `profile photo uri saving and removal`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repo = com.example.data.UserPreferencesRepository(context)

    repo.setProfilePhotoUri("file:///data/user/0/com.example/files/test.jpg")
    assertEquals("file:///data/user/0/com.example/files/test.jpg", repo.preferences.value.userProfilePhotoUri)

    repo.removeProfilePhotoWithFile(context)
    assertEquals("", repo.preferences.value.userProfilePhotoUri)
  }

  @Test
  fun `learning score calculation reflects verified activities without fabrication`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repo = com.example.data.UserPreferencesRepository(context)

    // Baseline calculation with defaults
    val initialScore = repo.calculateScore()
    assertTrue("Score should be non-negative", initialScore >= 0)

    // Complete lesson and practice
    repo.recordCourseLessonCompletion("test_l1", scorePercent = 100, correctCount = 5, totalQuestions = 5)
    repo.recordSpeakingCompleted()
    repo.recordListeningCompleted()

    val updatedScore = repo.calculateScore()
    assertTrue("Score should increase after completed activities", updatedScore > initialScore)
  }

  @Test
  fun `milestone achievements unlock properly and include week warrior`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repo = com.example.data.UserPreferencesRepository(context)

    val achievements = repo.getAchievements(repo.preferences.value)
    assertTrue("Achievements list should not be empty", achievements.isNotEmpty())
    val milestoneIds = achievements.map { it.id }
    assertTrue("Should include first_lesson", "first_lesson" in milestoneIds)
    assertTrue("Should include week_warrior", "week_warrior" in milestoneIds)
    assertTrue("Should include curriculum_climber", "curriculum_climber" in milestoneIds)
    assertTrue("Should include word_collector", "word_collector" in milestoneIds)
  }

  @Test
  fun `theme mode persists across repository reload`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repo1 = com.example.data.UserPreferencesRepository(context)
    repo1.setThemeMode("LIGHT")

    val repo2 = com.example.data.UserPreferencesRepository(context)
    assertEquals("LIGHT", repo2.preferences.value.themeMode)
  }

  @Test
  fun `json export outputs valid formatted json containing all user fields`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repo = com.example.data.UserPreferencesRepository(context)

    val jsonString = repo.exportUserDataJson()
    assertTrue("JSON string should not be blank", jsonString.isNotBlank())
    val jsonObject = org.json.JSONObject(jsonString)
    assertEquals("Idiom AI", jsonObject.getString("application"))
    assertTrue(jsonObject.has("profile"))
    assertTrue(jsonObject.has("goals"))
    assertTrue(jsonObject.has("learning_stats"))
  }
}
