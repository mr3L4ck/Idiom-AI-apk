package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.Language
import com.example.data.PronunciationEvaluation
import com.example.data.PronunciationEvaluator
import com.example.data.SpeakingPhrase
import com.example.data.VoiceLearningCatalog
import com.example.ui.theme.AccentCoral
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AmberSecondary
import com.example.util.SpeechState
import com.example.util.rememberSpeechRecognitionHelper
import com.example.util.rememberTextToSpeechHelper

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SpeakingPracticeScreen(
  learningLanguage: Language,
  nativeLanguage: Language,
  onFinish: (phrasesPracticed: Int, xpEarned: Int) -> Unit,
  onExit: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val phrases = remember(learningLanguage.id) {
    VoiceLearningCatalog.getSpeakingPhrases(learningLanguage.id)
  }

  var currentIndex by remember { mutableIntStateOf(0) }
  val currentPhrase = phrases.getOrElse(currentIndex) {
    SpeakingPhrase("fallback", "Hola", "OH-lah", "Hello", "Greeting")
  }

  // TTS and Speech Recognition
  val ttsHelper = rememberTextToSpeechHelper()
  val speechHelper = rememberSpeechRecognitionHelper()

  // Track playback rate: 1.0f (normal) or 0.75f (slower)
  var isSlowSpeed by remember { mutableStateOf(false) }

  // Pronunciation comparison result
  var evaluation by remember { mutableStateOf<PronunciationEvaluation?>(null) }
  var practicedCount by remember { mutableIntStateOf(0) }

  // Microphone Permission State
  var hasMicPermission by remember {
    mutableStateOf(
      ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.RECORD_AUDIO
      ) == PackageManager.PERMISSION_GRANTED
    )
  }
  var showPermissionRationaleDialog by remember { mutableStateOf(false) }

  val permissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    hasMicPermission = isGranted
    if (isGranted) {
      speechHelper.startListening(learningLanguage.id)
    }
  }

  // Evaluate when speech recognition succeeds
  val speechState = speechHelper.state.value
  val recognizedText = speechHelper.recognizedText.value

  LaunchedEffect(speechState, recognizedText) {
    if (speechState == SpeechState.SUCCESS && recognizedText.isNotBlank()) {
      evaluation = PronunciationEvaluator.evaluate(
        expected = currentPhrase.original,
        recognized = recognizedText
      )
      practicedCount++
    }
  }

  // Ensure speech recognition is cleanly stopped on back
  BackHandler {
    speechHelper.cancel()
    ttsHelper.stop()
    onExit()
  }

  // Pulsing animation for listening state
  val infiniteTransition = rememberInfiniteTransition(label = "micPulse")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = 1.25f,
    animationSpec = infiniteRepeatable(
      animation = tween(800, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulseScale"
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
      // Top Navigation App Bar
      TopAppBar(
        title = {
          Column {
            Text(
              text = "Speaking Practice",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "${learningLanguage.flagEmoji} ${learningLanguage.name} • Phrase ${currentIndex + 1} of ${phrases.size}",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        },
        navigationIcon = {
          IconButton(
            onClick = {
              speechHelper.cancel()
              ttsHelper.stop()
              onExit()
            },
            modifier = Modifier.testTag("speaking_back_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Exit speaking practice"
            )
          }
        },
        actions = {
          // Finish Session Chip
          TextButton(
            onClick = {
              speechHelper.cancel()
              ttsHelper.stop()
              val earnedXp = (practicedCount * 10).coerceAtLeast(15)
              onFinish(practicedCount, earnedXp)
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

      // Step Progress Indicator
      LinearProgressIndicator(
        progress = { (currentIndex + 1).toFloat() / phrases.size.toFloat() },
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

        // Target Phrase Card
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("target_phrase_card"),
          shape = RoundedCornerShape(24.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
          ),
          border = CardDefaults.outlinedCardBorder(),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            // Level badge
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
            ) {
              Text(
                text = "SPEAK ALOUD • ${currentPhrase.level.uppercase()}",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
              )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Original phrase in target language
            Text(
              text = currentPhrase.original,
              style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
              ),
              color = MaterialTheme.colorScheme.onSurface,
              textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Phonetic Pronunciation Guide
            Text(
              text = currentPhrase.pronunciation,
              style = MaterialTheme.typography.bodyLarge,
              color = MaterialTheme.colorScheme.primary,
              textAlign = TextAlign.Center,
              fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Translation in Learner's Language
            Text(
              text = currentPhrase.translation,
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Cultural / Phonetic Tip
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Filled.Info,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = currentPhrase.contextTip,
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Audio Listening Section with Speed Toggle
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.Center,
              verticalAlignment = Alignment.CenterVertically
            ) {
              // Listen Button (Normal / Current Speed)
              Button(
                onClick = {
                  val rate = if (isSlowSpeed) 0.75f else 1.0f
                  ttsHelper.speak(currentPhrase.original, learningLanguage.id, rate)
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                  containerColor = MaterialTheme.colorScheme.primaryContainer,
                  contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                modifier = Modifier.testTag("listen_phrase_button")
              ) {
                Icon(
                  imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                  contentDescription = "Listen to pronunciation",
                  modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = if (ttsHelper.isSpeaking.value) "Playing..." else "Listen",
                  fontWeight = FontWeight.Bold
                )
              }

              Spacer(modifier = Modifier.width(12.dp))

              // Speed Toggle: 1.0x vs 0.75x
              OutlinedButton(
                onClick = {
                  isSlowSpeed = !isSlowSpeed
                  val newRate = if (isSlowSpeed) 0.75f else 1.0f
                  ttsHelper.setSpeechRate(newRate)
                  ttsHelper.speak(currentPhrase.original, learningLanguage.id, newRate)
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("speech_rate_toggle")
              ) {
                Icon(
                  imageVector = Icons.Filled.Speed,
                  contentDescription = "Adjust playback speed",
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = if (isSlowSpeed) "0.75x (Slow)" else "1.0x (Normal)",
                  style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
              }
            }

            // TTS Voice Status if specific accent is missing
            if (ttsHelper.voiceStatusMessage.value != null) {
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = ttsHelper.voiceStatusMessage.value ?: "",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline,
                textAlign = TextAlign.Center
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Microphone & Pronunciation Evaluation Area
        if (!hasMicPermission) {
          // Pre-Permission Rationale Card
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
              containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            border = CardDefaults.outlinedCardBorder()
          ) {
            Column(
              modifier = Modifier.padding(20.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(54.dp)
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Icon(
                    imageVector = Icons.Filled.Mic,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                  )
                }
              }

              Spacer(modifier = Modifier.height(12.dp))

              Text(
                text = "Microphone Permission Required",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )

              Spacer(modifier = Modifier.height(6.dp))

              Text(
                text = "To check your speaking accuracy, ${com.example.data.BrandConfig.APP_NAME} needs temporary access to your microphone. Your voice is transcribed in real-time on your device and is never recorded or sent to external servers.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
              )

              Spacer(modifier = Modifier.height(16.dp))

              Button(
                onClick = {
                  permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.testTag("grant_mic_permission_button")
              ) {
                Icon(imageVector = Icons.Filled.Mic, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Enable Microphone")
              }
            }
          }
        } else if (!speechHelper.isAvailable) {
          // Speech Recognizer Unavailable on Device
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
              containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
            ),
            border = CardDefaults.outlinedCardBorder()
          ) {
            Column(
              modifier = Modifier.padding(18.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Icon(
                imageVector = Icons.Filled.MicOff,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(32.dp)
              )
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "Speech Recognition Service Unavailable",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.error
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "No compatible Android speech recognition service was detected on this device. You can still practice speaking aloud by listening to the phrase audio above.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
              )
            }
          }
        } else {
          // Speech Recognition Active Controls
          Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            // Dynamic Microphone Button
            Box(
              contentAlignment = Alignment.Center,
              modifier = Modifier.size(100.dp)
            ) {
              // Pulsing outer ripple when listening
              if (speechState == SpeechState.LISTENING) {
                Surface(
                  shape = CircleShape,
                  color = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                  modifier = Modifier
                    .fillMaxSize()
                    .scale(pulseScale)
                ) {}
              }

              val buttonColor = when (speechState) {
                SpeechState.LISTENING -> AccentCoral
                SpeechState.PROCESSING, SpeechState.PREPARING -> MaterialTheme.colorScheme.primary
                SpeechState.SUCCESS -> AccentGreen
                SpeechState.ERROR -> MaterialTheme.colorScheme.error
                SpeechState.IDLE -> MaterialTheme.colorScheme.primary
              }

              Surface(
                shape = CircleShape,
                color = buttonColor,
                modifier = Modifier
                  .size(76.dp)
                  .clip(CircleShape)
                  .clickable {
                    when (speechState) {
                      SpeechState.LISTENING -> speechHelper.stopListening()
                      SpeechState.PROCESSING, SpeechState.PREPARING -> speechHelper.cancel()
                      else -> {
                        evaluation = null
                        speechHelper.startListening(learningLanguage.id)
                      }
                    }
                  }
                  .testTag("microphone_action_button"),
                shadowElevation = 6.dp
              ) {
                Box(contentAlignment = Alignment.Center) {
                  when (speechState) {
                    SpeechState.PREPARING -> {
                      CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        color = Color.White,
                        strokeWidth = 3.dp
                      )
                    }
                    SpeechState.LISTENING -> {
                      Icon(
                        imageVector = Icons.Filled.Stop,
                        contentDescription = "Stop listening",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                      )
                    }
                    SpeechState.PROCESSING -> {
                      CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        color = Color.White,
                        strokeWidth = 3.dp
                      )
                    }
                    SpeechState.SUCCESS -> {
                      Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Recording successful",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                      )
                    }
                    SpeechState.ERROR -> {
                      Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "Retry speech",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                      )
                    }
                    SpeechState.IDLE -> {
                      Icon(
                        imageVector = Icons.Filled.Mic,
                        contentDescription = "Start speaking",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                      )
                    }
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Status Label
            val statusText = when (speechState) {
              SpeechState.IDLE -> "Tap the microphone and speak aloud"
              SpeechState.PREPARING -> "Connecting to speech engine..."
              SpeechState.LISTENING -> "Listening... Speak in ${learningLanguage.name} now!"
              SpeechState.PROCESSING -> "Analyzing speech transcript..."
              SpeechState.SUCCESS -> "Transcript recorded! Tap mic to try again."
              SpeechState.ERROR -> "Speech attempt failed. Tap to try again."
            }

            Text(
              text = statusText,
              style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
              color = when (speechState) {
                SpeechState.LISTENING -> AccentCoral
                SpeechState.SUCCESS -> AccentGreen
                SpeechState.ERROR -> MaterialTheme.colorScheme.error
                else -> MaterialTheme.colorScheme.onSurfaceVariant
              },
              textAlign = TextAlign.Center
            )

            // Live Partial Transcription
            if (speechHelper.partialText.value.isNotBlank() && speechState == SpeechState.LISTENING) {
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "\"${speechHelper.partialText.value}\"",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
              )
            }

            // Error Message Display
            if (speechState == SpeechState.ERROR && speechHelper.errorMessage.value != null) {
              Spacer(modifier = Modifier.height(10.dp))
              Card(
                colors = CardDefaults.cardColors(
                  containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
              ) {
                Text(
                  text = speechHelper.errorMessage.value ?: "Error occurred",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onErrorContainer,
                  modifier = Modifier.padding(12.dp),
                  textAlign = TextAlign.Center
                )
              }
            }

            // Feedback & Side-by-Side Pronunciation Comparison Card
            if (evaluation != null) {
              val eval = evaluation!!
              Spacer(modifier = Modifier.height(16.dp))

              Card(
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("pronunciation_feedback_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                  containerColor = MaterialTheme.colorScheme.surface
                ),
                border = CardDefaults.outlinedCardBorder()
              ) {
                Column(modifier = Modifier.padding(18.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = "Speech Transcript Comparison",
                      style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                      color = MaterialTheme.colorScheme.onSurface
                    )

                    Surface(
                      shape = RoundedCornerShape(8.dp),
                      color = if (eval.isExactMatch) {
                        AccentGreen.copy(alpha = 0.15f)
                      } else if (eval.isApproximateMatch) {
                        AmberSecondary.copy(alpha = 0.15f)
                      } else {
                        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)
                      }
                    ) {
                      Text(
                        text = if (eval.isExactMatch) "Exact Match" else if (eval.isApproximateMatch) "Approximate Match" else "Partial Match",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (eval.isExactMatch) AccentGreen else if (eval.isApproximateMatch) AmberSecondary else MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                      )
                    }
                  }

                  Spacer(modifier = Modifier.height(12.dp))

                  // Expected phrase
                  Text(
                    text = "Expected Phrase:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                  Text(
                    text = eval.expectedText,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                  )

                  Spacer(modifier = Modifier.height(10.dp))

                  // Recognized phrase
                  Text(
                    text = "Recognized from Voice:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                  Text(
                    text = if (eval.recognizedText.isNotBlank()) eval.recognizedText else "(No speech recognized)",
                    style = MaterialTheme.typography.bodyMedium.copy(
                      fontWeight = FontWeight.SemiBold,
                      color = if (eval.isExactMatch) AccentGreen else MaterialTheme.colorScheme.primary
                    )
                  )

                  Spacer(modifier = Modifier.height(12.dp))

                  // Word Match Chips
                  Text(
                    text = "Word-by-word match (${eval.matchedWordCount} of ${eval.totalWordCount} words):",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )

                  Spacer(modifier = Modifier.height(6.dp))

                  FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                  ) {
                    eval.wordComparisons.forEach { item ->
                      Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (item.matched) {
                          AccentGreen.copy(alpha = 0.2f)
                        } else {
                          MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        },
                        border = if (item.matched) {
                          null
                        } else {
                          CardDefaults.outlinedCardBorder()
                        }
                      ) {
                        Row(
                          modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                          verticalAlignment = Alignment.CenterVertically
                        ) {
                          if (item.matched) {
                            Icon(
                              imageVector = Icons.Filled.CheckCircle,
                              contentDescription = null,
                              tint = AccentGreen,
                              modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                          }
                          Text(
                            text = item.word,
                            style = MaterialTheme.typography.labelMedium.copy(
                              fontWeight = if (item.matched) FontWeight.Bold else FontWeight.Normal,
                              color = if (item.matched) AccentGreen else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                          )
                        }
                      }
                    }
                  }

                  Spacer(modifier = Modifier.height(12.dp))

                  // Summary feedback
                  Text(
                    text = eval.feedbackSummary,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                  )

                  Spacer(modifier = Modifier.height(10.dp))

                  // Scientific disclaimer (REQUIRED BY GUIDELINES)
                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Text(
                      text = "ℹ️ ${eval.disclaimerText}",
                      style = MaterialTheme.typography.labelSmall,
                      color = MaterialTheme.colorScheme.outline,
                      modifier = Modifier.padding(8.dp)
                    )
                  }
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Phrase Navigation Controls (Previous / Next / Try Again)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Previous button
          OutlinedButton(
            onClick = {
              if (currentIndex > 0) {
                speechHelper.reset()
                evaluation = null
                currentIndex--
              }
            },
            enabled = currentIndex > 0,
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Previous phrase",
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("Previous")
          }

          // Next phrase / Finish button
          Button(
            onClick = {
              if (currentIndex < phrases.size - 1) {
                speechHelper.reset()
                evaluation = null
                currentIndex++
              } else {
                speechHelper.cancel()
                ttsHelper.stop()
                val earnedXp = (practicedCount * 10).coerceAtLeast(25)
                onFinish(practicedCount, earnedXp)
              }
            },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier.testTag("next_phrase_button")
          ) {
            Text(
              text = if (currentIndex < phrases.size - 1) "Next Phrase" else "Complete Practice",
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowForward,
              contentDescription = null,
              modifier = Modifier.size(18.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(20.dp))
      }
    }
  }
}
