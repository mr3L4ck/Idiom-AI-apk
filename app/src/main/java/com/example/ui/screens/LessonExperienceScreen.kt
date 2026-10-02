package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CourseLesson
import com.example.data.LessonQuestion
import com.example.data.LessonVocabulary
import com.example.data.QuestionType
import com.example.ui.theme.AccentCoral
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AmberSecondary
import com.example.util.rememberTextToSpeechHelper

enum class LessonStage {
  VOCABULARY,
  QUIZ,
  RESULTS
}

@Composable
fun LessonExperienceScreen(
  lesson: CourseLesson,
  languageCode: String,
  languageName: String,
  onFinishAndSave: (scorePercent: Int, correctCount: Int, totalQuestions: Int) -> Unit,
  onExit: () -> Unit,
  modifier: Modifier = Modifier
) {
  var stage by remember { mutableStateOf(LessonStage.VOCABULARY) }
  var showExitConfirmDialog by remember { mutableStateOf(false) }

  // TTS helper for pronunciation
  val ttsHelper = rememberTextToSpeechHelper()

  // Quiz state
  var currentQuestionIndex by remember { mutableIntStateOf(0) }
  val userAnswers = remember { mutableStateMapOf<Int, String>() }
  val checkedAnswers = remember { mutableStateMapOf<Int, Boolean>() } // questionIndex -> isAnswerChecked
  val isAnswerCorrect = remember { mutableStateMapOf<Int, Boolean>() } // questionIndex -> wasCorrect

  // Handle back press
  BackHandler {
    if (stage == LessonStage.RESULTS) {
      val correctCount = isAnswerCorrect.values.count { it }
      val total = lesson.questions.size
      val scorePercent = if (total > 0) (correctCount * 100) / total else 0
      onFinishAndSave(scorePercent, correctCount, total)
    } else {
      showExitConfirmDialog = true
    }
  }

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
      // Top Lesson App Bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        IconButton(
          onClick = {
            if (stage == LessonStage.RESULTS) {
              val correctCount = isAnswerCorrect.values.count { it }
              val total = lesson.questions.size
              val scorePercent = if (total > 0) (correctCount * 100) / total else 0
              onFinishAndSave(scorePercent, correctCount, total)
            } else {
              showExitConfirmDialog = true
            }
          },
          modifier = Modifier.testTag("exit_lesson_button")
        ) {
          Icon(
            imageVector = Icons.Filled.Close,
            contentDescription = "Exit lesson",
            tint = MaterialTheme.colorScheme.onBackground
          )
        }

        // Title and phase
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = lesson.title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
          )
          Text(
            text = when (stage) {
              LessonStage.VOCABULARY -> "Step 1: Vocabulary & Audio"
              LessonStage.QUIZ -> "Step 2: Interactive Practice"
              LessonStage.RESULTS -> "Lesson Summary"
            },
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary
          )
        }

        // XP Chip
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = AmberSecondary.copy(alpha = 0.15f),
          modifier = Modifier.padding(end = 8.dp)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Filled.Star,
              contentDescription = null,
              tint = AmberSecondary,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "+${lesson.xpReward} XP",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = AmberSecondary
            )
          }
        }
      }

      // Progress bar
      val progress = when (stage) {
        LessonStage.VOCABULARY -> 0.25f
        LessonStage.QUIZ -> 0.25f + (0.65f * (currentQuestionIndex.toFloat() / lesson.questions.size.coerceAtLeast(1)))
        LessonStage.RESULTS -> 1f
      }
      LinearProgressIndicator(
        progress = { progress },
        modifier = Modifier
          .fillMaxWidth()
          .height(4.dp),
        color = MaterialTheme.colorScheme.primary,
        trackColor = MaterialTheme.colorScheme.surfaceVariant
      )

      // Screen Content by Stage
      when (stage) {
        LessonStage.VOCABULARY -> {
          VocabularySection(
            lesson = lesson,
            languageCode = languageCode,
            onSpeak = { text -> ttsHelper.speak(text, languageCode) },
            isTtsSpeaking = ttsHelper.isSpeaking.value,
            onStartQuiz = { stage = LessonStage.QUIZ }
          )
        }

        LessonStage.QUIZ -> {
          QuizSection(
            questions = lesson.questions,
            currentIndex = currentQuestionIndex,
            userAnswer = userAnswers[currentQuestionIndex],
            isAnswerChecked = checkedAnswers[currentQuestionIndex] == true,
            wasCorrect = isAnswerCorrect[currentQuestionIndex] == true,
            onAnswerSelected = { answer ->
              if (checkedAnswers[currentQuestionIndex] != true) {
                userAnswers[currentQuestionIndex] = answer
              }
            },
            onCheckAnswer = {
              val currentQuestion = lesson.questions[currentQuestionIndex]
              val selected = userAnswers[currentQuestionIndex] ?: ""
              val correct = selected.trim().equals(currentQuestion.correctAnswer.trim(), ignoreCase = true)
              checkedAnswers[currentQuestionIndex] = true
              isAnswerCorrect[currentQuestionIndex] = correct
            },
            onNextQuestion = {
              if (currentQuestionIndex < lesson.questions.size - 1) {
                currentQuestionIndex++
              } else {
                stage = LessonStage.RESULTS
              }
            },
            onPreviousQuestion = {
              if (currentQuestionIndex > 0) {
                currentQuestionIndex--
              }
            },
            onSpeak = { text -> ttsHelper.speak(text, languageCode) }
          )
        }

        LessonStage.RESULTS -> {
          val totalQuestions = lesson.questions.size
          val correctCount = isAnswerCorrect.values.count { it }
          val scorePercent = if (totalQuestions > 0) (correctCount * 100) / totalQuestions else 0

          ResultsSection(
            lesson = lesson,
            scorePercent = scorePercent,
            correctCount = correctCount,
            totalQuestions = totalQuestions,
            userAnswers = userAnswers,
            isAnswerCorrect = isAnswerCorrect,
            onRetry = {
              // Reset quiz state
              currentQuestionIndex = 0
              userAnswers.clear()
              checkedAnswers.clear()
              isAnswerCorrect.clear()
              stage = LessonStage.QUIZ
            },
            onFinish = {
              onFinishAndSave(scorePercent, correctCount, totalQuestions)
            }
          )
        }
      }
    }
  }

  // Exit Confirmation Dialog
  if (showExitConfirmDialog) {
    AlertDialog(
      onDismissRequest = { showExitConfirmDialog = false },
      title = { Text("Exit Lesson?") },
      text = {
        Text("Your quiz answers and score will not be saved if you exit now. Are you sure you want to exit?")
      },
      confirmButton = {
        TextButton(
          onClick = {
            showExitConfirmDialog = false
            onExit()
          }
        ) {
          Text("Exit", color = MaterialTheme.colorScheme.error)
        }
      },
      dismissButton = {
        Button(onClick = { showExitConfirmDialog = false }) {
          Text("Keep Learning")
        }
      }
    )
  }
}

