package com.example.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AVAILABLE_GEMINI_VOICES
import com.example.data.BrandConfig
import com.example.data.Language
import com.example.ui.theme.AccentCoral
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AmberSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TutorVoiceSettingsSheet(
  currentEngine: String,
  currentVoice: String,
  currentRate: Float,
  currentAutoPlay: Boolean,
  targetLanguage: Language,
  isApiKeyConfigured: Boolean,
  isPlayingPreview: Boolean,
  onPreview: (engine: String, voice: String, rate: Float, sampleText: String) -> Unit,
  onStopPreview: () -> Unit,
  onSave: (engine: String, voice: String, rate: Float, autoPlay: Boolean) -> Unit,
  onDismiss: () -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  var selectedEngine by remember { mutableStateOf(currentEngine) }
  var selectedVoice by remember { mutableStateOf(currentVoice) }
  var selectedRate by remember { mutableFloatStateOf(currentRate) }
  var autoPlayEnabled by remember { mutableStateOf(currentAutoPlay) }

  val previewSampleText = remember(targetLanguage.id) {
    when (targetLanguage.id.lowercase()) {
      "es" -> "¡Hola! Soy tu tutor en ${BrandConfig.APP_NAME}. ¿Cómo puedo ayudarte a practicar español hoy?"
      "fr" -> "Bonjour ! Je suis votre tuteur en ${BrandConfig.APP_NAME}. Pratiquons le français ensemble aujourd'hui."
      "de" -> "Hallo! Ich bin dein Tutor bei ${BrandConfig.APP_NAME}. Lass uns zusammen Konversation üben."
      "ja" -> "こんにちは！${BrandConfig.APP_NAME}の日本語チューターです。一緒に自然な会話を練習しましょう。"
      "ko" -> "안녕하세요! ${BrandConfig.APP_NAME} 한국어 튜터입니다. 오늘 어떤 대화를 연습해볼까요?"
      "pt" -> "Olá! Sou seu tutor no ${BrandConfig.APP_NAME}. Como posso ajudar com seu português hoje?"
      "it" -> "Ciao! Sono il tuo tutor su ${BrandConfig.APP_NAME}. Di cosa vorresti parlare oggi?"
      "ar" -> "مرحباً بك! أنا معلمك في ${BrandConfig.APP_NAME}. دعنا نتمرن على المحادثة معاً اليوم."
      else -> "Hello! I am your ${targetLanguage.name} tutor on ${BrandConfig.APP_NAME}. Let's practice speaking naturally together."
    }
  }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surface
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 8.dp)
        .verticalScroll(rememberScrollState())
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(36.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Filled.RecordVoiceOver,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(20.dp)
              )
            }
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "${BrandConfig.VOICE_FEATURE_NAME} & Pacing",
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Natural speech delivery for ${targetLanguage.flagEmoji} ${targetLanguage.name}",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        IconButton(onClick = onDismiss) {
          Icon(imageVector = Icons.Filled.Close, contentDescription = "Close settings")
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Section 1: Speech Engine Mode Selection
      Text(
        text = "SPEECH ENGINE",
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.primary
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Gemini Neural Voice Option Card
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .border(
            width = if (selectedEngine == "gemini_neural") 2.dp else 1.dp,
            color = if (selectedEngine == "gemini_neural") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
            shape = RoundedCornerShape(16.dp)
          )
          .clickable { selectedEngine = "gemini_neural" }
          .testTag("engine_gemini_neural_card"),
        colors = CardDefaults.cardColors(
          containerColor = if (selectedEngine == "gemini_neural") {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
          } else {
            MaterialTheme.colorScheme.surface
          }
        )
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Filled.AutoAwesome,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = BrandConfig.VOICE_FEATURE_NAME,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
            }

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = if (isApiKeyConfigured) AccentGreen.copy(alpha = 0.15f) else AmberSecondary.copy(alpha = 0.15f)
            ) {
              Text(
                text = if (isApiKeyConfigured) "● ${BrandConfig.AI_STATUS_READY}" else "○ Setup Required",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = if (isApiKeyConfigured) AccentGreen else AmberSecondary,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = "Expressive neural speech with authentic pronunciation, natural pauses, and conversational cadence.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Android System Device Option Card
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .border(
            width = if (selectedEngine == "device_tts") 2.dp else 1.dp,
            color = if (selectedEngine == "device_tts") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
            shape = RoundedCornerShape(16.dp)
          )
          .clickable { selectedEngine = "device_tts" }
          .testTag("engine_device_tts_card"),
        colors = CardDefaults.cardColors(
          containerColor = if (selectedEngine == "device_tts") {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
          } else {
            MaterialTheme.colorScheme.surface
          }
        )
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Android System Voice",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
            }

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = MaterialTheme.colorScheme.surfaceVariant
            ) {
              Text(
                text = "100% Offline",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = "On-device TextToSpeech engine. Operates locally with zero network quota, instant playback, and offline availability.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Section 2: Neural Voice Persona (Visible when Gemini Neural selected)
      if (selectedEngine == "gemini_neural") {
        Text(
          text = "${BrandConfig.VOICE_FEATURE_NAME.uppercase()} PERSONAS",
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        AVAILABLE_GEMINI_VOICES.forEach { voice ->
          val isSelected = selectedVoice == voice.voiceName

          Card(
            modifier = Modifier
              .fillMaxWidth()
              .padding(bottom = 8.dp)
              .clip(RoundedCornerShape(14.dp))
              .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                shape = RoundedCornerShape(14.dp)
              )
              .clickable { selectedVoice = voice.voiceName }
              .testTag("voice_option_${voice.voiceName.lowercase()}"),
            colors = CardDefaults.cardColors(
              containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
              } else {
                MaterialTheme.colorScheme.surface
              }
            )
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = voice.displayName,
                    style = MaterialTheme.typography.bodyLarge.copy(
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f)
                  ) {
                    Text(
                      text = voice.toneTrait,
                      style = MaterialTheme.typography.labelSmall,
                      color = MaterialTheme.colorScheme.onSecondaryContainer,
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }
                }
                Text(
                  text = voice.description,
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }

              if (isSelected) {
                Surface(
                  shape = CircleShape,
                  color = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(24.dp)
                ) {
                  Box(contentAlignment = Alignment.Center) {
                    Icon(
                      imageVector = Icons.Filled.Check,
                      contentDescription = "Selected",
                      tint = Color.White,
                      modifier = Modifier.size(16.dp)
                    )
                  }
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))
      }

      // Section 3: Speaking Pacing / Speed
      Text(
        text = "SPEAKING SPEED / PACING",
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.primary
      )

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        listOf(
          Triple(0.75f, "0.75x", "Learner Slow"),
          Triple(1.0f, "1.0x", "Natural Pace"),
          Triple(1.25f, "1.25x", "Brisk / Native")
        ).forEach { (rate, label, desc) ->
          val isSelected = selectedRate == rate

          Card(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(12.dp))
              .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
              )
              .clickable { selectedRate = rate }
              .testTag("rate_option_$label"),
            colors = CardDefaults.cardColors(
              containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surface
            )
          ) {
            Column(
              modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = label,
                style = MaterialTheme.typography.titleSmall.copy(
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                ),
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = desc,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Section 4: Hands-free Auto-Speak Toggle
      Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Auto-Play Spoken Replies",
              style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Speaks tutor replies aloud automatically for a conversational flow",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          Switch(
            checked = autoPlayEnabled,
            onCheckedChange = { autoPlayEnabled = it },
            modifier = Modifier.testTag("auto_play_voice_switch")
          )
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Section 5: Voice Preview & Test
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Preview Audio Button
        OutlinedButton(
          onClick = {
            if (isPlayingPreview) {
              onStopPreview()
            } else {
              onPreview(selectedEngine, selectedVoice, selectedRate, previewSampleText)
            }
          },
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .weight(1f)
            .height(48.dp)
            .testTag("preview_voice_button")
        ) {
          Icon(
            imageVector = if (isPlayingPreview) Icons.Filled.Stop else Icons.AutoMirrored.Filled.VolumeUp,
            contentDescription = null,
            tint = if (isPlayingPreview) AccentCoral else MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = if (isPlayingPreview) "Stop Sample" else "Preview Voice",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = if (isPlayingPreview) AccentCoral else MaterialTheme.colorScheme.primary
          )
        }

        // Save & Apply Button
        Button(
          onClick = {
            onStopPreview()
            onSave(selectedEngine, selectedVoice, selectedRate, autoPlayEnabled)
            onDismiss()
          },
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
          modifier = Modifier
            .weight(1f)
            .height(48.dp)
            .testTag("save_voice_settings_button")
        ) {
          Text(
            text = "Apply Settings",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
          )
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}
