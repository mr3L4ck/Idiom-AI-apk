package com.example.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

data class UserPreferences(
  val nativeLanguageId: String = "en",
  val learningLanguageId: String = "es",
  val dailyGoalMinutes: Int = 15,
  val currentDailyMinutes: Int = 8,
  val streakDays: Int = 3,
  val wordsLearned: Int = 34,
  val xpPoints: Int = 180,
  val isOnboardingCompleted: Boolean = false,
  val completedLessons: Set<String> = emptySet(),
  val lessonScores: Map<String, Int> = emptyMap(),
  val tutorVoiceEngine: String = "gemini_neural",
  val tutorGeminiVoice: String = "Kore",
  val tutorSpeechRate: Float = 1.0f,
  val autoPlayTutorAudio: Boolean = false,

  // Personal Profile fields
  val userName: String = "Language Learner",
  val userBio: String = "Learning world languages one conversation at a time.",
  val userAvatar: String = "🎓",
  val userProfilePhotoUri: String = "",
  val userCountry: String = "Global",
  val userProficiencyLevel: String = "A1",
  val themeMode: String = "SYSTEM", // "SYSTEM", "LIGHT", "DARK"

  // Language management
  val favoriteLanguages: Set<String> = emptySet(),
  val recentLanguages: List<String> = listOf("es", "en"),

  // Learning Goals & Reminder
  val weeklyGoalDays: Int = 5,
  val studyReminderEnabled: Boolean = false,
  val studyReminderTime: String = "19:00",

  // Recorded Milestones
  val hasCompletedSpeaking: Boolean = false,
  val hasCompletedListening: Boolean = false
)

data class MilestoneAchievement(
  val id: String,
  val title: String,
  val description: String,
  val emoji: String,
  val isUnlocked: Boolean,
  val progressText: String
)

data class CountryInfo(
  val code: String,
  val name: String,
  val flag: String,
  val region: String
)

val POPULAR_COUNTRIES: List<CountryInfo> = listOf(
  CountryInfo("GLOBAL", "Global / Worldwide", "🌍", "Global"),
  CountryInfo("US", "United States", "🇺🇸", "Americas"),
  CountryInfo("GB", "United Kingdom", "🇬🇧", "Europe"),
  CountryInfo("CA", "Canada", "🇨🇦", "Americas"),
  CountryInfo("AU", "Australia", "🇦🇺", "Oceania"),
  CountryInfo("ES", "Spain", "🇪🇸", "Europe"),
  CountryInfo("MX", "Mexico", "🇲🇽", "Americas"),
  CountryInfo("AR", "Argentina", "🇦🇷", "Americas"),
  CountryInfo("CO", "Colombia", "🇨🇴", "Americas"),
  CountryInfo("FR", "France", "🇫🇷", "Europe"),
  CountryInfo("DE", "Germany", "🇩🇪", "Europe"),
  CountryInfo("IT", "Italy", "🇮🇹", "Europe"),
  CountryInfo("BR", "Brazil", "🇧🇷", "Americas"),
  CountryInfo("PT", "Portugal", "🇵🇹", "Europe"),
  CountryInfo("AM", "Armenia", "🇦🇲", "Asia / Caucasus"),
  CountryInfo("BD", "Bangladesh", "🇧🇩", "Asia"),
  CountryInfo("IN", "India", "🇮🇳", "Asia"),
  CountryInfo("JP", "Japan", "🇯🇵", "Asia"),
  CountryInfo("KR", "South Korea", "🇰🇷", "Asia"),
  CountryInfo("CN", "China", "🇨🇳", "Asia"),
  CountryInfo("EG", "Egypt", "🇪🇬", "Africa / Middle East"),
  CountryInfo("SA", "Saudi Arabia", "🇸🇦", "Middle East"),
  CountryInfo("AE", "United Arab Emirates", "🇦🇪", "Middle East"),
  CountryInfo("NG", "Nigeria", "🇳🇬", "Africa"),
  CountryInfo("ZA", "South Africa", "🇿🇦", "Africa"),
  CountryInfo("KE", "Kenya", "🇰🇪", "Africa"),
  CountryInfo("TR", "Turkey", "🇹🇷", "Europe / Asia"),
  CountryInfo("NL", "Netherlands", "🇳🇱", "Europe"),
  CountryInfo("SE", "Sweden", "🇸🇪", "Europe"),
  CountryInfo("PL", "Poland", "🇵🇱", "Europe"),
  CountryInfo("UA", "Ukraine", "🇺🇦", "Europe"),
  CountryInfo("GR", "Greece", "🇬🇷", "Europe"),
  CountryInfo("IL", "Israel", "🇮🇱", "Middle East"),
  CountryInfo("ID", "Indonesia", "🇮🇩", "Asia"),
  CountryInfo("VN", "Vietnam", "🇻🇳", "Asia"),
  CountryInfo("TH", "Thailand", "🇹🇭", "Asia"),
  CountryInfo("PH", "Philippines", "🇵🇭", "Asia")
)

