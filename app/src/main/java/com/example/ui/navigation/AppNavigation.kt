package com.example.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Leaderboard
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MainViewModel
import com.example.ui.screens.AITutorScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LanguageSelectionScreen
import com.example.ui.screens.LearnScreen
import com.example.ui.screens.LessonExperienceScreen
import com.example.ui.screens.ListeningPracticeScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ProgressScreen
import com.example.ui.screens.SpeakingPracticeScreen
import com.example.ui.screens.WelcomeScreen

enum class RootScreen {
  WELCOME,
  LANGUAGE_SELECTION,
  MAIN_APP,
  LESSON_EXPERIENCE,
  SPEAKING_PRACTICE,
  LISTENING_PRACTICE
}

enum class BottomNavTab(
  val label: String,
  val selectedIcon: ImageVector,
  val unselectedIcon: ImageVector,
  val testTag: String
) {
  HOME("Home", Icons.Filled.Home, Icons.Outlined.Home, "nav_item_home"),
  LEARN("Learn", Icons.AutoMirrored.Filled.MenuBook, Icons.AutoMirrored.Outlined.MenuBook, "nav_item_learn"),
  AI_TUTOR("Idiom AI", Icons.Filled.SmartToy, Icons.Outlined.SmartToy, "nav_item_ai_tutor"),
  PROGRESS("Progress", Icons.Filled.Leaderboard, Icons.Outlined.Leaderboard, "nav_item_progress"),
  PROFILE("Profile", Icons.Filled.Person, Icons.Outlined.Person, "nav_item_profile")
}

