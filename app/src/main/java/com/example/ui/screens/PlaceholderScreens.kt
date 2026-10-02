package com.example.ui.screens

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Course
import com.example.data.CourseLesson
import com.example.data.Language
import com.example.data.LanguageCatalog
import com.example.data.LessonCatalog
import com.example.data.UserPreferences
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AmberSecondary

/**
 * Learn Tab Screen showing course dashboard, Unit 1, and lesson launch.
 */
@Composable
fun LearnScreen(
  learningLanguage: Language,
  course: Course?,
  preferences: UserPreferences,
  onStartCourseLesson: (CourseLesson) -> Unit,
  onSwitchToSpanish: () -> Unit,
  onStartSpeakingPractice: (() -> Unit)? = null,
  onStartListeningPractice: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  val scrollState = rememberScrollState()

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
        .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
      // Top Course Title Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = if (course != null) course.title else "${learningLanguage.name} Course",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
          )
          Text(
            text = if (course != null) "${learningLanguage.flagEmoji} ${course.level} • ${course.subtitle}" else "${learningLanguage.flagEmoji} Beginner Track",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Voice Lab: Speaking & Listening Quick Practice
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ),
        border = CardDefaults.outlinedCardBorder()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(text = "🎙️", fontSize = 18.sp)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Voice Practice Lab",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
            }
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = MaterialTheme.colorScheme.primary
            ) {
              Text(
                text = "VOICE",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = "Practice speaking aloud with real-time speech comparison, or sharpen your ear with audio comprehension exercises.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Button(
              onClick = { onStartSpeakingPractice?.invoke() },
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
              Icon(imageVector = Icons.Filled.Mic, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Speaking", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
            }

            OutlinedButton(
              onClick = { onStartListeningPractice?.invoke() },
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(12.dp)
            ) {
              Icon(imageVector = Icons.Filled.Headphones, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Listening", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      if (course != null) {
        // Active Unit 1 Card with Everyday Greetings lesson
        val unit1 = course.units.firstOrNull()
        if (unit1 != null) {
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = CardDefaults.outlinedCardBorder(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
          ) {
            Column(modifier = Modifier.padding(18.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = MaterialTheme.colorScheme.primaryContainer
                ) {
                  Text(
                    text = "UNIT ${unit1.unitNumber} • ACTIVE",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                  )
                }

                Icon(
                  imageVector = Icons.Filled.School,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary
                )
              }

              Spacer(modifier = Modifier.height(10.dp))

              Text(
                text = unit1.title,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )

              Spacer(modifier = Modifier.height(4.dp))

              Text(
                text = unit1.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )

              Spacer(modifier = Modifier.height(16.dp))

              // Lesson List inside Unit 1
              unit1.lessons.forEach { lesson ->
                val isCompleted = lesson.id in preferences.completedLessons
                val bestScore = preferences.lessonScores[lesson.id]

                Card(
                  modifier = Modifier.fillMaxWidth(),
                  shape = RoundedCornerShape(16.dp),
                  colors = CardDefaults.cardColors(
                    containerColor = if (isCompleted) {
                      MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                    } else {
                      MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    }
                  ),
                  border = CardDefaults.outlinedCardBorder()
                ) {
                  Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                          shape = CircleShape,
                          color = if (isCompleted) AccentGreen else MaterialTheme.colorScheme.primary,
                          modifier = Modifier.size(32.dp)
                        ) {
                          Box(contentAlignment = Alignment.Center) {
                            Icon(
                              imageVector = if (isCompleted) Icons.Filled.CheckCircle else Icons.Filled.PlayArrow,
                              contentDescription = null,
                              tint = Color.White,
                              modifier = Modifier.size(18.dp)
                            )
                          }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                          Text(
                            text = lesson.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                          )
                          Text(
                            text = "${lesson.durationMinutes} min • +${lesson.xpReward} XP • ${lesson.vocabulary.size} Words",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                          )
                        }
                      }

                      if (isCompleted && bestScore != null) {
                        Surface(
                          shape = RoundedCornerShape(8.dp),
                          color = AccentGreen.copy(alpha = 0.15f)
                        ) {
                          Text(
                            text = "Score: $bestScore%",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = AccentGreen,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                          )
                        }
                      }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                      text = lesson.description,
                      style = MaterialTheme.typography.bodySmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                      onClick = { onStartCourseLesson(lesson) },
                      modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("start_lesson_button"),
                      shape = RoundedCornerShape(12.dp),
                      colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                      Icon(
                        imageVector = if (isCompleted) Icons.Filled.PlayArrow else Icons.Filled.School,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                      )
                      Spacer(modifier = Modifier.width(8.dp))
                      Text(
                        text = if (isCompleted) "Practice Again / Review" else "Start Everyday Greetings",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                      )
                    }
                  }
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Remaining Locked Units (Unit 2 & 3)
        course.units.drop(1).forEach { unit ->
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .padding(bottom = 12.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
              containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
            ),
            border = CardDefaults.outlinedCardBorder()
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                ) {
                  Text(
                    text = "UNIT ${unit.unitNumber}",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                  )
                }

                Icon(
                  imageVector = Icons.Filled.Lock,
                  contentDescription = "Locked",
                  tint = MaterialTheme.colorScheme.outline
                )
              }

              Spacer(modifier = Modifier.height(8.dp))

              Text(
                text = unit.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
              )

              Spacer(modifier = Modifier.height(4.dp))

              Text(
                text = unit.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
              )
            }
          }
        }

      } else {
        // Transparent Coming Soon State for languages without verified full course yet
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(22.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = CardDefaults.outlinedCardBorder()
        ) {
          Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Surface(
              shape = CircleShape,
              color = MaterialTheme.colorScheme.primaryContainer,
              modifier = Modifier.size(60.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Text(text = learningLanguage.flagEmoji, fontSize = 32.sp)
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Surface(
              shape = RoundedCornerShape(12.dp),
              color = AmberSecondary.copy(alpha = 0.15f)
            ) {
              Text(
                text = "COURSE CONTENT IN PREPARATION",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = AmberSecondary,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
              )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
              text = "${learningLanguage.name} Beginner Course",
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface,
              textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
              text = "Verified interactive curriculum for ${learningLanguage.name} is currently in production with native linguists to ensure accurate cultural idioms, grammar, and native audio. We do not use unverified auto-generated courses.",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            Card(
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
              ),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Text(
                  text = "🇪🇸 Practice Spanish in the Meantime",
                  style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "Our verified Spanish course has the complete interactive lesson ready with vocabulary audio, pronunciation, fill-in-the-blank, and multiple-choice quizzes.",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurface
                )
              }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Button(
              onClick = onSwitchToSpanish,
              modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("switch_to_spanish_button"),
              shape = RoundedCornerShape(14.dp),
              colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
              Icon(imageVector = Icons.Filled.SwapHoriz, contentDescription = null)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Switch to Spanish Course",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(28.dp))
    }
  }
}



/**
 * Progress Tab Screen showing learner statistics, streak history, and achievements.
 */
@Composable
fun ProgressScreen(
  preferences: UserPreferences,
  learningLanguage: Language,
  modifier: Modifier = Modifier
) {
  val scrollState = rememberScrollState()

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
        .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
      Text(
        text = "Learning Statistics",
        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onBackground
      )
      Text(
        text = "Your journey with ${learningLanguage.flagEmoji} ${learningLanguage.name}",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Spacer(modifier = Modifier.height(18.dp))

      // 4-metric grid
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        MetricCard(
          title = "Streak",
          value = "${preferences.streakDays} Days",
          sub = "Current Streak",
          emoji = "🔥",
          modifier = Modifier.weight(1f)
        )
        MetricCard(
          title = "Words",
          value = "${preferences.wordsLearned}",
          sub = "Vocabulary",
          emoji = "📚",
          modifier = Modifier.weight(1f)
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        MetricCard(
          title = "Total XP",
          value = "${preferences.xpPoints}",
          sub = "Earned",
          emoji = "⭐",
          modifier = Modifier.weight(1f)
        )
        val calculatedAccuracy = if (preferences.lessonScores.isNotEmpty()) {
          "${preferences.lessonScores.values.average().toInt()}%"
        } else {
          "100%"
        }
        MetricCard(
          title = "Accuracy",
          value = calculatedAccuracy,
          sub = if (preferences.lessonScores.isNotEmpty()) "Quiz average" else "Practice score",
          emoji = "🎯",
          modifier = Modifier.weight(1f)
        )
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Weekly Activity Chart Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
      ) {
        Column(
          modifier = Modifier.padding(18.dp)
        ) {
          Text(
            text = "This Week's Practice (Minutes)",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.height(14.dp))

          // 7-day bar chart
          val days = listOf("Mon" to 12, "Tue" to 15, "Wed" to 8, "Thu" to 14, "Fri" to 10, "Sat" to 20, "Sun" to preferences.currentDailyMinutes)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
          ) {
            days.forEach { (day, minutes) ->
              Column(
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(
                  text = "${minutes}m",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                  modifier = Modifier
                    .width(24.dp)
                    .height((minutes * 3.5).coerceIn(16.0, 80.0).dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                      if (day == "Sun") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = day,
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onSurface
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Achievements
      Text(
        text = "Milestones & Badges",
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onBackground
      )
      Spacer(modifier = Modifier.height(10.dp))

      AchievementRow(
        title = "First Words Explorer",
        description = "Completed your first conversational lesson",
        isCompleted = preferences.completedLessons.isNotEmpty()
      )
      Spacer(modifier = Modifier.height(8.dp))
      AchievementRow(
        title = "Everyday Greetings Master",
        description = "Completed the Spanish Everyday Greetings beginner lesson",
        isCompleted = "es_u1_l1" in preferences.completedLessons
      )
      Spacer(modifier = Modifier.height(8.dp))
      AchievementRow(
        title = "3-Day Consistency Streak",
        description = "Practiced 3 consecutive days in a row",
        isCompleted = preferences.streakDays >= 3
      )
      Spacer(modifier = Modifier.height(8.dp))
      AchievementRow(
        title = "Polyglot Horizon",
        description = "Explore lessons across world languages",
        isCompleted = preferences.completedLessons.size >= 3
      )

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
private fun MetricCard(
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
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(text = title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = emoji, fontSize = 18.sp)
      }
      Spacer(modifier = Modifier.height(6.dp))
      Text(text = value, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold), color = MaterialTheme.colorScheme.onSurface)
      Text(text = sub, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
    }
  }
}

@Composable
private fun AchievementRow(
  title: String,
  description: String,
  isCompleted: Boolean
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isCompleted) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    ),
    border = CardDefaults.outlinedCardBorder()
  ) {
    Row(
      modifier = Modifier.padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Surface(
        shape = CircleShape,
        color = if (isCompleted) AmberSecondary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
        modifier = Modifier.size(38.dp)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = Icons.Filled.EmojiEvents,
            contentDescription = null,
            tint = if (isCompleted) AmberSecondary else MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(20.dp)
          )
        }
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = title,
          style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = description,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      if (isCompleted) {
        Icon(
          imageVector = Icons.Filled.CheckCircle,
          contentDescription = "Completed",
          tint = AccentGreen,
          modifier = Modifier.size(20.dp)
        )
      }
    }
  }
}