class UserPreferencesRepository(context: Context) {
  private val prefs: SharedPreferences =
    context.applicationContext.getSharedPreferences("idiom_ai_prefs", Context.MODE_PRIVATE)

  private val _preferences = MutableStateFlow(loadPreferences())
  val preferences: StateFlow<UserPreferences> = _preferences.asStateFlow()

  private fun loadPreferences(): UserPreferences {
    val nativeLang = prefs.getString("native_lang", "en") ?: "en"
    val learningLang = prefs.getString("learning_lang", "es") ?: "es"
    val dailyGoal = prefs.getInt("daily_goal", 15)
    val currentMinutes = prefs.getInt("current_minutes", 8)
    val streak = prefs.getInt("streak", 3)
    val words = prefs.getInt("words", 34)
    val xp = prefs.getInt("xp", 180)
    val onboardingDone = prefs.getBoolean("onboarding_done", false)
    val completedSet = prefs.getStringSet("completed_lessons", emptySet()) ?: emptySet()
    val voiceEngine = prefs.getString("tutor_voice_engine", "gemini_neural") ?: "gemini_neural"
    val geminiVoice = prefs.getString("tutor_gemini_voice", "Kore") ?: "Kore"
    val speechRate = prefs.getFloat("tutor_speech_rate", 1.0f)
    val autoPlay = prefs.getBoolean("auto_play_tutor_audio", false)

    val name = prefs.getString("user_name", "Language Learner") ?: "Language Learner"
    val bio = prefs.getString("user_bio", "Learning world languages one conversation at a time.") ?: ""
    val avatar = prefs.getString("user_avatar", "🎓") ?: "🎓"
    val photoUri = prefs.getString("user_photo_uri", "") ?: ""
    val country = prefs.getString("user_country", "Global") ?: "Global"
    val level = prefs.getString("user_level", "A1") ?: "A1"
    val theme = prefs.getString("theme_mode", "SYSTEM") ?: "SYSTEM"
    val weeklyDays = prefs.getInt("weekly_goal_days", 5)
    val reminderOn = prefs.getBoolean("reminder_enabled", false)
    val reminderAt = prefs.getString("reminder_time", "19:00") ?: "19:00"
    val speakingDone = prefs.getBoolean("has_completed_speaking", false)
    val listeningDone = prefs.getBoolean("has_completed_listening", false)

    val favs = prefs.getStringSet("favorite_languages", emptySet()) ?: emptySet()
    val recentsList = (prefs.getString("recent_languages", "es,en") ?: "es,en").split(",").filter { it.isNotBlank() }

    // Load saved lesson scores
    val scores = mutableMapOf<String, Int>()
    completedSet.forEach { lessonId ->
      val score = prefs.getInt("score_$lessonId", -1)
      if (score >= 0) {
        scores[lessonId] = score
      }
    }

    return UserPreferences(
      nativeLanguageId = nativeLang,
      learningLanguageId = learningLang,
      dailyGoalMinutes = dailyGoal,
      currentDailyMinutes = currentMinutes,
      streakDays = streak,
      wordsLearned = words,
      xpPoints = xp,
      isOnboardingCompleted = onboardingDone,
      completedLessons = completedSet,
      lessonScores = scores,
      tutorVoiceEngine = voiceEngine,
      tutorGeminiVoice = geminiVoice,
      tutorSpeechRate = speechRate,
      autoPlayTutorAudio = autoPlay,
      userName = name,
      userBio = bio,
      userAvatar = avatar,
      userProfilePhotoUri = photoUri,
      userCountry = country,
      userProficiencyLevel = level,
      themeMode = theme,
      favoriteLanguages = favs,
      recentLanguages = if (recentsList.isNotEmpty()) recentsList else listOf("es", "en"),
      weeklyGoalDays = weeklyDays,
      studyReminderEnabled = reminderOn,
      studyReminderTime = reminderAt,
      hasCompletedSpeaking = speakingDone,
      hasCompletedListening = listeningDone
    )
  }

