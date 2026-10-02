package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Tune
import com.example.ui.components.TutorVoiceSettingsSheet
import com.example.util.rememberTutorVoiceManager
import com.example.data.BrandConfig
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.ChatMessage
import com.example.data.Language
import com.example.data.MessageSender
import com.example.data.TutorLevel
import com.example.ui.AiTutorViewModel
import com.example.ui.theme.AccentCoral
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AmberSecondary
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.util.SpeechState
import com.example.util.rememberSpeechRecognitionHelper
import com.example.util.rememberTextToSpeechHelper

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AITutorScreen(
  learningLanguage: Language,
  nativeLanguage: Language,
  modifier: Modifier = Modifier,
  tutorViewModel: AiTutorViewModel = viewModel()
) {
  val messages by tutorViewModel.messages.collectAsStateWithLifecycle()
  val isLoading by tutorViewModel.isLoading.collectAsStateWithLifecycle()
  val tutorLevel by tutorViewModel.tutorLevel.collectAsStateWithLifecycle()
  val explainInNative by tutorViewModel.explainInNative.collectAsStateWithLifecycle()
  val isApiKeyAvailable by tutorViewModel.isApiKeyAvailable.collectAsStateWithLifecycle()
  val userPreferences by tutorViewModel.userPreferences.collectAsStateWithLifecycle()

  var inputMessage by remember { mutableStateOf("") }
  var showClearConfirmDialog by remember { mutableStateOf(false) }
  var showLevelDialog by remember { mutableStateOf(false) }
  var showApiKeyInfoDialog by remember { mutableStateOf(false) }
  var showVoiceSettingsSheet by remember { mutableStateOf(false) }

  val listState = rememberLazyListState()
  val ttsHelper = rememberTextToSpeechHelper()
  val voiceManager = rememberTutorVoiceManager(ttsHelper)
  val context = LocalContext.current
  val speechHelper = rememberSpeechRecognitionHelper()

  var hasMicPermission by remember {
    mutableStateOf(
      ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.RECORD_AUDIO
      ) == PackageManager.PERMISSION_GRANTED
    )
  }
  var showMicRationaleDialog by remember { mutableStateOf(false) }

  val micPermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    hasMicPermission = isGranted
    if (isGranted) {
      speechHelper.startListening(learningLanguage.id)
    }
  }

  // Update input text when voice recognition produces speech
  val speechResult = speechHelper.recognizedText.value
  LaunchedEffect(speechResult) {
    if (speechResult.isNotBlank()) {
      inputMessage = if (inputMessage.isBlank()) speechResult else "$inputMessage $speechResult"
    }
  }

  // Initialize tutor for the currently selected languages
  LaunchedEffect(learningLanguage.id) {
    tutorViewModel.initForLanguage(learningLanguage, nativeLanguage)
  }

  // Auto-scroll to the bottom when messages list updates
  LaunchedEffect(messages.size, isLoading) {
    if (messages.isNotEmpty()) {
      listState.animateScrollToItem(messages.size - 1)
    }
  }

  // Auto-play spoken tutor response if auto-play is enabled in voice settings
  LaunchedEffect(messages.size) {
    val lastMessage = messages.lastOrNull()
    if (lastMessage != null &&
      lastMessage.sender == MessageSender.TUTOR &&
      !lastMessage.isError &&
      userPreferences.autoPlayTutorAudio &&
      !voiceManager.isPlaying.value
    ) {
      voiceManager.playTutorResponse(
        messageId = lastMessage.id,
        text = lastMessage.text,
        targetLanguage = learningLanguage,
        engineMode = userPreferences.tutorVoiceEngine,
        geminiVoice = userPreferences.tutorGeminiVoice,
        speechRate = userPreferences.tutorSpeechRate
      )
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
        .imePadding()
    ) {
      // Header Bar
      Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        border = CardDefaults.outlinedCardBorder()
      ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.weight(1f)
            ) {
              Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(42.dp)
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Icon(
                    imageVector = Icons.Filled.SmartToy,
                    contentDescription = BrandConfig.AI_TUTOR_NAME,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(24.dp)
                  )
                }
              }

              Spacer(modifier = Modifier.width(10.dp))

              Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = BrandConfig.AI_TUTOR_NAME,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  if (isApiKeyAvailable) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                      shape = RoundedCornerShape(6.dp),
                      color = AccentGreen.copy(alpha = 0.15f)
                    ) {
                      Text(
                        text = "● ${BrandConfig.AI_STATUS_READY}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = AccentGreen,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                      )
                    }
                  }
                }
                Text(
                  text = "Practicing ${learningLanguage.flagEmoji} ${learningLanguage.name} • ${tutorLevel.label} (${tutorLevel.cefr})",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }

            // Action icons
            Row(verticalAlignment = Alignment.CenterVertically) {
              IconButton(
                onClick = { showVoiceSettingsSheet = true },
                modifier = Modifier.testTag("tutor_voice_settings_button")
              ) {
                Icon(
                  imageVector = Icons.Filled.RecordVoiceOver,
                  contentDescription = "Tutor voice settings",
                  tint = if (userPreferences.tutorVoiceEngine == "gemini_neural") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
              }

              IconButton(
                onClick = { showLevelDialog = true },
                modifier = Modifier.testTag("tutor_settings_button")
              ) {
                Icon(
                  imageVector = Icons.Filled.Tune,
                  contentDescription = "Tutor settings",
                  tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }

              IconButton(
                onClick = { showClearConfirmDialog = true },
                modifier = Modifier.testTag("new_chat_button")
              ) {
                Icon(
                  imageVector = Icons.Filled.AddComment,
                  contentDescription = "Start new conversation",
                  tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Filter & Settings Chips Row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Level chip
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = MaterialTheme.colorScheme.surfaceVariant,
              modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .clickable { showLevelDialog = true }
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "Level: ${tutorLevel.label}",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }

            // Voice settings chip
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = if (userPreferences.tutorVoiceEngine == "gemini_neural") {
                MaterialTheme.colorScheme.primaryContainer
              } else {
                MaterialTheme.colorScheme.surfaceVariant
              },
              modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .clickable { showVoiceSettingsSheet = true }
                .testTag("voice_settings_chip")
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Filled.RecordVoiceOver,
                  contentDescription = null,
                  modifier = Modifier.size(13.dp),
                  tint = if (userPreferences.tutorVoiceEngine == "gemini_neural") MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = if (userPreferences.tutorVoiceEngine == "gemini_neural") {
                    "${BrandConfig.VOICE_FEATURE_NAME}: ${userPreferences.tutorGeminiVoice} (${userPreferences.tutorSpeechRate}x)"
                  } else {
                    "Voice: System TTS"
                  },
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = if (userPreferences.tutorVoiceEngine == "gemini_neural") MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }

            // Native explanation toggle chip
            FilterChip(
              selected = explainInNative,
              onClick = { tutorViewModel.setExplainInNative(!explainInNative) },
              label = {
                Text(
                  text = if (explainInNative) "Explaining in ${nativeLanguage.name}" else "Only in ${learningLanguage.name}",
                  style = MaterialTheme.typography.labelSmall
                )
              },
              leadingIcon = {
                if (explainInNative) {
                  Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)
                  )
                }
              },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
              )
            )
          }
        }
      }

      // Voice Status Notice Banner (if fallback or quota notice is active)
      voiceManager.playbackNotice.value?.let { notice ->
        Surface(
          color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Filled.Info,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.onSecondaryContainer,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = notice,
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSecondaryContainer,
              modifier = Modifier.weight(1f)
            )
          }
        }
      }

      // API Key Status Banner (if key is missing or default placeholder)
      if (!isApiKeyAvailable) {
        Surface(
          color = AmberSecondary.copy(alpha = 0.15f),
          modifier = Modifier
            .fillMaxWidth()
            .clickable { showApiKeyInfoDialog = true }
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Filled.Key,
              contentDescription = null,
              tint = AmberSecondary,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Live AI Tutor configuration needed. Tap for setup details.",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
              color = MaterialTheme.colorScheme.onSurface,
              modifier = Modifier.weight(1f)
            )
            Icon(
              imageVector = Icons.Filled.Info,
              contentDescription = "Info",
              tint = AmberSecondary,
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }

      // Chat Messages List
      LazyColumn(
        state = listState,
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        // Suggested Conversation Starters (when chat has only the welcome message)
        if (messages.size <= 1) {
          item(key = "conversation_starters") {
            SuggestedPromptsCard(
              targetLanguage = learningLanguage,
              nativeLanguage = nativeLanguage,
              onSelectPrompt = { prompt ->
                tutorViewModel.sendMessage(prompt, learningLanguage, nativeLanguage)
              }
            )
          }
        }

        items(messages, key = { it.id }) { message ->
          val isCurrentPlaying = voiceManager.isPlaying.value && voiceManager.playingMessageId.value == message.id
          val isCurrentBuffering = voiceManager.isBuffering.value && voiceManager.playingMessageId.value == message.id
          ChatMessageBubble(
            message = message,
            targetLanguageCode = learningLanguage.id,
            isPlaying = isCurrentPlaying,
            isBuffering = isCurrentBuffering,
            engineBadge = if (isCurrentPlaying) voiceManager.activeEngineBadge.value else null,
            onToggleAudio = {
              voiceManager.playTutorResponse(
                messageId = message.id,
                text = message.text,
                targetLanguage = learningLanguage,
                engineMode = userPreferences.tutorVoiceEngine,
                geminiVoice = userPreferences.tutorGeminiVoice,
                speechRate = userPreferences.tutorSpeechRate
              )
            },
            onRetry = {
              tutorViewModel.retryLastUserMessage(learningLanguage, nativeLanguage)
            }
          )
        }

        // Typing / Loading indicator
        if (isLoading) {
          item(key = "loading_indicator") {
            TutorTypingBubble(targetLanguageName = learningLanguage.name)
          }
        }
      }

      // Voice Dictation Active Banner
      if (speechHelper.state.value == SpeechState.LISTENING) {
        Surface(
          modifier = Modifier.fillMaxWidth(),
          color = AccentCoral.copy(alpha = 0.15f),
          border = CardDefaults.outlinedCardBorder()
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Filled.Mic,
              contentDescription = null,
              tint = AccentCoral,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = if (speechHelper.partialText.value.isNotBlank())
                "\"${speechHelper.partialText.value}\""
              else
                "Listening in ${learningLanguage.name}... (Speak clearly)",
              style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
              color = MaterialTheme.colorScheme.onSurface,
              modifier = Modifier.weight(1f)
            )
            IconButton(
              onClick = { speechHelper.stopListening() },
              modifier = Modifier.size(28.dp)
            ) {
              Icon(imageVector = Icons.Filled.Stop, contentDescription = "Done speaking", tint = AccentCoral)
            }
          }
        }
      }

      // Bottom Message Composer
      Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp,
        border = CardDefaults.outlinedCardBorder()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          OutlinedTextField(
            value = inputMessage,
            onValueChange = { inputMessage = it },
            placeholder = {
              Text(
                text = "Message in ${learningLanguage.name} or ask for tips...",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1
              )
            },
            modifier = Modifier
              .weight(1f)
              .testTag("tutor_message_input"),
            shape = RoundedCornerShape(24.dp),
            maxLines = 4,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(
              onSend = {
                if (inputMessage.isNotBlank() && !isLoading) {
                  val textToSend = inputMessage
                  inputMessage = ""
                  tutorViewModel.sendMessage(textToSend, learningLanguage, nativeLanguage)
                }
              }
            ),
            trailingIcon = {
              if (inputMessage.isNotEmpty()) {
                IconButton(onClick = { inputMessage = "" }) {
                  Icon(imageVector = Icons.Filled.Close, contentDescription = "Clear text")
                }
              }
            },
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
              unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
              focusedBorderColor = MaterialTheme.colorScheme.primary,
              unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
            )
          )

          Spacer(modifier = Modifier.width(6.dp))

          // Voice Dictation Button
          Surface(
            shape = CircleShape,
            color = if (speechHelper.state.value == SpeechState.LISTENING) {
              AccentCoral
            } else {
              MaterialTheme.colorScheme.surfaceVariant
            },
            modifier = Modifier
              .size(48.dp)
              .clip(CircleShape)
              .clickable(enabled = !isLoading) {
                if (speechHelper.state.value == SpeechState.LISTENING) {
                  speechHelper.stopListening()
                } else if (!hasMicPermission) {
                  showMicRationaleDialog = true
                } else {
                  speechHelper.startListening(learningLanguage.id)
                }
              }
              .testTag("tutor_voice_input_button")
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = if (speechHelper.state.value == SpeechState.LISTENING) Icons.Filled.Stop else Icons.Filled.Mic,
                contentDescription = if (speechHelper.state.value == SpeechState.LISTENING) "Stop voice input" else "Dictate with voice",
                tint = if (speechHelper.state.value == SpeechState.LISTENING) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(22.dp)
              )
            }
          }

          Spacer(modifier = Modifier.width(6.dp))

          // Send Button
          Surface(
            shape = CircleShape,
            color = if (inputMessage.isNotBlank() && !isLoading) {
              MaterialTheme.colorScheme.primary
            } else {
              MaterialTheme.colorScheme.surfaceVariant
            },
            modifier = Modifier
              .size(48.dp)
              .clip(CircleShape)
              .clickable(enabled = inputMessage.isNotBlank() && !isLoading) {
                val textToSend = inputMessage
                inputMessage = ""
                tutorViewModel.sendMessage(textToSend, learningLanguage, nativeLanguage)
              }
              .testTag("tutor_send_button")
          ) {
            Box(contentAlignment = Alignment.Center) {
              if (isLoading) {
                CircularProgressIndicator(
                  modifier = Modifier.size(20.dp),
                  color = MaterialTheme.colorScheme.primary,
                  strokeWidth = 2.dp
                )
              } else {
                Icon(
                  imageVector = Icons.AutoMirrored.Filled.Send,
                  contentDescription = "Send message",
                  tint = if (inputMessage.isNotBlank()) Color.White else MaterialTheme.colorScheme.outline,
                  modifier = Modifier.size(20.dp)
                )
              }
            }
          }
        }
      }
    }
  }

  // Clear Conversation Confirmation Dialog
  if (showClearConfirmDialog) {
    AlertDialog(
      onDismissRequest = { showClearConfirmDialog = false },
      title = { Text("Start New Conversation?") },
      text = {
        Text("This will clear your current conversation history with your ${learningLanguage.name} tutor and start a fresh practice session.")
      },
      confirmButton = {
        TextButton(
          onClick = {
            showClearConfirmDialog = false
            tutorViewModel.clearConversation(learningLanguage, nativeLanguage)
          }
        ) {
          Text("Start New Chat", color = MaterialTheme.colorScheme.primary)
        }
      },
      dismissButton = {
        TextButton(onClick = { showClearConfirmDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }

  // Tutor Proficiency Level Dialog
  if (showLevelDialog) {
    AlertDialog(
      onDismissRequest = { showLevelDialog = false },
      title = { Text("Select Tutor Level") },
      text = {
        Column {
          Text(
            text = "Your AI tutor will adjust vocabulary, grammar complexity, and speed according to your level:",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(12.dp))

          TutorLevel.entries.forEach { level ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable {
                  tutorViewModel.setTutorLevel(level)
                  showLevelDialog = false
                }
                .padding(vertical = 8.dp, horizontal = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              RadioButton(
                selected = tutorLevel == level,
                onClick = {
                  tutorViewModel.setTutorLevel(level)
                  showLevelDialog = false
                }
              )
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(
                  text = "${level.label} (${level.cefr})",
                  style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = if (tutorLevel == level) FontWeight.Bold else FontWeight.Normal
                  )
                )
                Text(
                  text = when (level) {
                    TutorLevel.BEGINNER -> "Simple words, everyday greetings, polite courtesies"
                    TutorLevel.INTERMEDIATE -> "Connected sentences, travel, dining, past & future"
                    TutorLevel.ADVANCED -> "Complex grammar, cultural idioms, fluent discussion"
                  },
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }
      },
      confirmButton = {
        TextButton(onClick = { showLevelDialog = false }) {
          Text("Done")
        }
      }
    )
  }

  // AI Setup Guidance Dialog
  if (showApiKeyInfoDialog) {
    AlertDialog(
      onDismissRequest = { showApiKeyInfoDialog = false },
      title = { Text("${BrandConfig.AI_TUTOR_NAME} Setup") },
      text = {
        Column {
          Text(
            text = "${BrandConfig.APP_NAME} connects to high-performance AI and neural voice models for interactive conversational practice.",
            style = MaterialTheme.typography.bodyMedium
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "To enable live responses:\n1. Open the Secrets panel in AI Studio.\n2. Add your GEMINI_API_KEY.\n3. The key is securely injected at build time without hardcoding secrets in client code.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(10.dp))
          Text(
            text = "Note: All basic lessons, vocabulary courses, and unit quizzes remain completely free and functional offline without an API key.",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary
          )
        }
      },
      confirmButton = {
        Button(onClick = { showApiKeyInfoDialog = false }) {
          Text("Understood")
        }
      }
    )
  }

  // Microphone Permission Rationale Dialog
  if (showMicRationaleDialog) {
    AlertDialog(
      onDismissRequest = { showMicRationaleDialog = false },
      title = { Text("Microphone Access") },
      text = {
        Text("${BrandConfig.APP_NAME} uses your device's microphone for real-time speech dictation so you can talk with ${BrandConfig.AI_TUTOR_NAME} in ${learningLanguage.name}. Your speech is transcribed locally via device speech services and is not stored or shared.")
      },
      confirmButton = {
        Button(
          onClick = {
            showMicRationaleDialog = false
            micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
          }
        ) {
          Text("Enable Microphone")
        }
      },
      dismissButton = {
        TextButton(onClick = { showMicRationaleDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }

  // Voice Settings Sheet
  if (showVoiceSettingsSheet) {
    TutorVoiceSettingsSheet(
      currentEngine = userPreferences.tutorVoiceEngine,
      currentVoice = userPreferences.tutorGeminiVoice,
      currentRate = userPreferences.tutorSpeechRate,
      currentAutoPlay = userPreferences.autoPlayTutorAudio,
      targetLanguage = learningLanguage,
      isApiKeyConfigured = voiceManager.isApiKeyConfigured(),
      isPlayingPreview = voiceManager.isPlaying.value && voiceManager.playingMessageId.value == "preview_voice",
      onPreview = { engine, voice, rate, sampleText ->
        voiceManager.previewVoice(
          sampleText = sampleText,
          targetLanguage = learningLanguage,
          engineMode = engine,
          geminiVoice = voice,
          speechRate = rate
        )
      },
      onStopPreview = {
        voiceManager.stop()
      },
      onSave = { engine, voice, rate, autoPlay ->
        tutorViewModel.updateVoiceSettings(
          engineMode = engine,
          geminiVoice = voice,
          speechRate = rate,
          autoPlay = autoPlay
        )
      },
      onDismiss = {
        voiceManager.stop()
        showVoiceSettingsSheet = false
      }
    )
  }
}

/**
 * Message bubble with user, tutor, and system formats.
 */
@Composable
private fun ChatMessageBubble(
  message: ChatMessage,
  targetLanguageCode: String,
  isPlaying: Boolean = false,
  isBuffering: Boolean = false,
  engineBadge: String? = null,
  onToggleAudio: () -> Unit,
  onRetry: () -> Unit
) {
  when (message.sender) {
    MessageSender.USER -> {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
      ) {
        Surface(
          shape = RoundedCornerShape(18.dp, 18.dp, 4.dp, 18.dp),
          color = MaterialTheme.colorScheme.primary,
          modifier = Modifier.widthIn(max = 300.dp)
        ) {
          Text(
            text = message.text,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
          )
        }
      }
    }

    MessageSender.TUTOR -> {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
      ) {
        Card(
          shape = RoundedCornerShape(18.dp, 18.dp, 18.dp, 4.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = CardDefaults.outlinedCardBorder(),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
          modifier = Modifier.widthIn(max = 320.dp)
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f, fill = false)
              ) {
                Surface(
                  shape = CircleShape,
                  color = MaterialTheme.colorScheme.primaryContainer,
                  modifier = Modifier.size(22.dp)
                ) {
                  Box(contentAlignment = Alignment.Center) {
                    Icon(
                      imageVector = Icons.Filled.SmartToy,
                      contentDescription = null,
                      tint = MaterialTheme.colorScheme.onPrimaryContainer,
                      modifier = Modifier.size(13.dp)
                    )
                  }
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = BrandConfig.AI_TUTOR_NAME,
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.primary
                )
                if (engineBadge != null) {
                  Spacer(modifier = Modifier.width(6.dp))
                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                  ) {
                    Text(
                      text = engineBadge,
                      style = MaterialTheme.typography.labelSmall,
                      color = MaterialTheme.colorScheme.primary,
                      modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp),
                      fontSize = 10.sp
                    )
                  }
                }
              }

              // Audio pronunciation / playback button
              IconButton(
                onClick = onToggleAudio,
                modifier = Modifier
                  .size(28.dp)
                  .testTag("tutor_message_audio_button")
              ) {
                if (isBuffering) {
                  CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                  )
                } else if (isPlaying) {
                  Icon(
                    imageVector = Icons.Filled.Stop,
                    contentDescription = "Stop voice playback",
                    tint = AccentCoral,
                    modifier = Modifier.size(20.dp)
                  )
                } else {
                  Icon(
                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = "Listen to pronunciation",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
              text = message.text,
              style = MaterialTheme.typography.bodyLarge,
              color = MaterialTheme.colorScheme.onSurface
            )

            if (isPlaying) {
              Spacer(modifier = Modifier.height(6.dp))
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 2.dp)
              ) {
                Text(
                  text = "▶ Speaking aloud...",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.primary
                )
              }
            }
          }
        }
      }
    }

    MessageSender.SYSTEM -> {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
          containerColor = if (message.isError) AccentCoral.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant
        ),
        border = if (message.isError) {
          CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(AccentCoral.copy(alpha = 0.5f)))
        } else {
          CardDefaults.outlinedCardBorder()
        },
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = if (message.isError) Icons.Filled.ErrorOutline else Icons.Filled.Info,
              contentDescription = null,
              tint = if (message.isError) AccentCoral else MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = if (message.isError) "Tutor Notice" else "Info",
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
              color = if (message.isError) AccentCoral else MaterialTheme.colorScheme.onSurface
            )
          }

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = message.text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
          )

          if (message.canRetry) {
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedButton(
              onClick = onRetry,
              modifier = Modifier.testTag("tutor_retry_button"),
              shape = RoundedCornerShape(10.dp)
            ) {
              Icon(imageVector = Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Retry Message")
            }
          }
        }
      }
    }
  }
}

