package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Language
import com.example.data.SampleLesson
import com.example.data.UserPreferences
import com.example.ui.components.DailyGoalCard
import com.example.ui.components.DifficultyBadge
import com.example.ui.components.PhraseInteractiveCard
import com.example.ui.components.StatChip
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AmberSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
  nativeLanguage: Language,
  learningLanguage: Language,
  preferences: UserPreferences,
  lesson: SampleLesson,
  onNavigateToLanguageSelection: () -> Unit,
  onUpdateDailyGoal: (Int) -> Unit,
  onCompleteLesson: (String) -> Unit,
  onStartCourseLesson: (() -> Unit)? = null,
  onStartSpeakingPractice: (() -> Unit)? = null,
  onStartListeningPractice: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  val scrollState = rememberScrollState()
  var showLessonSheet by remember { mutableStateOf(false) }
  var showGoalDialog by remember { mutableStateOf(false) }
  var showCelebrationBanner by remember { mutableStateOf(false) }

  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background),
    contentAlignment = Alignment.TopCenter
  ) {
    Column(
      modifier = Modifier
        .widthIn(max = 600.dp)
        .fillMaxSize()
        .verticalScroll(scrollState)
        .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
      // Top header with language switch & stats
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Selected Language Chip
        Surface(
          shape = RoundedCornerShape(20.dp),
          color = MaterialTheme.colorScheme.surface,
          border = CardDefaults.outlinedCardBorder(),
          modifier = Modifier.clickable(onClick = onNavigateToLanguageSelection)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Text(text = learningLanguage.flagEmoji, fontSize = 20.sp)
            Text(
              text = learningLanguage.name,
              style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Icon(
              imageVector = Icons.Filled.SwapHoriz,
              contentDescription = "Switch language",
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(18.dp)
            )
          }
        }

        // Stats row
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          StatChip(
            icon = Icons.Filled.LocalFireDepartment,
            iconTint = AmberSecondary,
            label = "Streak",
            value = "${preferences.streakDays}d"
          )
          StatChip(
            icon = Icons.Filled.Star,
            iconTint = MaterialTheme.colorScheme.primary,
            label = "XP",
            value = "${preferences.xpPoints}"
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Celebration banner after finishing a lesson
      AnimatedVisibility(visible = showCelebrationBanner) {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = AccentGreen.copy(alpha = 0.15f)),
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 14.dp)
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(text = "🎉", fontSize = 28.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Great job! Lesson completed!",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = AccentGreen
              )
              Text(
                text = "+25 XP earned • Daily practice time updated",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
              )
            }
            IconButton(onClick = { showCelebrationBanner = false }) {
              Icon(imageVector = Icons.Filled.Close, contentDescription = "Dismiss")
            }
          }
        }
      }

      // Welcome Card showing Selected Learning Language
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.primary
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(
          modifier = Modifier.padding(20.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
          ) {
            Column {
              Text(
                text = "CURRENT LANGUAGE",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 1.sp
                ),
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "${learningLanguage.flagEmoji} ${learningLanguage.name}",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                color = MaterialTheme.colorScheme.onPrimary
              )
              Text(
                text = "${learningLanguage.nativeName} • ${learningLanguage.scriptType} Script",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
              )
            }

            Surface(
              shape = RoundedCornerShape(12.dp),
              color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f),
              modifier = Modifier.clickable(onClick = onNavigateToLanguageSelection)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "Change",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onPrimary
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Greeting showcase
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.15f),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(text = "💬", fontSize = 20.sp)
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "\"${learningLanguage.sampleGreeting}\"",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                  text = learningLanguage.greetingMeaning,
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Daily Goal Section
      DailyGoalCard(
        currentMinutes = preferences.currentDailyMinutes,
        goalMinutes = preferences.dailyGoalMinutes,
        onAdjustGoal = { showGoalDialog = true }
      )

      Spacer(modifier = Modifier.height(18.dp))

      // Today's Sample Lesson Card
      Text(
        text = "Today's Featured Lesson",
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onBackground
      )
      Spacer(modifier = Modifier.height(8.dp))

      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = CardDefaults.outlinedCardBorder()
      ) {
        Column(
          modifier = Modifier.padding(18.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            DifficultyBadge(difficulty = lesson.category)
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "⏱️ ${lesson.durationMinutes} min",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "⭐ +${lesson.xpReward} XP",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = AmberSecondary
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = lesson.title,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = "Master basic conversational greetings and polite responses in ${learningLanguage.name}.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(14.dp))

          // Mini phrase preview
          val firstPhrase = lesson.phrases.firstOrNull()
          if (firstPhrase != null) {
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Surface(
                  shape = CircleShape,
                  color = MaterialTheme.colorScheme.primaryContainer,
                  modifier = Modifier.size(32.dp)
                ) {
                  Box(contentAlignment = Alignment.Center) {
                    Text(text = "1", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                  }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(
                    text = firstPhrase.original,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  Text(
                    text = "${firstPhrase.pronunciation} • ${firstPhrase.translation}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Continue Learning Button
          Button(
            onClick = {
              if (onStartCourseLesson != null) {
                onStartCourseLesson()
              } else {
                showLessonSheet = true
              }
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .testTag("continue_learning_button"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
          ) {
            Icon(
              imageVector = Icons.Filled.PlayArrow,
              contentDescription = null,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Continue Learning",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Learning Progress Overview
      Text(
        text = "Your Progress",
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onBackground
      )
      Spacer(modifier = Modifier.height(8.dp))

      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
      ) {
        Column(
          modifier = Modifier.padding(18.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Beginner Track: A1 Foundation",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Stage 1 of 5 completed",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = MaterialTheme.colorScheme.primaryContainer
            ) {
              Text(
                text = "35%",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          LinearProgressIndicator(
            progress = { 0.35f },
            modifier = Modifier
              .fillMaxWidth()
              .height(8.dp)
              .clip(RoundedCornerShape(4.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
          )

          Spacer(modifier = Modifier.height(16.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            ProgressStatItem(
              title = "Words Learned",
              value = "${preferences.wordsLearned}",
              subtitle = "target: 100"
            )
            ProgressStatItem(
              title = "Time Practiced",
              value = "${preferences.currentDailyMinutes}m",
              subtitle = "today"
            )
            ProgressStatItem(
              title = "Streak",
              value = "${preferences.streakDays} days",
              subtitle = "personal best: 7"
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Quick Practice Activities
      Text(
        text = "Quick Practice",
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onBackground
      )
      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        QuickPracticeCard(
          icon = Icons.Filled.RecordVoiceOver,
          title = "Pronounce",
          subtitle = "Speak aloud",
          modifier = Modifier.weight(1f),
          onClick = {
            if (onStartSpeakingPractice != null) {
              onStartSpeakingPractice()
            } else {
              showLessonSheet = true
            }
          }
        )
        QuickPracticeCard(
          icon = Icons.Filled.Book,
          title = "Vocabulary",
          subtitle = "Flashcards",
          modifier = Modifier.weight(1f),
          onClick = { showLessonSheet = true }
        )
        QuickPracticeCard(
          icon = Icons.Filled.Headphones,
          title = "Listening",
          subtitle = "Audio cues",
          modifier = Modifier.weight(1f),
          onClick = {
            if (onStartListeningPractice != null) {
              onStartListeningPractice()
            } else {
              showLessonSheet = true
            }
          }
        )
      }

      Spacer(modifier = Modifier.height(28.dp))
    }
  }

  // Interactive Lesson Modal Sheet
  if (showLessonSheet) {
    ModalBottomSheet(
      onDismissRequest = { showLessonSheet = false },
      sheetState = sheetState
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 8.dp)
          .verticalScroll(rememberScrollState())
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = lesson.title,
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "${learningLanguage.flagEmoji} ${learningLanguage.name} • ${lesson.phrases.size} Key Phrases",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          IconButton(onClick = { showLessonSheet = false }) {
            Icon(imageVector = Icons.Filled.Close, contentDescription = "Close")
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Phrase Cards
        lesson.phrases.forEachIndexed { index, phrase ->
          PhraseInteractiveCard(
            phrase = phrase,
            index = index + 1,
            modifier = Modifier.padding(bottom = 12.dp)
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
          onClick = {
            onCompleteLesson(lesson.id)
            showLessonSheet = false
            showCelebrationBanner = true
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("mark_lesson_complete_button"),
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
          Icon(imageVector = Icons.Filled.CheckCircle, contentDescription = null)
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Complete Lesson (+25 XP)",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
          )
        }

        Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }

  // Edit Goal Dialog
  if (showGoalDialog) {
    var tempGoal by remember { mutableIntStateOf(preferences.dailyGoalMinutes) }
    val goalOptions = listOf(5, 10, 15, 20, 30)

    AlertDialog(
      onDismissRequest = { showGoalDialog = false },
      title = { Text("Set Daily Learning Goal") },
      text = {
        Column {
          Text(
            text = "How many minutes would you like to practice each day?",
            style = MaterialTheme.typography.bodyMedium
          )
          Spacer(modifier = Modifier.height(12.dp))
          goalOptions.forEach { minutes ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clickable { tempGoal = minutes }
                .padding(vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              RadioButton(
                selected = tempGoal == minutes,
                onClick = { tempGoal = minutes }
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "$minutes minutes / day",
                style = MaterialTheme.typography.bodyLarge.copy(
                  fontWeight = if (tempGoal == minutes) FontWeight.Bold else FontWeight.Normal
                )
              )
            }
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            onUpdateDailyGoal(tempGoal)
            showGoalDialog = false
          }
        ) {
          Text("Save Goal")
        }
      },
      dismissButton = {
        TextButton(onClick = { showGoalDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }
}

@Composable
private fun ProgressStatItem(
  title: String,
  value: String,
  subtitle: String
) {
  Column(horizontalAlignment = Alignment.Start) {
    Text(
      text = title,
      style = MaterialTheme.typography.labelSmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(modifier = Modifier.height(2.dp))
    Text(
      text = value,
      style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
      color = MaterialTheme.colorScheme.onSurface
    )
    Text(
      text = subtitle,
      style = MaterialTheme.typography.labelSmall,
      color = MaterialTheme.colorScheme.outline
    )
  }
}

@Composable
private fun QuickPracticeCard(
  icon: ImageVector,
  title: String,
  subtitle: String,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Card(
    modifier = modifier
      .clip(RoundedCornerShape(16.dp))
      .clickable(onClick = onClick),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    )
  ) {
    Column(
      modifier = Modifier.padding(12.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.size(36.dp)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
          )
        }
      }
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = title,
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface
      )
      Text(
        text = subtitle,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}