  fun setLanguages(nativeId: String, learningId: String) {
    prefs.edit()
      .putString("native_lang", nativeId)
      .putString("learning_lang", learningId)
      .apply()
    addRecentLanguage(learningId)
    _preferences.value = _preferences.value.copy(
      nativeLanguageId = nativeId,
      learningLanguageId = learningId
    )
  }

  fun setLearningLanguage(learningId: String) {
    prefs.edit().putString("learning_lang", learningId).apply()
    addRecentLanguage(learningId)
    _preferences.value = _preferences.value.copy(learningLanguageId = learningId)
  }

  fun setDailyGoal(minutes: Int) {
    prefs.edit().putInt("daily_goal", minutes).apply()
    _preferences.value = _preferences.value.copy(dailyGoalMinutes = minutes)
  }

  fun setWeeklyGoal(days: Int) {
    val clamped = days.coerceIn(1, 7)
    prefs.edit().putInt("weekly_goal_days", clamped).apply()
    _preferences.value = _preferences.value.copy(weeklyGoalDays = clamped)
  }

  fun setStudyReminder(enabled: Boolean, time: String) {
    prefs.edit()
      .putBoolean("reminder_enabled", enabled)
      .putString("reminder_time", time)
      .apply()
    _preferences.value = _preferences.value.copy(
      studyReminderEnabled = enabled,
      studyReminderTime = time
    )
  }

  fun updateProfile(name: String, bio: String, avatar: String, level: String) {
    prefs.edit()
      .putString("user_name", name.trim())
      .putString("user_bio", bio.trim())
      .putString("user_avatar", avatar)
      .putString("user_level", level)
      .apply()
    _preferences.value = _preferences.value.copy(
      userName = name.trim(),
      userBio = bio.trim(),
      userAvatar = avatar,
      userProficiencyLevel = level
    )
  }

  fun updateProfileFull(
    name: String,
    bio: String,
    avatar: String,
    level: String,
    country: String,
    photoUri: String
  ) {
    prefs.edit()
      .putString("user_name", name.trim())
      .putString("user_bio", bio.trim())
      .putString("user_avatar", avatar)
      .putString("user_level", level)
      .putString("user_country", country)
      .putString("user_photo_uri", photoUri)
      .apply()
    _preferences.value = _preferences.value.copy(
      userName = name.trim(),
      userBio = bio.trim(),
      userAvatar = avatar,
      userProficiencyLevel = level,
      userCountry = country,
      userProfilePhotoUri = photoUri
    )
  }

  fun setProfilePhotoUri(uriString: String) {
    prefs.edit().putString("user_photo_uri", uriString).apply()
    _preferences.value = _preferences.value.copy(userProfilePhotoUri = uriString)
  }

  fun saveProfilePhotoFromUri(sourceUri: android.net.Uri, appContext: Context): String {
    return try {
      val targetFile = java.io.File(appContext.filesDir, "profile_photo.jpg")
      appContext.contentResolver.openInputStream(sourceUri)?.use { input ->
        java.io.FileOutputStream(targetFile).use { output ->
          input.copyTo(output)
        }
      }
      val path = targetFile.absolutePath
      setProfilePhotoUri(path)
      path
    } catch (e: Exception) {
      val fallback = sourceUri.toString()
      setProfilePhotoUri(fallback)
      fallback
    }
  }