/**
 * Vocabulary Section with 5 greetings, pronunciations, audio playback, and example sentences.
 */
@Composable
private fun VocabularySection(
  lesson: CourseLesson,
  languageCode: String,
  onSpeak: (String) -> Unit,
  isTtsSpeaking: Boolean,
  onStartQuiz: () -> Unit
) {
  val scrollState = rememberScrollState()

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(scrollState)
      .padding(horizontal = 20.dp, vertical = 14.dp),
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    Column {
      // Header instructions
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
        ),
        border = CardDefaults.outlinedCardBorder()
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(36.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Filled.School,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(20.dp)
              )
            }
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = "Learn the 5 Everyday Greetings",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Listen to native pronunciation, review translations, and then take the interactive practice quiz.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Vocabulary Cards List
      lesson.vocabulary.forEachIndexed { index, item ->
        VocabularyItemCard(
          item = item,
          index = index + 1,
          onSpeak = { onSpeak(item.audioText) },
          modifier = Modifier.padding(bottom = 12.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Start Quiz Button
    Button(
      onClick = onStartQuiz,
      modifier = Modifier
        .fillMaxWidth()
        .height(54.dp)
        .testTag("start_quiz_button"),
      shape = RoundedCornerShape(16.dp),
      colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
      Text(
        text = "Start Practice Quiz (${lesson.questions.size} Questions)",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
      )
      Spacer(modifier = Modifier.width(8.dp))
      Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
    }
  }
}

@Composable
private fun VocabularyItemCard(
  item: LessonVocabulary,
  index: Int,
  onSpeak: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = CardDefaults.outlinedCardBorder(),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Column(
      modifier = Modifier.padding(16.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(28.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Text(
                text = "$index",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onPrimaryContainer
              )
            }
          }
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Phonetic: / ${item.phonetic} /",
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.primary
          )
        }

        // TTS Pronunciation Button
        Surface(
          shape = CircleShape,
          color = MaterialTheme.colorScheme.surfaceVariant,
          modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .clickable(onClick = onSpeak)
            .testTag("tts_button_${item.id}")
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.VolumeUp,
              contentDescription = "Listen to pronunciation",
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(22.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Main Term
      Text(
        text = item.term,
        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
        color = MaterialTheme.colorScheme.onSurface
      )

      // Translation
      Text(
        text = item.translation,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Example sentence in target language
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(10.dp)) {
          Text(
            text = "💬 \"${item.exampleSentence}\"",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "Translation: ${item.exampleTranslation}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Cultural usage tip
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Filled.Lightbulb,
          contentDescription = null,
          tint = AmberSecondary,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = item.culturalNote,
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }
  }
}

/**
 * Quiz Section with multiple-choice and fill-in-the-blank questions.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun QuizSection(
  questions: List<LessonQuestion>,
  currentIndex: Int,
  userAnswer: String?,
  isAnswerChecked: Boolean,
  wasCorrect: Boolean,
  onAnswerSelected: (String) -> Unit,
  onCheckAnswer: () -> Unit,
  onNextQuestion: () -> Unit,
  onPreviousQuestion: () -> Unit,
  onSpeak: (String) -> Unit
) {
  val question = questions[currentIndex]
  val scrollState = rememberScrollState()

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(scrollState)
      .padding(horizontal = 20.dp, vertical = 14.dp),
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    Column {
      // Question counter & type badge
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "QUESTION ${currentIndex + 1} OF ${questions.size}",
          style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
          ),
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = MaterialTheme.colorScheme.primaryContainer
        ) {
          Text(
            text = when (question.type) {
              QuestionType.MULTIPLE_CHOICE -> "Multiple Choice"
              QuestionType.FILL_IN_THE_BLANK -> "Fill in the Blank"
            },
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Question Prompt Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Text(
            text = question.prompt,
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Bold,
              lineHeight = 28.sp
            ),
            color = MaterialTheme.colorScheme.onSurface
          )

          // If fill-in-the-blank has a context sentence
          if (question.contextSentence != null) {
            Spacer(modifier = Modifier.height(14.dp))
            val displayedSentence = if (!userAnswer.isNullOrEmpty()) {
              question.contextSentence.replace("______", "[ $userAnswer ]")
            } else {
              question.contextSentence
            }

            Surface(
              shape = RoundedCornerShape(12.dp),
              color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  text = displayedSentence,
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (!userAnswer.isNullOrEmpty()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                  )
                )

                if (userAnswer != null && !isAnswerChecked) {
                  Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                    modifier = Modifier
                      .size(28.dp)
                      .clip(CircleShape)
                      .clickable { onAnswerSelected("") }
                  ) {
                    Box(contentAlignment = Alignment.Center) {
                      Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Clear answer",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                      )
                    }
                  }
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Answer Options / Word Bank
      if (question.type == QuestionType.MULTIPLE_CHOICE) {
        // Multiple Choice Options List
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          question.options.forEach { option ->
            val isSelected = userAnswer == option
            val borderColor = when {
              isAnswerChecked && isSelected && wasCorrect -> AccentGreen
              isAnswerChecked && isSelected && !wasCorrect -> AccentCoral
              isAnswerChecked && option == question.correctAnswer -> AccentGreen
              isSelected -> MaterialTheme.colorScheme.primary
              else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
            }
            val bgColor = when {
              isAnswerChecked && isSelected && wasCorrect -> AccentGreen.copy(alpha = 0.12f)
              isAnswerChecked && isSelected && !wasCorrect -> AccentCoral.copy(alpha = 0.12f)
              isAnswerChecked && option == question.correctAnswer -> AccentGreen.copy(alpha = 0.12f)
              isSelected -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
              else -> MaterialTheme.colorScheme.surface
            }

            Card(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(
                  width = if (isSelected || (isAnswerChecked && option == question.correctAnswer)) 2.dp else 1.dp,
                  color = borderColor,
                  shape = RoundedCornerShape(16.dp)
                )
                .clickable(enabled = !isAnswerChecked) {
                  onAnswerSelected(option)
                }
                .testTag("option_${option.replace(" ", "_")}"),
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(containerColor = bgColor)
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = if (isSelected) Icons.Filled.CheckCircle else Icons.Outlined.Circle,
                    contentDescription = null,
                    tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(22.dp)
                  )
                  Spacer(modifier = Modifier.width(12.dp))
                  Text(
                    text = option,
                    style = MaterialTheme.typography.bodyLarge.copy(
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                  )
                }

                // Audio listen icon for foreign words
                IconButton(
                  onClick = { onSpeak(option) },
                  modifier = Modifier.size(32.dp)
                ) {
                  Icon(
                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = "Listen",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.size(18.dp)
                  )
                }
              }
            }
          }
        }
      } else {
        // Fill in the blank: Interactive Word Bank Chips + Optional Manual Text Field
        Column {
          Text(
            text = "Select the missing word from the bank below:",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(10.dp))

          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            question.options.forEach { word ->
              val isSelected = userAnswer == word
              val chipBg = when {
                isAnswerChecked && isSelected && wasCorrect -> AccentGreen
                isAnswerChecked && isSelected && !wasCorrect -> AccentCoral
                isSelected -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.surfaceVariant
              }
              val chipTextColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface

              Surface(
                shape = RoundedCornerShape(12.dp),
                color = chipBg,
                border = if (!isSelected) CardDefaults.outlinedCardBorder() else null,
                modifier = Modifier
                  .clip(RoundedCornerShape(12.dp))
                  .clickable(enabled = !isAnswerChecked) {
                    onAnswerSelected(word)
                  }
                  .testTag("word_chip_$word")
              ) {
                Text(
                  text = word,
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = chipTextColor,
                  modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                )
              }
            }
          }

          // Optional manual text entry alternative
          Spacer(modifier = Modifier.height(14.dp))
          var manualInput by remember { mutableStateOf("") }
          OutlinedTextField(
            value = manualInput,
            onValueChange = {
              manualInput = it
              if (it.isNotEmpty() && !isAnswerChecked) {
                onAnswerSelected(it)
              }
            },
            enabled = !isAnswerChecked,
            label = { Text("Or type answer manually") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = MaterialTheme.colorScheme.surface,
              unfocusedContainerColor = MaterialTheme.colorScheme.surface
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Feedback Banner after checking answer
      AnimatedVisibility(visible = isAnswerChecked) {
        val bannerBg = if (wasCorrect) AccentGreen.copy(alpha = 0.15f) else AccentCoral.copy(alpha = 0.15f)
        val bannerBorder = if (wasCorrect) AccentGreen else AccentCoral

        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = bannerBg),
          modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, bannerBorder, RoundedCornerShape(16.dp))
            .padding(vertical = 6.dp)
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = if (wasCorrect) Icons.Filled.CheckCircle else Icons.AutoMirrored.Filled.HelpOutline,
                contentDescription = null,
                tint = if (wasCorrect) AccentGreen else AccentCoral,
                modifier = Modifier.size(24.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = if (wasCorrect) "¡Excelente! Correct answer!" else "Not quite.",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = if (wasCorrect) AccentGreen else AccentCoral
              )
            }

            if (!wasCorrect) {
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Correct answer: \"${question.correctAnswer}\"",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = question.explanation,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurface
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Action Area (Check or Next + Previous navigation)
    Column(modifier = Modifier.fillMaxWidth()) {
      if (!isAnswerChecked) {
        Button(
          onClick = onCheckAnswer,
          enabled = !userAnswer.isNullOrBlank(),
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
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
          onClick = onNextQuestion,
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("next_question_button"),
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = if (wasCorrect) AccentGreen else MaterialTheme.colorScheme.primary
          )
        ) {
          Text(
            text = if (currentIndex < questions.size - 1) "Next Question" else "See Lesson Results",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
        }
      }

      // Previous button when applicable
      if (currentIndex > 0) {
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(
          onClick = onPreviousQuestion,
          modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
          Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(text = "Previous Question")
        }
      }
    }
  }
}

/**
 * Results Section showing score percentage, correct answers, mistakes review, and retry flow.
 */
@Composable
private fun ResultsSection(
  lesson: CourseLesson,
  scorePercent: Int,
  correctCount: Int,
  totalQuestions: Int,
  userAnswers: Map<Int, String>,
  isAnswerCorrect: Map<Int, Boolean>,
  onRetry: () -> Unit,
  onFinish: () -> Unit
) {
  val scrollState = rememberScrollState()
  val missedQuestions = lesson.questions.filterIndexed { index, _ ->
    isAnswerCorrect[index] != true
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(scrollState)
      .padding(horizontal = 20.dp, vertical = 16.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      // Medallion icon
      Surface(
        shape = CircleShape,
        color = if (scorePercent >= 80) AmberSecondary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.size(80.dp)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = Icons.Filled.EmojiEvents,
            contentDescription = "Lesson Trophy",
            tint = if (scorePercent >= 80) AmberSecondary else MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(44.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      Text(
        text = if (scorePercent == 100) {
          "🎉 Perfect Score!"
        } else if (scorePercent >= 70) {
          "👏 Well Done!"
        } else {
          "💪 Keep Practicing!"
        },
        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
        color = MaterialTheme.colorScheme.onBackground
      )

      Text(
        text = "You finished \"${lesson.title}\"",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Spacer(modifier = Modifier.height(18.dp))

      // Score Stat Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp),
          horizontalArrangement = Arrangement.SpaceEvenly,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = "$scorePercent%",
              style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
              color = if (scorePercent >= 70) AccentGreen else AmberSecondary
            )
            Text(
              text = "Accuracy",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          Box(
            modifier = Modifier
              .width(1.dp)
              .height(40.dp)
              .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
          )

          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = "$correctCount / $totalQuestions",
              style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Correct Answers",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          Box(
            modifier = Modifier
              .width(1.dp)
              .height(40.dp)
              .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
          )

          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = "+${lesson.xpReward}",
              style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
              color = AmberSecondary
            )
            Text(
              text = "XP Earned",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      // Review Mistakes Section
      if (missedQuestions.isNotEmpty()) {
        Spacer(modifier = Modifier.height(20.dp))
        Column(modifier = Modifier.fillMaxWidth()) {
          Text(
            text = "Review Mistakes (${missedQuestions.size})",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
          )
          Spacer(modifier = Modifier.height(8.dp))

          missedQuestions.forEach { q ->
            val indexInAll = lesson.questions.indexOf(q)
            val givenAnswer = userAnswers[indexInAll] ?: "(None)"

            Card(
              modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
              ),
              border = CardDefaults.outlinedCardBorder()
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Text(
                  text = q.prompt,
                  style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                  Text(text = "❌ Your answer: ", style = MaterialTheme.typography.bodySmall, color = AccentCoral)
                  Text(text = givenAnswer, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                }
                Row {
                  Text(text = "✅ Correct answer: ", style = MaterialTheme.typography.bodySmall, color = AccentGreen)
                  Text(text = q.correctAnswer, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "💡 ${q.explanation}",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Action buttons: Finish & Save OR Retry
    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Button(
        onClick = onFinish,
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("finish_lesson_button"),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
      ) {
        Icon(imageVector = Icons.Filled.Check, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Finish & Save Progress",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
      }

      OutlinedButton(
        onClick = onRetry,
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .testTag("retry_lesson_button"),
        shape = RoundedCornerShape(14.dp)
      ) {
        Icon(imageVector = Icons.Filled.Replay, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "Retry Lesson")
      }
    }
  }
}