/**
 * Animated typing bubble showing tutor is formulating a response.
 */
@Composable
private fun TutorTypingBubble(targetLanguageName: String) {
  val transition = rememberInfiniteTransition(label = "pulse")
  val alpha by transition.animateFloat(
    initialValue = 0.4f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(600),
      repeatMode = RepeatMode.Reverse
    ),
    label = "dotAlpha"
  )

  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.Start
  ) {
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      border = CardDefaults.outlinedCardBorder(),
      modifier = Modifier.padding(vertical = 4.dp)
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        CircularProgressIndicator(
          modifier = Modifier.size(16.dp),
          color = MaterialTheme.colorScheme.primary,
          strokeWidth = 2.dp
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
          text = "Tutor is writing in $targetLanguageName...",
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
          color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = alpha)
        )
      }
    }
  }
}

/**
 * Suggested conversation starters for beginners.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SuggestedPromptsCard(
  targetLanguage: Language,
  nativeLanguage: Language,
  onSelectPrompt: (String) -> Unit
) {
  val samplePrompts = when (targetLanguage.id.lowercase()) {
    "es" -> listOf(
      "👋 ¡Hola! Me llamo Alex y quiero aprender español." to "Introduce yourself",
      "☕ ¿Cómo puedo pedir un café con leche en una cafetería?" to "Order coffee",
      "🗺️ ¿Cómo pregunto dónde está la estación de tren?" to "Ask directions",
      "💡 ¿Cuál es la diferencia entre 'por' y 'para'?" to "Grammar tip",
      "🍽️ ¿Cuáles son las comidas típicas más populares?" to "Food & culture"
    )
    "fr" -> listOf(
      "👋 Bonjour ! Je m'appelle Alex et j'apprends le français." to "Introduce yourself",
      "☕ Un café et un croissant, s'il vous plaît." to "Café ordering",
      "🗺️ Où se trouve la station de métro la plus proche ?" to "Ask directions",
      "💡 Pouvez-vous m'expliquer la règle du féminin ?" to "Grammar tip"
    )
    "de" -> listOf(
      "👋 Hallo! Ich heiße Alex und lerne Deutsch." to "Introduce yourself",
      "☕ Ich möchte bitte einen Kaffee bestellen." to "Order coffee",
      "🗺️ Wo ist die nächste Haltestelle?" to "Directions",
      "💡 Wann benutzt man 'der, die, das'?" to "Articles tip"
    )
    else -> listOf(
      "${targetLanguage.sampleGreeting}! Let's start a beginner conversation." to "Start chatting",
      "How do I say 'Thank you very much' and 'Please'?" to "Polite phrases",
      "Can you teach me 3 common phrases for travelers?" to "Travel essentials",
      "How do I introduce myself in ${targetLanguage.name}?" to "Introductions"
    )
  }

  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
    ),
    border = CardDefaults.outlinedCardBorder()
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Filled.AutoAwesome,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Suggested Conversation Prompts",
          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        samplePrompts.forEach { (prompt, label) ->
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface,
            border = CardDefaults.outlinedCardBorder(),
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .clickable { onSelectPrompt(prompt) }
          ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
              Text(
                text = prompt,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
              )
              Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
              )
            }
          }
        }
      }
    }
  }
}