  fun removeProfilePhoto() {
    prefs.edit().remove("user_photo_uri").apply()
    _preferences.value = _preferences.value.copy(userProfilePhotoUri = "")
  }

  fun removeProfilePhotoWithFile(appContext: Context? = null) {
    try {
      appContext?.let {
        val targetFile = java.io.File(it.filesDir, "profile_photo.jpg")
        if (targetFile.exists()) targetFile.delete()
      }
    } catch (_: Exception) {}
    removeProfilePhoto()
  }

  fun setUserCountry(country: String) {
    prefs.edit().putString("user_country", country).apply()
    _preferences.value = _preferences.value.copy(userCountry = country)
  }

  fun setThemeMode(mode: String) {
    prefs.edit().putString("theme_mode", mode).apply()
    _preferences.value = _preferences.value.copy(themeMode = mode)
  }

  fun toggleFavoriteLanguage(langId: String) {
    val current = _preferences.value.favoriteLanguages.toMutableSet()
    if (langId in current) {
      current.remove(langId)
    } else {
      current.add(langId)
    }
    prefs.edit().putStringSet("favorite_languages", current).apply()
    _preferences.value = _preferences.value.copy(favoriteLanguages = current)
  }

  fun addRecentLanguage(langId: String) {
    val current = _preferences.value.recentLanguages.filter { it != langId }.toMutableList()
    current.add(0, langId)
    val trimmed = current.take(8)
    prefs.edit().putString("recent_languages", trimmed.joinToString(",")).apply()
    _preferences.value = _preferences.value.copy(recentLanguages = trimmed)
  }

  fun calculateScore(p: UserPreferences = _preferences.value): Int {
    val lessonPts = p.completedLessons.size * 25
    val wordPts = p.wordsLearned * 2
    val quizPts = p.lessonScores.values.sumOf { it / 2 }
    val speakingPts = if (p.hasCompletedSpeaking) 30 else 0
    val listeningPts = if (p.hasCompletedListening) 30 else 0
    val streakPts = p.streakDays * 15
    return (lessonPts + wordPts + quizPts + speakingPts + listeningPts + streakPts).coerceAtLeast(0)
  }

  fun setOnboardingCompleted(completed: Boolean) {
    prefs.edit().putBoolean("onboarding_done", completed).apply()
    _preferences.value = _preferences.value.copy(isOnboardingCompleted = completed)
  }

  fun completeSampleLesson(lessonId: String, addedMinutes: Int = 5, addedXp: Int = 25, addedWords: Int = 6) {
    val updatedCompleted = _preferences.value.completedLessons + lessonId
    val updatedMinutes = _preferences.value.currentDailyMinutes + addedMinutes
    val updatedXp = _preferences.value.xpPoints + addedXp
    val updatedWords = _preferences.value.wordsLearned + addedWords

    prefs.edit()
      .putStringSet("completed_lessons", updatedCompleted)
      .putInt("current_minutes", updatedMinutes)
      .putInt("xp", updatedXp)
      .putInt("words", updatedWords)
      .apply()

    _preferences.value = _preferences.value.copy(
      completedLessons = updatedCompleted,
      currentDailyMinutes = updatedMinutes,
      xpPoints = updatedXp,
      wordsLearned = updatedWords
    )
  }

