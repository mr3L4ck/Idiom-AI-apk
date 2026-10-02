package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Language
import com.example.data.ListeningExercise
import com.example.data.VoiceLearningCatalog
import com.example.ui.theme.AccentCoral
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AmberSecondary
import com.example.util.rememberTextToSpeechHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListeningPracticeScreen(
  learningLanguage: Language,
  nativeLanguage: Language,
  onFinish: (correctCount: Int, totalCount: Int, xpEarned: Int) -> Unit,
  onExit: () -> Unit,
  modifier: Modifier = Modifier
) {
  val exercises = remember(learningLanguage.id) {
    VoiceLearningCatalog.getListeningExercises(learningLanguage.id)
  }

  var currentIndex by remember { mutableIntStateOf(0) }
  val currentExercise = exercises.getOrElse(currentIndex) {
    ListeningExercise("fallback", "Hola", "What did you hear?", listOf("Hello", "Goodbye"), 0, "Hello", "OH-lah", "Greeting")
  }

  val ttsHelper = rememberTextToSpeechHelper()
  var isSlowPlayback by remember { mutableStateOf(false) }
  var playCount by remember { mutableIntStateOf(0) }

  // State for user's selected answer
  var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
  var isAnswerChecked by remember { mutableStateOf(false) }
  var correctScore by remember { mutableIntStateOf(0) }

  // Auto-play audio when first loading an exercise
  LaunchedEffect(currentIndex, ttsHelper.isReady.value) {
    if (ttsHelper.isReady.value) {
      val rate = if (isSlowPlayback) 0.75f else 1.0f
      ttsHelper.speak(currentExercise.spokenPhrase, learningLanguage.id, rate)
      playCount++
    }
  }

  BackHandler {
    ttsHelper.stop()
    onExit()
  }

  // Animation for wave effect while playing audio
  val infiniteTransition = rememberInfiniteTransition(label = "audioWave")
  val waveScale by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = 1.2f,
    animationSpec = infiniteRepeatable(
      animation = tween(600, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "waveScale"
  )

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background),
    contentAlignment = Alignment.TopCenter
  ) {
    Column(
      modifier = Modifier
        .widthIn(max = 640.dp)
        .fillMaxSize()
    ) {
      // Top Navigation Bar
      TopAppBar(
        title = {
          Column {
            Text(
              text = "Listening Practice",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "${learningLanguage.flagEmoji} ${learningLanguage.name} • Exercise ${currentIndex + 1} of ${exercises.size}",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        },
        navigationIcon = {
          IconButton(
            onClick = {
              ttsHelper.stop()
              onExit()
            },
            modifier = Modifier.testTag("listening_back_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Exit listening practice"
            )
          }
        },
        actions = {
          TextButton(
            onClick = {
              ttsHelper.stop()
              val earnedXp = (correctScore * 10).coerceAtLeast(15)
              onFinish(correctScore, exercises.size, earnedXp)
            }
          ) {
            Text(
              text = "Done",
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )

      // Step Progress Bar
      LinearProgressIndicator(
        progress = { (currentIndex + 1).toFloat() / exercises.size.toFloat() },
        modifier = Modifier
          .fillMaxWidth()
          .height(4.dp),
        color = MaterialTheme.colorScheme.primary,
        trackColor = MaterialTheme.colorScheme.surfaceVariant
      )

      Column(
        modifier = Modifier
          .fillMaxSize()
          .verticalScroll(rememberScrollState())
          .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {

        // Big Audio Card
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("audio_player_card"),
          shape = RoundedCornerShape(24.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
          ),
          border = CardDefaults.outlinedCardBorder(),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            // Headphones icon badge
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Filled.Headphones,
                  contentDescription = null,
                  modifier = Modifier.size(14.dp),
                  tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "AUDIO COMPREHENSION",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onPrimaryContainer
                )
              }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Animated Play Audio Button
            Box(
              contentAlignment = Alignment.Center,
              modifier = Modifier.size(96.dp)
            ) {
              if (ttsHelper.isSpeaking.value) {
                Surface(
                  shape = CircleShape,
                  color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                  modifier = Modifier
                    .fillMaxSize()
                    .scale(waveScale)
                ) {}
              }

              Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                  .size(76.dp)
                  .clip(CircleShape)
                  .clickable {
                    val rate = if (isSlowPlayback) 0.75f else 1.0f
                    ttsHelper.speak(currentExercise.spokenPhrase, learningLanguage.id, rate)
                    playCount++
                  }
                  .testTag("play_listening_audio_button"),
                shadowElevation = 4.dp
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Icon(
                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = "Play spoken audio",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
              text = if (ttsHelper.isSpeaking.value) "Speaking audio..." else "Tap to listen again",
              style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
              color = if (ttsHelper.isSpeaking.value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Playback Speed Toggle & Play Count
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.Center,
              verticalAlignment = Alignment.CenterVertically
            ) {
              OutlinedButton(
                onClick = {
                  isSlowPlayback = !isSlowPlayback
                  val newRate = if (isSlowPlayback) 0.75f else 1.0f
                  ttsHelper.setSpeechRate(newRate)
                  ttsHelper.speak(currentExercise.spokenPhrase, learningLanguage.id, newRate)
                  playCount++
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("listening_rate_toggle")
              ) {
                Icon(
                  imageVector = Icons.Filled.Speed,
                  contentDescription = null,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = if (isSlowPlayback) "Slower (0.75x)" else "Normal (1.0x)",
                  style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
              }

              Spacer(modifier = Modifier.width(16.dp))

              Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    imageVector = Icons.Filled.Replay,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "$playCount plays",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }
            }

            // Reported TTS status message if accent voice missing
            if (ttsHelper.voiceStatusMessage.value != null) {
              Spacer(modifier = Modifier.height(10.dp))
              Text(
                text = ttsHelper.voiceStatusMessage.value ?: "",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline,
                textAlign = TextAlign.Center
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Question Title
        Text(
          text = currentExercise.promptQuestion,
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onBackground,
          textAlign = TextAlign.Start,
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Multiple Choice Options List
        currentExercise.options.forEachIndexed { optionIndex, optionText ->
          val isSelected = selectedOptionIndex == optionIndex
          val isCorrectOption = optionIndex == currentExercise.correctOptionIndex

          val cardBorderColor = when {
            isAnswerChecked && isCorrectOption -> AccentGreen
            isAnswerChecked && isSelected && !isCorrectOption -> AccentCoral
            isSelected -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
          }

          val cardBgColor = when {
            isAnswerChecked && isCorrectOption -> AccentGreen.copy(alpha = 0.12f)
            isAnswerChecked && isSelected && !isCorrectOption -> AccentCoral.copy(alpha = 0.12f)
            isSelected -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
            else -> MaterialTheme.colorScheme.surface
          }

          Card(
            modifier = Modifier
              .fillMaxWidth()
              .padding(bottom = 10.dp)
              .clip(RoundedCornerShape(16.dp))
              .border(
                width = if (isSelected || (isAnswerChecked && isCorrectOption)) 2.dp else 1.dp,
                color = cardBorderColor,
                shape = RoundedCornerShape(16.dp)
              )
              .clickable(enabled = !isAnswerChecked) {
                selectedOptionIndex = optionIndex
              }
              .testTag("listening_option_$optionIndex"),
            colors = CardDefaults.cardColors(containerColor = cardBgColor)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              // Selection indicator icon
              Surface(
                shape = CircleShape,
                color = when {
                  isAnswerChecked && isCorrectOption -> AccentGreen
                  isAnswerChecked && isSelected && !isCorrectOption -> AccentCoral
                  isSelected -> MaterialTheme.colorScheme.primary
                  else -> MaterialTheme.colorScheme.surfaceVariant
                },
                modifier = Modifier.size(24.dp)
              ) {
                Box(contentAlignment = Alignment.Center) {
                  if (isAnswerChecked && isCorrectOption) {
                    Icon(
                      imageVector = Icons.Filled.Check,
                      contentDescription = "Correct",
                      tint = Color.White,
                      modifier = Modifier.size(16.dp)
                    )
                  } else if (isAnswerChecked && isSelected && !isCorrectOption) {
                    Icon(
                      imageVector = Icons.Filled.Close,
                      contentDescription = "Incorrect",
                      tint = Color.White,
                      modifier = Modifier.size(16.dp)
                    )
                  } else if (isSelected) {
                    Surface(
                      shape = CircleShape,
                      color = Color.White,
                      modifier = Modifier.size(10.dp)
                    ) {}
                  }
                }
              }

              Spacer(modifier = Modifier.width(14.dp))

              Text(
                text = optionText,
                style = MaterialTheme.typography.bodyLarge.copy(
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
              )
            }
          }
        }

        // Post-Answer Feedback & Phrase Reveal
        AnimatedVisibility(visible = isAnswerChecked) {
          val isCorrect = selectedOptionIndex == currentExercise.correctOptionIndex

          Card(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 10.dp, bottom = 14.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
              containerColor = if (isCorrect) AccentGreen.copy(alpha = 0.12f) else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f)
            ),
            border = CardDefaults.outlinedCardBorder()
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = if (isCorrect) Icons.Filled.CheckCircle else Icons.Filled.Info,
                  contentDescription = null,
                  tint = if (isCorrect) AccentGreen else MaterialTheme.colorScheme.error,
                  modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = if (isCorrect) "Correct! Well done." else "Incorrect answer",
                  style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                  color = if (isCorrect) AccentGreen else MaterialTheme.colorScheme.error
                )
              }

              Spacer(modifier = Modifier.height(8.dp))

              Text(
                text = "Spoken Phrase: \"${currentExercise.spokenPhrase}\"",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )

              if (currentExercise.pronunciation.isNotBlank()) {
                Text(
                  text = "Pronunciation: ${currentExercise.pronunciation}",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.primary
                )
              }

              Spacer(modifier = Modifier.height(4.dp))

              Text(
                text = currentExercise.contextExplanation,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Action Buttons: Check Answer vs Next Question
        if (!isAnswerChecked) {
          Button(
            onClick = {
              if (selectedOptionIndex != null) {
                isAnswerChecked = true
                if (selectedOptionIndex == currentExercise.correctOptionIndex) {
                  correctScore++
                }
              }
            },
            enabled = selectedOptionIndex != null,
            modifier = Modifier
              .fillMaxWidth()
              .height(50.dp)
              .testTag("check_answer_button"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
          ) {
            Text(
              text = "Check Answer",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
          }
        } else {
          Button(
            onClick = {
              if (currentIndex < exercises.size - 1) {
                currentIndex++
                selectedOptionIndex = null
                isAnswerChecked = false
                playCount = 0
              } else {
                ttsHelper.stop()
                val earnedXp = (correctScore * 10).coerceAtLeast(20)
                onFinish(correctScore, exercises.size, earnedXp)
              }
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(50.dp)
              .testTag("next_exercise_button"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
          ) {
            Text(
              text = if (currentIndex < exercises.size - 1) "Next Exercise" else "Complete Practice",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowForward,
              contentDescription = null,
              modifier = Modifier.size(20.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }
}