@Composable
fun AppNavigation(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  val preferences by viewModel.userPreferences.collectAsStateWithLifecycle()
  val nativeLanguage by viewModel.nativeLanguage.collectAsStateWithLifecycle()
  val learningLanguage by viewModel.learningLanguage.collectAsStateWithLifecycle()
  val currentCourse by viewModel.currentCourse.collectAsStateWithLifecycle()
  val currentLesson by viewModel.currentLesson.collectAsStateWithLifecycle()

  var rootScreen by rememberSaveable {
    mutableStateOf(
      if (preferences.isOnboardingCompleted) RootScreen.MAIN_APP else RootScreen.WELCOME
    )
  }

  var currentTab by rememberSaveable { mutableStateOf(BottomNavTab.HOME) }

  AnimatedContent(
    targetState = rootScreen,
    transitionSpec = { fadeIn() togetherWith fadeOut() },
    label = "RootNavigation"
  ) { targetRoot ->
    when (targetRoot) {
      RootScreen.WELCOME -> {
        WelcomeScreen(
          onGetStarted = { rootScreen = RootScreen.LANGUAGE_SELECTION },
          onSkipToHome = {
            viewModel.completeOnboarding()
            rootScreen = RootScreen.MAIN_APP
          },
          hasExistingSelection = preferences.isOnboardingCompleted,
          modifier = modifier
        )
      }

      RootScreen.LANGUAGE_SELECTION -> {
        LanguageSelectionScreen(
          initialNativeId = preferences.nativeLanguageId,
          initialLearningId = preferences.learningLanguageId,
          onLanguagesConfirmed = { nativeId, learningId ->
            viewModel.saveLanguageSelection(nativeId, learningId)
            viewModel.completeOnboarding()
            rootScreen = RootScreen.MAIN_APP
            currentTab = BottomNavTab.HOME
          },
          onBack = {
            if (preferences.isOnboardingCompleted) {
              rootScreen = RootScreen.MAIN_APP
            } else {
              rootScreen = RootScreen.WELCOME
            }
          },
          modifier = modifier
        )
      }

      RootScreen.LESSON_EXPERIENCE -> {
        val course = currentCourse
        val activeLesson = course?.units?.firstOrNull()?.lessons?.firstOrNull()
        if (activeLesson != null) {
          LessonExperienceScreen(
            lesson = activeLesson,
            languageCode = learningLanguage.id,
            languageName = learningLanguage.name,
            onFinishAndSave = { scorePercent, correctCount, totalQuestions ->
              viewModel.recordCourseLessonResult(
                lessonId = activeLesson.id,
                scorePercent = scorePercent,
                correctCount = correctCount,
                totalQuestions = totalQuestions,
                minutesSpent = 5,
                xpEarned = 30
              )
              rootScreen = RootScreen.MAIN_APP
            },
            onExit = {
              rootScreen = RootScreen.MAIN_APP
            },
            modifier = modifier
          )
        } else {
          rootScreen = RootScreen.MAIN_APP
        }
      }

      RootScreen.SPEAKING_PRACTICE -> {
        SpeakingPracticeScreen(
          learningLanguage = learningLanguage,
          nativeLanguage = nativeLanguage,
          onFinish = { _, xpEarned ->
            viewModel.recordSpeakingCompleted(minutesSpent = 3, xpEarned = xpEarned)
            rootScreen = RootScreen.MAIN_APP
          },
          onExit = {
            rootScreen = RootScreen.MAIN_APP
          },
          modifier = modifier
        )
      }

      RootScreen.LISTENING_PRACTICE -> {
        ListeningPracticeScreen(
          learningLanguage = learningLanguage,
          nativeLanguage = nativeLanguage,
          onFinish = { _, _, xpEarned ->
            viewModel.recordListeningCompleted(minutesSpent = 3, xpEarned = xpEarned)
            rootScreen = RootScreen.MAIN_APP
          },
          onExit = {
            rootScreen = RootScreen.MAIN_APP
          },
          modifier = modifier
        )
      }

      RootScreen.MAIN_APP -> {
        Scaffold(
          modifier = modifier.fillMaxSize(),
          contentWindowInsets = WindowInsets.navigationBars,
          bottomBar = {
            NavigationBar(
              containerColor = MaterialTheme.colorScheme.surface,
              contentColor = MaterialTheme.colorScheme.onSurface,
              windowInsets = NavigationBarDefaults.windowInsets
            ) {
              BottomNavTab.entries.forEach { tab ->
                val isSelected = currentTab == tab
                NavigationBarItem(
                  selected = isSelected,
                  onClick = { currentTab = tab },
                  icon = {
                    Icon(
                      imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                      contentDescription = tab.label
                    )
                  },
                  label = { Text(text = tab.label) },
                  colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                  ),
                  modifier = Modifier.testTag(tab.testTag)
                )
              }
            }
          }
        ) { innerPadding ->
          // Handle back press to always return to HOME tab if on a secondary tab
          if (currentTab != BottomNavTab.HOME) {
            BackHandler {
              currentTab = BottomNavTab.HOME
            }
          }

          Box(
            modifier = Modifier
              .fillMaxSize()
              .padding(innerPadding)
          ) {
            when (currentTab) {
              BottomNavTab.HOME -> {
                HomeScreen(
                  nativeLanguage = nativeLanguage,
                  learningLanguage = learningLanguage,
                  preferences = preferences,
                  lesson = currentLesson,
                  onNavigateToLanguageSelection = {
                    rootScreen = RootScreen.LANGUAGE_SELECTION
                  },
                  onUpdateDailyGoal = { minutes ->
                    viewModel.updateDailyGoal(minutes)
                  },
                  onCompleteLesson = { lessonId ->
                    viewModel.completeLesson(lessonId)
                  },
                  onStartCourseLesson = {
                    if (currentCourse != null) {
                      rootScreen = RootScreen.LESSON_EXPERIENCE
                    }
                  },
                  onStartSpeakingPractice = {
                    rootScreen = RootScreen.SPEAKING_PRACTICE
                  },
                  onStartListeningPractice = {
                    rootScreen = RootScreen.LISTENING_PRACTICE
                  }
                )
              }

              BottomNavTab.LEARN -> {
                LearnScreen(
                  learningLanguage = learningLanguage,
                  course = currentCourse,
                  preferences = preferences,
                  onStartCourseLesson = { lesson ->
                    rootScreen = RootScreen.LESSON_EXPERIENCE
                  },
                  onSwitchToSpanish = {
                    viewModel.changeLearningLanguage("es")
                  },
                  onStartSpeakingPractice = {
                    rootScreen = RootScreen.SPEAKING_PRACTICE
                  },
                  onStartListeningPractice = {
                    rootScreen = RootScreen.LISTENING_PRACTICE
                  }
                )
              }

              BottomNavTab.AI_TUTOR -> {
                AITutorScreen(
                  learningLanguage = learningLanguage,
                  nativeLanguage = nativeLanguage
                )
              }

              BottomNavTab.PROGRESS -> {
                ProgressScreen(
                  preferences = preferences,
                  learningLanguage = learningLanguage
                )
              }

              BottomNavTab.PROFILE -> {
                val context = androidx.compose.ui.platform.LocalContext.current
                val achievements = viewModel.getAchievements(preferences)
                ProfileScreen(
                  preferences = preferences,
                  nativeLanguage = nativeLanguage,
                  learningLanguage = learningLanguage,
                  achievements = achievements,
                  onUpdateProfile = { name, bio, avatar, level ->
                    viewModel.updateProfile(name, bio, avatar, level)
                  },
                  onUpdateProfileFull = { name, bio, avatar, level, country, photoUri ->
                    viewModel.updateProfileFull(name, bio, avatar, level, country, photoUri)
                  },
                  onSavePhotoUri = { uri ->
                    viewModel.saveProfilePhotoFromUri(uri, context)
                  },
                  onRemovePhoto = {
                    viewModel.removeProfilePhoto(context)
                  },
                  onSelectCountry = { country ->
                    viewModel.setUserCountry(country)
                  },
                  onSetThemeMode = { mode ->
                    viewModel.setThemeMode(mode)
                  },
                  onChangeLanguages = {
                    rootScreen = RootScreen.LANGUAGE_SELECTION
                  },
                  onUpdateDailyGoal = { minutes ->
                    viewModel.updateDailyGoal(minutes)
                  },
                  onUpdateWeeklyGoal = { days ->
                    viewModel.updateWeeklyGoal(days)
                  },
                  onToggleReminder = { enabled, time ->
                    viewModel.setStudyReminder(enabled, time)
                  },
                  onUpdateVoiceSettings = { engine, voice, rate, autoPlay ->
                    viewModel.setTutorVoiceSettings(engine, voice, rate, autoPlay)
                  },
                  onCalculateScore = {
                    viewModel.calculateScore(preferences)
                  },
                  onExportData = {
                    viewModel.exportUserDataJson()
                  },
                  onResetProgress = {
                    viewModel.resetToWelcome()
                    rootScreen = RootScreen.WELCOME
                  }
                )
              }
            }
          }
        }
      }
    }
  }
}