  fun recordCourseLessonCompletion(
    lessonId: String,
    scorePercent: Int,
    correctCount: Int,
    totalQuestions: Int,
    minutesSpent: Int = 5,
    xpEarned: Int = 30
  ) {
    val currentScores = _preferences.value.lessonScores.toMutableMap()
    val previousBest = currentScores[lessonId] ?: 0
    val bestScore = maxOf(previousBest, scorePercent)
    currentScores[lessonId] = bestScore

    val isFirstCompletion = lessonId !in _preferences.value.completedLessons
    val updatedCompleted = _preferences.value.completedLessons + lessonId
    val updatedMinutes = _preferences.value.currentDailyMinutes + minutesSpent
    val updatedXp = _preferences.value.xpPoints + xpEarned
    val updatedWords = if (isFirstCompletion) _preferences.value.wordsLearned + 5 else _preferences.value.wordsLearned

    prefs.edit()
      .putStringSet("completed_lessons", updatedCompleted)
      .putInt("score_$lessonId", bestScore)
      .putInt("current_minutes", updatedMinutes)
      .putInt("xp", updatedXp)
      .putInt("words", updatedWords)
      .apply()

    _preferences.value = _preferences.value.copy(
      completedLessons = updatedCompleted,
      lessonScores = currentScores,
      currentDailyMinutes = updatedMinutes,
      xpPoints = updatedXp,
      wordsLearned = updatedWords
    )
  }

  fun recordPracticeSession(minutesSpent: Int = 3, xpEarned: Int = 20) {
    val updatedMinutes = _preferences.value.currentDailyMinutes + minutesSpent
    val updatedXp = _preferences.value.xpPoints + xpEarned

    prefs.edit()
      .putInt("current_minutes", updatedMinutes)
      .putInt("xp", updatedXp)
      .apply()

    _preferences.value = _preferences.value.copy(
      currentDailyMinutes = updatedMinutes,
      xpPoints = updatedXp
    )
  }

  fun recordSpeakingCompleted(minutesSpent: Int = 3, xpEarned: Int = 25) {
    val updatedMinutes = _preferences.value.currentDailyMinutes + minutesSpent
    val updatedXp = _preferences.value.xpPoints + xpEarned

    prefs.edit()
      .putInt("current_minutes", updatedMinutes)
      .putInt("xp", updatedXp)
      .putBoolean("has_completed_speaking", true)
      .apply()

    _preferences.value = _preferences.value.copy(
      currentDailyMinutes = updatedMinutes,
      xpPoints = updatedXp,
      hasCompletedSpeaking = true
    )
  }

  fun recordListeningCompleted(minutesSpent: Int = 3, xpEarned: Int = 25) {
    val updatedMinutes = _preferences.value.currentDailyMinutes + minutesSpent
    val updatedXp = _preferences.value.xpPoints + xpEarned

    prefs.edit()
      .putInt("current_minutes", updatedMinutes)
      .putInt("xp", updatedXp)
      .putBoolean("has_completed_listening", true)
      .apply()

    _preferences.value = _preferences.value.copy(
      currentDailyMinutes = updatedMinutes,
      xpPoints = updatedXp,
      hasCompletedListening = true
    )
  }

  fun setTutorVoiceSettings(
    engineMode: String,
    geminiVoice: String,
    speechRate: Float,
    autoPlay: Boolean
  ) {
    prefs.edit()
      .putString("tutor_voice_engine", engineMode)
      .putString("tutor_gemini_voice", geminiVoice)
      .putFloat("tutor_speech_rate", speechRate)
      .putBoolean("auto_play_tutor_audio", autoPlay)
      .apply()

    _preferences.value = _preferences.value.copy(
      tutorVoiceEngine = engineMode,
      tutorGeminiVoice = geminiVoice,
      tutorSpeechRate = speechRate,
      autoPlayTutorAudio = autoPlay
    )
  }

