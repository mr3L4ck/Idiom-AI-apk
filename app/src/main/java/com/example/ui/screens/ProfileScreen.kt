package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.AVAILABLE_GEMINI_VOICES
import com.example.data.BrandConfig
import com.example.data.CountryInfo
import com.example.data.Language
import com.example.data.MilestoneAchievement
import com.example.data.POPULAR_COUNTRIES
import com.example.data.UserPreferences
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.DesignTokens
import java.io.File

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
  preferences: UserPreferences,
  nativeLanguage: Language,
  learningLanguage: Language,
  achievements: List<MilestoneAchievement>,
  onUpdateProfile: (name: String, bio: String, avatar: String, level: String) -> Unit,
  onUpdateProfileFull: (name: String, bio: String, avatar: String, level: String, country: String, photoUri: String) -> Unit,
  onSavePhotoUri: (Uri) -> Unit,
  onRemovePhoto: () -> Unit,
  onSelectCountry: (String) -> Unit,
  onSetThemeMode: (String) -> Unit,
  onChangeLanguages: () -> Unit,
  onUpdateDailyGoal: (Int) -> Unit,
  onUpdateWeeklyGoal: (Int) -> Unit,
  onToggleReminder: (Boolean, String) -> Unit,
  onUpdateVoiceSettings: (engine: String, voice: String, rate: Float, autoPlay: Boolean) -> Unit,
  onCalculateScore: () -> Int,
  onExportData: () -> String,
  onResetProgress: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val clipboardManager = LocalClipboardManager.current
  val scrollState = rememberScrollState()

  // State dialogs and sheets
  var showEditProfileDialog by remember { mutableStateOf(false) }
  var showPhotoOptionsDialog by remember { mutableStateOf(false) }
  var showCountryPickerDialog by remember { mutableStateOf(false) }
  var showScoreExplainerDialog by remember { mutableStateOf(false) }
  var showExportDialog by remember { mutableStateOf(false) }
  var showResetConfirmDialog by remember { mutableStateOf(false) }
  var showVoiceSettingsDialog by remember { mutableStateOf(false) }
  var exportedJsonText by remember { mutableStateOf("") }
  var selectedMilestoneTab by remember { mutableIntStateOf(0) } // 0: All, 1: Unlocked, 2: In Progress

  // Photo Picker Launcher (System Android Photo Picker - Zero Permission)
  val photoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri: Uri? ->
    if (uri != null) {
      onSavePhotoUri(uri)
      Toast.makeText(context, "Profile photo updated", Toast.LENGTH_SHORT).show()
    }
  }

  // Notification Permission Launcher (Android 13+ / API 33+)
  val notificationPermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    if (isGranted) {
      onToggleReminder(true, preferences.studyReminderTime)
      Toast.makeText(context, "Study reminder enabled for ${preferences.studyReminderTime}", Toast.LENGTH_SHORT).show()
    } else {
      Toast.makeText(context, "Notification permission is needed for reminders", Toast.LENGTH_LONG).show()
    }
  }

  val availableAvatars = listOf("🎓", "🌍", "🚀", "🦊", "🦉", "🌟", "💡", "🦁", "🐯", "🐼", "🐬", "🌺")
  val availableLevels = listOf(
    "A1" to "Beginner",
    "A2" to "Elementary",
    "B1" to "Intermediate",
    "B2" to "Upper Intermediate",
    "C1" to "Advanced",
    "C2" to "Mastery"
  )
  val reminderTimes = listOf("08:00", "12:30", "19:00", "21:00")

  // Calculated real score
  val calculatedScore = onCalculateScore()

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background),
    contentAlignment = Alignment.TopCenter
  ) {
    Column(
      modifier = Modifier
        .widthIn(max = DesignTokens.MaxContentWidth)
        .fillMaxSize()
        .verticalScroll(scrollState)
        .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {

      // Screen Header Title
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Learner Dashboard",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
          )
          Text(
            text = "Personal profile, learning score & preferences",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        IconButton(
          onClick = { showEditProfileDialog = true },
          modifier = Modifier.testTag("edit_profile_header_button")
        ) {
          Icon(
            imageVector = Icons.Filled.Edit,
            contentDescription = "Edit Profile",
            tint = MaterialTheme.colorScheme.primary
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // ==========================================
      // SECTION 1: PERSONAL PROFILE HEADER CARD
      // ==========================================
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("profile_header_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
      ) {
        Column(modifier = Modifier.padding(20.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Profile Photo / Avatar with edit badge
            Box(
              modifier = Modifier
                .size(76.dp)
                .clickable { showPhotoOptionsDialog = true }
                .testTag("profile_avatar_badge"),
              contentAlignment = Alignment.BottomEnd
            ) {
              val hasCustomPhoto = preferences.userProfilePhotoUri.isNotBlank() &&
                File(preferences.userProfilePhotoUri).exists()

              if (hasCustomPhoto) {
                AsyncImage(
                  model = ImageRequest.Builder(context)
                    .data(File(preferences.userProfilePhotoUri))
                    .crossfade(true)
                    .build(),
                  contentDescription = "User Profile Photo",
                  modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                  contentScale = ContentScale.Crop
                )
              } else {
                Surface(
                  shape = CircleShape,
                  color = MaterialTheme.colorScheme.primaryContainer,
                  modifier = Modifier.size(76.dp)
                ) {
                  Box(contentAlignment = Alignment.Center) {
                    Text(text = preferences.userAvatar, fontSize = 38.sp)
                  }
                }
              }

              // Camera edit icon badge
              Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                border = androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.surface),
                modifier = Modifier.size(26.dp)
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Icon(
                    imageVector = Icons.Filled.CameraAlt,
                    contentDescription = "Change photo",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(14.dp)
                  )
                }
              }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = preferences.userName,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
              )

              if (preferences.userBio.isNotBlank()) {
                Text(
                  text = preferences.userBio,
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  maxLines = 2
                )
              }

              Spacer(modifier = Modifier.height(4.dp))

              // Country & Level row
              Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                // Country Chip (Clickable to change)
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = MaterialTheme.colorScheme.surfaceVariant,
                  modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { showCountryPickerDialog = true }
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                  ) {
                    val country = POPULAR_COUNTRIES.find { it.name.equals(preferences.userCountry, ignoreCase = true) || it.code.equals(preferences.userCountry, ignoreCase = true) }
                    Text(text = country?.flag ?: "🌍", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                      text = country?.name ?: preferences.userCountry,
                      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                }

                // CEFR Level Tag
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)
                ) {
                  Text(
                    text = "Level ${preferences.userProficiencyLevel}",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Native vs Learning Language Cards
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Surface(
              shape = RoundedCornerShape(14.dp),
              color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
              modifier = Modifier
                .weight(1f)
                .clickable { onChangeLanguages() }
            ) {
              Column(modifier = Modifier.padding(10.dp)) {
                Text(
                  text = "INTERFACE / NATIVE",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = "${nativeLanguage.flagEmoji} ${nativeLanguage.name}",
                  style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onSurface
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(14.dp),
              color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
              modifier = Modifier
                .weight(1f)
                .clickable { onChangeLanguages() }
            ) {
              Column(modifier = Modifier.padding(10.dp)) {
                Text(
                  text = "LEARNING TARGET",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = "${learningLanguage.flagEmoji} ${learningLanguage.name}",
                  style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onSurface
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          OutlinedButton(
            onClick = { showEditProfileDialog = true },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("edit_profile_button"),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(imageVector = Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Edit Profile & Biography")
          }
        }
      }

      Spacer(modifier = Modifier.height(22.dp))

      // ==========================================
      // SECTION 2: LEARNING SCORE & STATISTICS
      // ==========================================
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "Learning Score",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
          )
          Spacer(modifier = Modifier.width(6.dp))
          IconButton(
            onClick = { showScoreExplainerDialog = true },
            modifier = Modifier.size(24.dp)
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.HelpOutline,
              contentDescription = "How score is calculated",
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(18.dp)
            )
          }
        }

        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.primaryContainer
        ) {
          Text(
            text = "$calculatedScore Pts",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("learning_score_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          // Score summary bar
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Idiom Learning Metric",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Based on verified lesson completions and voice practice",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
            Text(text = "⭐", fontSize = 24.sp)
          }

          Spacer(modifier = Modifier.height(14.dp))

          // 6 Key Statistics Grid
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            StatItem(
              title = "Streak",
              value = "${preferences.streakDays} Days",
              sub = "Consecutive study",
              emoji = "🔥",
              modifier = Modifier.weight(1f)
            )
            StatItem(
              title = "Lessons",
              value = "${preferences.completedLessons.size}",
              sub = "Units mastered",
              emoji = "✅",
              modifier = Modifier.weight(1f)
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            val avgQuiz = if (preferences.lessonScores.isNotEmpty()) {
              "${preferences.lessonScores.values.average().toInt()}%"
            } else {
              "—"
            }
            StatItem(
              title = "Quiz Accuracy",
              value = avgQuiz,
              sub = "Best quiz scores",
              emoji = "🎯",
              modifier = Modifier.weight(1f)
            )
            StatItem(
              title = "Vocabulary",
              value = "${preferences.wordsLearned}",
              sub = "Words practiced",
              emoji = "📚",
              modifier = Modifier.weight(1f)
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            StatItem(
              title = "Speaking",
              value = if (preferences.hasCompletedSpeaking) "Completed" else "Not started",
              sub = "Real-time speech",
              emoji = "🗣️",
              modifier = Modifier.weight(1f)
            )
            StatItem(
              title = "Listening",
              value = if (preferences.hasCompletedListening) "Completed" else "Not started",
              sub = "Audio comprehension",
              emoji = "🎧",
              modifier = Modifier.weight(1f)
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          Text(
            text = "App Learning Score is an internal activity metric. It is not an official CEFR language exam or institutional certification.",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
          )
        }
      }

      Spacer(modifier = Modifier.height(22.dp))

      // ==========================================
      // SECTION 3: ACHIEVEMENTS & MILESTONES
      // ==========================================
      val unlockedCount = achievements.count { it.isUnlocked }
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Milestones & Badges",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
          )
          Text(
            text = "$unlockedCount of ${achievements.size} milestones unlocked",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Surface(
          shape = RoundedCornerShape(10.dp),
          color = AccentGreen.copy(alpha = 0.16f)
        ) {
          Text(
            text = "$unlockedCount/${achievements.size}",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = AccentGreen,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Tabs: All, Unlocked, In Progress
      TabRow(
        selectedTabIndex = selectedMilestoneTab,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary,
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
      ) {
        Tab(
          selected = selectedMilestoneTab == 0,
          onClick = { selectedMilestoneTab = 0 },
          text = { Text("All (${achievements.size})") }
        )
        Tab(
          selected = selectedMilestoneTab == 1,
          onClick = { selectedMilestoneTab = 1 },
          text = { Text("Unlocked ($unlockedCount)") }
        )
        Tab(
          selected = selectedMilestoneTab == 2,
          onClick = { selectedMilestoneTab = 2 },
          text = { Text("Locked (${achievements.size - unlockedCount})") }
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      val displayedMilestones = when (selectedMilestoneTab) {
        1 -> achievements.filter { it.isUnlocked }
        2 -> achievements.filter { !it.isUnlocked }
        else -> achievements
      }

      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (displayedMilestones.isEmpty()) {
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
          ) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "No milestones in this category yet.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        } else {
          displayedMilestones.forEach { achievement ->
            AchievementCardItem(achievement = achievement)
          }
        }
      }

      Spacer(modifier = Modifier.height(22.dp))

      // ==========================================
      // SECTION 4: LEARNING GOALS & REMINDERS
      // ==========================================
      Text(
        text = "Study Goals & Reminders",
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onBackground
      )
      Spacer(modifier = Modifier.height(10.dp))

      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          // Today's Progress Bar
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Today's Study Goal",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "${preferences.currentDailyMinutes} / ${preferences.dailyGoalMinutes} min",
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.primary
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          val dailyFraction = (preferences.currentDailyMinutes.toFloat() / preferences.dailyGoalMinutes.toFloat()).coerceIn(0f, 1f)
          LinearProgressIndicator(
            progress = { dailyFraction },
            modifier = Modifier
              .fillMaxWidth()
              .height(8.dp)
              .clip(RoundedCornerShape(4.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
          )

          Spacer(modifier = Modifier.height(14.dp))

          Text(
            text = "Daily Target:",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(6.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            listOf(5, 10, 15, 20, 30, 45).forEach { mins ->
              val isSelected = preferences.dailyGoalMinutes == mins
              Surface(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(10.dp))
                  .clickable { onUpdateDailyGoal(mins) },
                shape = RoundedCornerShape(10.dp),
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
              ) {
                Box(modifier = Modifier.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                  Text(
                    text = "${mins}m",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(18.dp))

          // Weekly Target
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Weekly Target",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "${preferences.weeklyGoalDays} days study per week",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              listOf(3, 5, 7).forEach { days ->
                val isSelected = preferences.weeklyGoalDays == days
                FilterChip(
                  selected = isSelected,
                  onClick = { onUpdateWeeklyGoal(days) },
                  label = { Text("${days}d") }
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(18.dp))

          // Daily Study Reminder Row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Filled.Alarm,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
              )
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "Daily Reminder",
                  style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                  text = if (preferences.studyReminderEnabled) "Scheduled for ${preferences.studyReminderTime}" else "Disabled",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }

            Switch(
              checked = preferences.studyReminderEnabled,
              onCheckedChange = { checked ->
                if (checked && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                  val hasPerm = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
                  if (!hasPerm) {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    return@Switch
                  }
                }
                onToggleReminder(checked, preferences.studyReminderTime)
              }
            )
          }

          if (preferences.studyReminderEnabled) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              reminderTimes.forEach { time ->
                val isSelected = preferences.studyReminderTime == time
                FilterChip(
                  selected = isSelected,
                  onClick = { onToggleReminder(true, time) },
                  label = { Text(time) }
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(22.dp))

      // ==========================================
      // SECTION 5: APPEARANCE & THEME
      // ==========================================
      Text(
        text = "Appearance",
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onBackground
      )
      Spacer(modifier = Modifier.height(10.dp))

      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("appearance_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Text(
            text = "Color Theme",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "Select your preferred visual appearance. Deep Navy & Turquoise is default.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(14.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            ThemeOptionCard(
              title = "System",
              sub = "Auto",
              icon = Icons.Filled.Settings,
              isSelected = preferences.themeMode == "SYSTEM",
              onClick = { onSetThemeMode("SYSTEM") },
              modifier = Modifier.weight(1f)
            )
            ThemeOptionCard(
              title = "Dark",
              sub = "Navy & Teal",
              icon = Icons.Filled.DarkMode,
              isSelected = preferences.themeMode == "DARK",
              onClick = { onSetThemeMode("DARK") },
              modifier = Modifier.weight(1f)
            )
            ThemeOptionCard(
              title = "Light",
              sub = "Daylight",
              icon = Icons.Filled.LightMode,
              isSelected = preferences.themeMode == "LIGHT",
              onClick = { onSetThemeMode("LIGHT") },
              modifier = Modifier.weight(1f)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(22.dp))

      // ==========================================
      // SECTION 6: VOICE & LEARNING PREFERENCES
      // ==========================================
      Text(
        text = "Voice & Tutor Preferences",
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onBackground
      )
      Spacer(modifier = Modifier.height(10.dp))

      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          SettingRow(
            label = "Tutor Voice Persona",
            value = preferences.tutorGeminiVoice
          )
          Spacer(modifier = Modifier.height(8.dp))
          SettingRow(
            label = "Voice Engine",
            value = if (preferences.tutorVoiceEngine == "gemini_neural") BrandConfig.VOICE_FEATURE_NAME else "Device TTS"
          )
          Spacer(modifier = Modifier.height(8.dp))
          SettingRow(
            label = "Speaking Speed",
            value = "${preferences.tutorSpeechRate}x"
          )
          Spacer(modifier = Modifier.height(8.dp))
          SettingRow(
            label = "Auto-Play Responses",
            value = if (preferences.autoPlayTutorAudio) "Enabled" else "Off"
          )

          Spacer(modifier = Modifier.height(14.dp))

          OutlinedButton(
            onClick = { showVoiceSettingsDialog = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(imageVector = Icons.Filled.RecordVoiceOver, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Adjust Voice Persona & Speed")
          }
        }
      }

      Spacer(modifier = Modifier.height(22.dp))

      // ==========================================
      // SECTION 7: PRIVACY, DATA EXPORT & ACCOUNT
      // ==========================================
      Text(
        text = "Privacy & Local Data",
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onBackground
      )
      Spacer(modifier = Modifier.height(10.dp))

      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Filled.Security,
              contentDescription = null,
              tint = AccentGreen,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "On-Device Privacy & Data Security",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = "• Voice speech recognition is evaluated locally in real-time. No recordings are stored or retained.\n" +
              "• Interactive AI conversational requests are processed securely via Google API endpoints. No private data is logged on Idiom AI external servers.\n" +
              "• All lesson scores, vocabulary, and user profile details remain on your device in private local storage.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(16.dp))

          // Export JSON
          OutlinedButton(
            onClick = {
              exportedJsonText = onExportData()
              showExportDialog = true
            },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("export_data_button"),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(imageVector = Icons.Filled.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Export Learning Data (JSON)")
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Clear local data
          OutlinedButton(
            onClick = { showResetConfirmDialog = true },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("reset_local_data_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
          ) {
            Icon(imageVector = Icons.Filled.DeleteOutline, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Clear Local Data & Reset")
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Section 8: Brand & Legal Disclosures
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
        border = CardDefaults.outlinedCardBorder()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "About ${BrandConfig.APP_NAME}",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "${BrandConfig.TAGLINE} • /${BrandConfig.PRONUNCIATION_GUIDE}/",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Conversational AI: ${BrandConfig.AI_TUTOR_NAME}\nVoice Delivery: ${BrandConfig.VOICE_FEATURE_NAME} & on-device speech",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = BrandConfig.TECHNICAL_PROVIDER_DISCLOSURE,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
          )
        }
      }

      Spacer(modifier = Modifier.height(32.dp))
    }
  }

  // ==========================================
  // DIALOGS & BOTTOM SHEETS
  // ==========================================

  // 1. Photo Options Dialog (Choose photo picker, preset avatar, or remove)
  if (showPhotoOptionsDialog) {
    AlertDialog(
      onDismissRequest = { showPhotoOptionsDialog = false },
      title = { Text("Profile Photo") },
      text = {
        Column {
          Text(
            text = "Choose a photo from your device using the Android Photo Picker or select a preset avatar:",
            style = MaterialTheme.typography.bodyMedium
          )
          Spacer(modifier = Modifier.height(16.dp))

          // Option A: Android Photo Picker
          Button(
            onClick = {
              showPhotoOptionsDialog = false
              photoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
              )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(imageVector = Icons.Filled.AddAPhoto, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Choose from Gallery")
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Option B: Remove Photo (if set)
          if (preferences.userProfilePhotoUri.isNotBlank()) {
            OutlinedButton(
              onClick = {
                onRemovePhoto()
                showPhotoOptionsDialog = false
                Toast.makeText(context, "Profile photo removed", Toast.LENGTH_SHORT).show()
              },
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
              Icon(imageVector = Icons.Filled.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text("Remove Photo")
            }
          }
        }
      },
      confirmButton = {},
      dismissButton = {
        TextButton(onClick = { showPhotoOptionsDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }

  // 2. Comprehensive Edit Profile Dialog
  if (showEditProfileDialog) {
    var editName by remember { mutableStateOf(preferences.userName) }
    var editBio by remember { mutableStateOf(preferences.userBio) }
    var editAvatar by remember { mutableStateOf(preferences.userAvatar) }
    var editLevel by remember { mutableStateOf(preferences.userProficiencyLevel) }

    AlertDialog(
      onDismissRequest = { showEditProfileDialog = false },
      title = { Text("Edit Learner Profile") },
      text = {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
          Text("Display Name", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
          Spacer(modifier = Modifier.height(4.dp))
          OutlinedTextField(
            value = editName,
            onValueChange = { editName = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
          )

          Spacer(modifier = Modifier.height(14.dp))

          Text("Choose Avatar Icon", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
          Spacer(modifier = Modifier.height(6.dp))
          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            availableAvatars.forEach { av ->
              val isSelected = editAvatar == av
              Surface(
                shape = CircleShape,
                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                modifier = Modifier
                  .size(42.dp)
                  .clickable { editAvatar = av }
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Text(text = av, fontSize = 22.sp)
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          Text("Proficiency Level", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
          Spacer(modifier = Modifier.height(6.dp))
          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            availableLevels.forEach { (lvl, title) ->
              FilterChip(
                selected = editLevel == lvl,
                onClick = { editLevel = lvl },
                label = { Text("$lvl - $title") }
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          Text("Biography (Optional)", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
          Spacer(modifier = Modifier.height(4.dp))
          OutlinedTextField(
            value = editBio,
            onValueChange = { editBio = it },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3,
            placeholder = { Text("Share your language goals or background...") }
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            onUpdateProfile(editName, editBio, editAvatar, editLevel)
            showEditProfileDialog = false
            Toast.makeText(context, "Profile updated", Toast.LENGTH_SHORT).show()
          }
        ) {
          Text("Save Profile")
        }
      },
      dismissButton = {
        TextButton(onClick = { showEditProfileDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }

  // 3. Searchable Country Picker Dialog
  if (showCountryPickerDialog) {
    var countryQuery by remember { mutableStateOf("") }
    val filteredCountries = remember(countryQuery) {
      if (countryQuery.isBlank()) POPULAR_COUNTRIES
      else POPULAR_COUNTRIES.filter {
        it.name.contains(countryQuery, ignoreCase = true) || it.code.contains(countryQuery, ignoreCase = true)
      }
    }

    AlertDialog(
      onDismissRequest = { showCountryPickerDialog = false },
      title = { Text("Select Country or Region") },
      text = {
        Column(modifier = Modifier.height(380.dp)) {
          Text(
            text = "Optional: Choose your country or region. No device location permission is used.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = countryQuery,
            onValueChange = { countryQuery = it },
            placeholder = { Text("Search countries...") },
            leadingIcon = { Icon(imageVector = Icons.Filled.Search, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(10.dp))

          LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            items(filteredCountries, key = { it.code }) { c ->
              val isSelected = preferences.userCountry.equals(c.name, ignoreCase = true) ||
                preferences.userCountry.equals(c.code, ignoreCase = true)
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(10.dp))
                  .clickable {
                    onSelectCountry(c.name)
                    showCountryPickerDialog = false
                  }
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(text = c.flag, fontSize = 20.sp)
                  Spacer(modifier = Modifier.width(10.dp))
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = c.name,
                      style = MaterialTheme.typography.bodyMedium.copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal),
                      color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                      text = c.region,
                      style = MaterialTheme.typography.labelSmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                  if (isSelected) {
                    Icon(imageVector = Icons.Filled.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                  }
                }
              }
            }
          }
        }
      },
      confirmButton = {},
      dismissButton = {
        TextButton(onClick = { showCountryPickerDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }

  // 4. Learning Score Explanation Dialog
  if (showScoreExplainerDialog) {
    AlertDialog(
      onDismissRequest = { showScoreExplainerDialog = false },
      title = { Text("How Learning Points are Earned") },
      text = {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
          Text(
            text = "Your App Learning Score reflects your real practice and consistency:",
            style = MaterialTheme.typography.bodyMedium
          )
          Spacer(modifier = Modifier.height(12.dp))

          ScoreRuleItem("🏁 Completed Lessons", "25 points each", "Awarded upon finishing a verified course unit")
          ScoreRuleItem("📚 Words Learned", "2 points each", "Awarded for every new vocabulary term acquired")
          ScoreRuleItem("🎯 Unit Quizzes", "Up to 50 points", "Calculated as (Quiz Score % / 2)")
          ScoreRuleItem("🗣️ Speaking Practice", "30 points", "Awarded for pronunciation exercises")
          ScoreRuleItem("🎧 Listening Practice", "30 points", "Awarded for audio comprehension units")
          ScoreRuleItem("🔥 Study Streak", "15 points / day", "Accumulates with consecutive daily practice")

          Spacer(modifier = Modifier.height(14.dp))
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
          ) {
            Text(
              text = "Note: Points are awarded only for successfully completed activities and are never duplicated. This metric is designed to motivate daily practice and is not an accredited language certification.",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.padding(10.dp)
            )
          }
        }
      },
      confirmButton = {
        Button(onClick = { showScoreExplainerDialog = false }) {
          Text("Got It")
        }
      }
    )
  }

  // 5. Voice Persona & Speed Dialog
  if (showVoiceSettingsDialog) {
    var selectedVoice by remember { mutableStateOf(preferences.tutorGeminiVoice) }
    var selectedSpeed by remember { mutableFloatStateOf(preferences.tutorSpeechRate) }
    var autoPlayAudio by remember { mutableStateOf(preferences.autoPlayTutorAudio) }

    AlertDialog(
      onDismissRequest = { showVoiceSettingsDialog = false },
      title = { Text("Tutor Voice & Speed") },
      text = {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
          Text("Voice Persona", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
          Spacer(modifier = Modifier.height(6.dp))

          AVAILABLE_GEMINI_VOICES.forEach { voice ->
            val isSelected = selectedVoice == voice.voiceName
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .clickable { selectedVoice = voice.voiceName }
                .padding(vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              RadioButton(
                selected = isSelected,
                onClick = { selectedVoice = voice.voiceName }
              )
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(
                  text = "${voice.displayName} • ${voice.toneTrait}",
                  style = MaterialTheme.typography.bodyMedium.copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                )
                Text(
                  text = voice.description,
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Speaking Rate", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
            Text("${selectedSpeed}x", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
          }
          Slider(
            value = selectedSpeed,
            onValueChange = { selectedSpeed = (Math.round(it * 10) / 10.0f).coerceIn(0.75f, 1.5f) },
            valueRange = 0.75f..1.5f,
            steps = 2
          )

          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text("Auto-Play Responses", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
              Text("Play tutor speech aloud automatically", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Switch(checked = autoPlayAudio, onCheckedChange = { autoPlayAudio = it })
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            onUpdateVoiceSettings(preferences.tutorVoiceEngine, selectedVoice, selectedSpeed, autoPlayAudio)
            showVoiceSettingsDialog = false
            Toast.makeText(context, "Voice settings saved", Toast.LENGTH_SHORT).show()
          }
        ) {
          Text("Save")
        }
      },
      dismissButton = {
        TextButton(onClick = { showVoiceSettingsDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }

  // 6. Export JSON Dialog
  if (showExportDialog) {
    AlertDialog(
      onDismissRequest = { showExportDialog = false },
      title = { Text("Export Learning Data") },
      text = {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
          Text(
            text = "Your full learning progress, scores, milestones and settings formatted as JSON:",
            style = MaterialTheme.typography.bodySmall
          )
          Spacer(modifier = Modifier.height(8.dp))
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = exportedJsonText,
              style = MaterialTheme.typography.labelSmall.copy(fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace),
              modifier = Modifier.padding(10.dp)
            )
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            clipboardManager.setText(AnnotatedString(exportedJsonText))
            Toast.makeText(context, "JSON copied to clipboard!", Toast.LENGTH_SHORT).show()
            showExportDialog = false
          }
        ) {
          Icon(imageVector = Icons.Filled.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Copy JSON")
        }
      },
      dismissButton = {
        TextButton(onClick = { showExportDialog = false }) {
          Text("Close")
        }
      }
    )
  }

  // 7. Clear / Reset Confirmation Dialog
  if (showResetConfirmDialog) {
    AlertDialog(
      onDismissRequest = { showResetConfirmDialog = false },
      title = { Text("Clear Local Data?") },
      text = {
        Text(
          text = "This will erase all recorded study minutes, streaks, completed lesson scores, and profile information from this device. This action cannot be undone.",
          style = MaterialTheme.typography.bodyMedium
        )
      },
      confirmButton = {
        Button(
          onClick = {
            showResetConfirmDialog = false
            onResetProgress()
          },
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
          Text("Clear Everything")
        }
      },
      dismissButton = {
        TextButton(onClick = { showResetConfirmDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }
}

@Composable
private fun StatItem(
  title: String,
  value: String,
  sub: String,
  emoji: String,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = CardDefaults.outlinedCardBorder()
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = title,
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(text = emoji, fontSize = 16.sp)
      }
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = value,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
        color = MaterialTheme.colorScheme.primary,
        maxLines = 1
      )
      Text(
        text = sub,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 1
      )
    }
  }
}

@Composable
private fun AchievementCardItem(achievement: MilestoneAchievement) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (achievement.isUnlocked) {
        AccentGreen.copy(alpha = 0.08f)
      } else {
        MaterialTheme.colorScheme.surface
      }
    ),
    border = if (achievement.isUnlocked) {
      CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(AccentGreen.copy(alpha = 0.6f)))
    } else {
      CardDefaults.outlinedCardBorder()
    }
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Surface(
        shape = CircleShape,
        color = if (achievement.isUnlocked) AccentGreen.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.size(44.dp)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Text(text = achievement.emoji, fontSize = 22.sp)
        }
      }

      Spacer(modifier = Modifier.width(14.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = achievement.title,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.width(6.dp))
          if (achievement.isUnlocked) {
            Icon(
              imageVector = Icons.Filled.CheckCircle,
              contentDescription = "Unlocked",
              tint = AccentGreen,
              modifier = Modifier.size(16.dp)
            )
          } else {
            Icon(
              imageVector = Icons.Filled.Lock,
              contentDescription = "Locked",
              tint = MaterialTheme.colorScheme.outline,
              modifier = Modifier.size(14.dp)
            )
          }
        }
        Text(
          text = achievement.description,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
          text = achievement.progressText,
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
          color = if (achievement.isUnlocked) AccentGreen else MaterialTheme.colorScheme.primary
        )
      }
    }
  }
}

@Composable
private fun ThemeOptionCard(
  title: String,
  sub: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(14.dp),
    color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
    border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else CardDefaults.outlinedCardBorder(),
    modifier = modifier
      .clip(RoundedCornerShape(14.dp))
      .clickable { onClick() }
  ) {
    Column(
      modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.size(24.dp)
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = title,
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
      )
      Text(
        text = sub,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 1
      )
    }
  }
}

@Composable
private fun ScoreRuleItem(title: String, points: String, description: String) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 6.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface
      )
      Text(
        text = description,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
    Surface(
      shape = RoundedCornerShape(8.dp),
      color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
    ) {
      Text(
        text = points,
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
      )
    }
  }
}

@Composable
private fun SettingRow(label: String, value: String) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Text(
      text = value,
      style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
      color = MaterialTheme.colorScheme.onSurface
    )
  }
}
