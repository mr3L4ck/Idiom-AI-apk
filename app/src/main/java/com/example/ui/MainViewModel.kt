package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.Course
import com.example.data.CourseCatalog
import com.example.data.Language
import com.example.data.LanguageCatalog
import com.example.data.LessonCatalog
import com.example.data.SampleLesson
import com.example.data.UserPreferences
import com.example.data.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class MainViewModel(application: Application) : AndroidViewModel(application) {

  private val repository = UserPreferencesRepository(application)

  val userPreferences: StateFlow<UserPreferences> = repository.preferences

  val nativeLanguage: StateFlow<Language> = repository.preferences
    .map { prefs -> LanguageCatalog.getLanguageById(prefs.nativeLanguageId) }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = LanguageCatalog.getLanguageById("en")
    )

  val learningLanguage: StateFlow<Language> = repository.preferences
    .map { prefs -> LanguageCatalog.getLanguageById(prefs.learningLanguageId) }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = LanguageCatalog.getLanguageById("es")
    )

  val currentCourse: StateFlow<Course?> = repository.preferences
    .map { prefs -> CourseCatalog.getCourseForLanguage(prefs.learningLanguageId) }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = CourseCatalog.getCourseForLanguage("es")
    )

  val currentLesson: StateFlow<SampleLesson> = repository.preferences
    .map { prefs -> LessonCatalog.getSampleLessonForLanguage(prefs.learningLanguageId) }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = LessonCatalog.getSampleLessonForLanguage("es")
    )

  fun saveLanguageSelection(nativeId: String, learningId: String) {
    repository.setLanguages(nativeId, learningId)
  }

  fun changeLearningLanguage(learningId: String) {
    repository.setLearningLanguage(learningId)
  }

  fun completeOnboarding() {
    repository.setOnboardingCompleted(true)
  }

  fun updateDailyGoal(minutes: Int) {
    repository.setDailyGoal(minutes)
  }

  fun completeLesson(lessonId: String) {
    repository.completeSampleLesson(lessonId)
  }

  fun recordCourseLessonResult(
    lessonId: String,
    scorePercent: Int,
    correctCount: Int,
    totalQuestions: Int,
    minutesSpent: Int = 5,
    xpEarned: Int = 30
  ) {
    repository.recordCourseLessonCompletion(
      lessonId = lessonId,
      scorePercent = scorePercent,
      correctCount = correctCount,
      totalQuestions = totalQuestions,
      minutesSpent = minutesSpent,
      xpEarned = xpEarned
    )
  }

  fun recordVoicePracticeSession(minutesSpent: Int = 3, xpEarned: Int = 20) {
    repository.recordPracticeSession(minutesSpent = minutesSpent, xpEarned = xpEarned)
  }

  fun updateProfile(name: String, bio: String, avatar: String, level: String) {
    repository.updateProfile(name, bio, avatar, level)
  }

  fun updateProfileFull(
    name: String,
    bio: String,
    avatar: String,
    level: String,
    country: String,
    photoUri: String
  ) {
    repository.updateProfileFull(name, bio, avatar, level, country, photoUri)
  }

  fun setProfilePhotoUri(uri: String) {
    repository.setProfilePhotoUri(uri)
  }

  fun saveProfilePhotoFromUri(sourceUri: android.net.Uri, context: android.content.Context): String {
    return repository.saveProfilePhotoFromUri(sourceUri, context)
  }

  fun removeProfilePhoto(context: android.content.Context? = null) {
    repository.removeProfilePhotoWithFile(context)
  }

  fun setTutorVoiceSettings(
    engineMode: String,
    geminiVoice: String,
    speechRate: Float,
    autoPlay: Boolean
  ) {
    repository.setTutorVoiceSettings(engineMode, geminiVoice, speechRate, autoPlay)
  }

  fun setUserCountry(country: String) {
    repository.setUserCountry(country)
  }

  fun setThemeMode(mode: String) {
    repository.setThemeMode(mode)
  }

  fun calculateScore(prefs: UserPreferences): Int {
    return repository.calculateScore(prefs)
  }

  fun updateWeeklyGoal(days: Int) {
    repository.setWeeklyGoal(days)
  }

  fun setStudyReminder(enabled: Boolean, time: String) {
    repository.setStudyReminder(enabled, time)
  }

  fun recordSpeakingCompleted(minutesSpent: Int = 3, xpEarned: Int = 25) {
    repository.recordSpeakingCompleted(minutesSpent, xpEarned)
  }

  fun recordListeningCompleted(minutesSpent: Int = 3, xpEarned: Int = 25) {
    repository.recordListeningCompleted(minutesSpent, xpEarned)
  }

  fun getAchievements(prefs: UserPreferences): List<com.example.data.MilestoneAchievement> {
    return repository.getAchievements(prefs)
  }

  fun exportUserDataJson(): String {
    return repository.exportUserDataJson()
  }

  fun clearAllLocalData() {
    repository.clearAllLocalData()
  }

  fun resetToWelcome() {
    repository.clearAllLocalData()
  }
}