  fun getAchievements(prefs: UserPreferences): List<MilestoneAchievement> {
    val hasLesson = prefs.completedLessons.isNotEmpty()
    val hasQuizAce = prefs.lessonScores.values.any { it >= 80 }
    val hasSpeaking = prefs.hasCompletedSpeaking
    val hasListening = prefs.hasCompletedListening
    val hasDailyGoal = prefs.currentDailyMinutes >= prefs.dailyGoalMinutes
    val hasStreak = prefs.streakDays >= 3
    val hasWeekStreak = prefs.streakDays >= 7
    val hasFiveLessons = prefs.completedLessons.size >= 5
    val hasFiftyWords = prefs.wordsLearned >= 50

    return listOf(
      MilestoneAchievement(
        id = "first_lesson",
        title = "First Steps",
        description = "Complete your first lesson.",
        emoji = "🏁",
        isUnlocked = hasLesson,
        progressText = if (hasLesson) "Completed" else "0 / 1 lessons"
      ),
      MilestoneAchievement(
        id = "quiz_ace",
        title = "Quiz Ace",
        description = "Score 80% or higher on any unit quiz.",
        emoji = "🎯",
        isUnlocked = hasQuizAce,
        progressText = if (hasQuizAce) "Completed (80%+ score)" else "Take a unit quiz"
      ),
      MilestoneAchievement(
        id = "speaking_starter",
        title = "Voice Pioneer",
        description = "Practice speaking aloud with real-time pronunciation feedback.",
        emoji = "🗣️",
        isUnlocked = hasSpeaking,
        progressText = if (hasSpeaking) "Completed" else "Try Speaking Practice"
      ),
      MilestoneAchievement(
        id = "listening_starter",
        title = "Attentive Ear",
        description = "Complete a listening comprehension exercise.",
        emoji = "🎧",
        isUnlocked = hasListening,
        progressText = if (hasListening) "Completed" else "Try Listening Practice"
      ),
      MilestoneAchievement(
        id = "daily_goal_met",
        title = "Daily Achiever",
        description = "Reach your daily study minutes goal.",
        emoji = "⭐",
        isUnlocked = hasDailyGoal,
        progressText = "${prefs.currentDailyMinutes} / ${prefs.dailyGoalMinutes} min"
      ),
      MilestoneAchievement(
        id = "streak_keeper",
        title = "Streak Champion",
        description = "Maintain a study streak of 3 or more days.",
        emoji = "🔥",
        isUnlocked = hasStreak,
        progressText = "${prefs.streakDays} / 3 days"
      ),
      MilestoneAchievement(
        id = "week_warrior",
        title = "Week Warrior",
        description = "Reach an unbroken 7-day study streak.",
        emoji = "⚡",
        isUnlocked = hasWeekStreak,
        progressText = "${prefs.streakDays} / 7 days"
      ),
      MilestoneAchievement(
        id = "curriculum_climber",
        title = "Curriculum Climber",
        description = "Complete 5 or more language lessons.",
        emoji = "🏔️",
        isUnlocked = hasFiveLessons,
        progressText = "${prefs.completedLessons.size} / 5 lessons"
      ),
      MilestoneAchievement(
        id = "word_collector",
        title = "Word Collector",
        description = "Master 50 or more vocabulary terms.",
        emoji = "📖",
        isUnlocked = hasFiftyWords,
        progressText = "${prefs.wordsLearned} / 50 words"
      )
    )
  }

  fun exportUserDataJson(): String {
    val p = _preferences.value
    val root = JSONObject().apply {
      put("application", "Idiom AI")
      put("account_mode", "Guest (On-Device Storage)")
      put("exported_at_epoch_ms", System.currentTimeMillis())
      put("profile", JSONObject().apply {
        put("name", p.userName)
        put("avatar", p.userAvatar)
        put("bio", p.userBio)
        put("proficiency_level", p.userProficiencyLevel)
        put("native_language", p.nativeLanguageId)
        put("learning_language", p.learningLanguageId)
      })
      put("goals", JSONObject().apply {
        put("daily_goal_minutes", p.dailyGoalMinutes)
        put("current_daily_minutes", p.currentDailyMinutes)
        put("weekly_target_days", p.weeklyGoalDays)
        put("streak_days", p.streakDays)
      })
      put("learning_stats", JSONObject().apply {
        put("total_xp", p.xpPoints)
        put("words_learned", p.wordsLearned)
        put("completed_lessons_count", p.completedLessons.size)
        put("completed_lessons_list", JSONArray(p.completedLessons.toList()))
        val scoresJson = JSONObject()
        p.lessonScores.forEach { (k, v) -> scoresJson.put(k, v) }
        put("lesson_scores", scoresJson)
      })
    }
    return root.toString(2)
  }

  fun clearAllLocalData() {
    prefs.edit().clear().apply()
    _preferences.value = UserPreferences()
  }

  fun resetProgress() {
    clearAllLocalData()
  }
}
