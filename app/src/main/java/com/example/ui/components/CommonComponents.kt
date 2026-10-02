package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Language
import com.example.data.PhraseCard
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AmberSecondary

@Composable
fun LanguageCard(
  language: Language,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
  val backgroundColor = if (isSelected) {
    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
  } else {
    MaterialTheme.colorScheme.surface
  }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .border(
        width = if (isSelected) 2.dp else 1.dp,
        color = borderColor,
        shape = RoundedCornerShape(16.dp)
      )
      .clickable(onClick = onClick)
      .testTag("language_item_${language.id}"),
    colors = CardDefaults.cardColors(containerColor = backgroundColor),
    shape = RoundedCornerShape(16.dp),
    elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 3.dp else 1.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Flag badge
      Surface(
        modifier = Modifier.size(46.dp),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceVariant
      ) {
        Box(contentAlignment = Alignment.Center) {
          Text(text = language.flagEmoji, fontSize = 24.sp)
        }
      }

      Spacer(modifier = Modifier.width(14.dp))

      // Language info
      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = language.name,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "• ${language.nativeName}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }

        Spacer(modifier = Modifier.height(3.dp))

        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = language.region,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary
          )
          Text(
            text = "•",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline
          )
          Text(
            text = language.speakers,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Capability Badges
        Row(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          DifficultyBadge(difficulty = language.difficulty)

          if (language.isRtl) {
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = MaterialTheme.colorScheme.secondaryContainer
            ) {
              Text(
                text = "RTL",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
              )
            }
          }

          Surface(
            shape = RoundedCornerShape(4.dp),
            color = when (language.capabilities.ttsLevel) {
              com.example.data.TtsCapabilityLevel.NEURAL_AND_DEVICE -> MaterialTheme.colorScheme.primaryContainer
              com.example.data.TtsCapabilityLevel.DEVICE_ONLY -> MaterialTheme.colorScheme.surfaceVariant
              com.example.data.TtsCapabilityLevel.NOT_SUPPORTED -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
            }
          ) {
            Text(
              text = when (language.capabilities.ttsLevel) {
                com.example.data.TtsCapabilityLevel.NEURAL_AND_DEVICE -> "🔊 Voice"
                com.example.data.TtsCapabilityLevel.DEVICE_ONLY -> "🔉 System TTS"
                com.example.data.TtsCapabilityLevel.NOT_SUPPORTED -> "📝 Text Only"
              },
              style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
              color = when (language.capabilities.ttsLevel) {
                com.example.data.TtsCapabilityLevel.NEURAL_AND_DEVICE -> MaterialTheme.colorScheme.onPrimaryContainer
                com.example.data.TtsCapabilityLevel.DEVICE_ONLY -> MaterialTheme.colorScheme.onSurfaceVariant
                com.example.data.TtsCapabilityLevel.NOT_SUPPORTED -> MaterialTheme.colorScheme.onErrorContainer
              },
              modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.width(8.dp))

      // Selection checkmark
      Icon(
        imageVector = if (isSelected) Icons.Filled.CheckCircle else Icons.Outlined.Circle,
        contentDescription = if (isSelected) "Selected" else "Not selected",
        tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
        modifier = Modifier.size(24.dp)
      )
    }
  }
}

@Composable
fun DifficultyBadge(difficulty: String) {
  val (bgColor, textColor) = when (difficulty) {
    "Beginner Friendly" -> Pair(AccentGreen.copy(alpha = 0.15f), AccentGreen)
    "Moderate" -> Pair(AmberSecondary.copy(alpha = 0.15f), AmberSecondary)
    else -> Pair(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f), MaterialTheme.colorScheme.tertiary)
  }

  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(6.dp))
      .background(bgColor)
      .padding(horizontal = 6.dp, vertical = 2.dp)
  ) {
    Text(
      text = difficulty,
      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
      color = textColor
    )
  }
}

@Composable
fun StatChip(
  icon: ImageVector,
  iconTint: Color,
  label: String,
  value: String,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(12.dp),
    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      Icon(
        imageVector = icon,
        contentDescription = label,
        tint = iconTint,
        modifier = Modifier.size(16.dp)
      )
      Text(
        text = value,
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface
      )
    }
  }
}

@Composable
fun DailyGoalCard(
  currentMinutes: Int,
  goalMinutes: Int,
  onAdjustGoal: () -> Unit,
  modifier: Modifier = Modifier
) {
  val progress = (currentMinutes.toFloat() / goalMinutes.toFloat()).coerceIn(0f, 1f)
  val animatedProgress by animateFloatAsState(
    targetValue = progress,
    animationSpec = tween(durationMillis = 800),
    label = "GoalProgress"
  )

  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
    ),
    border = CardDefaults.outlinedCardBorder()
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
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(32.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(18.dp)
              )
            }
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "Daily Goal",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "$currentMinutes of $goalMinutes mins completed",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Surface(
          shape = RoundedCornerShape(10.dp),
          color = MaterialTheme.colorScheme.surface,
          modifier = Modifier.clickable(onClick = onAdjustGoal)
        ) {
          Text(
            text = "Edit Goal",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      LinearProgressIndicator(
        progress = { animatedProgress },
        modifier = Modifier
          .fillMaxWidth()
          .height(10.dp)
          .clip(RoundedCornerShape(5.dp)),
        color = MaterialTheme.colorScheme.primary,
        trackColor = MaterialTheme.colorScheme.surfaceVariant
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = if (progress >= 1f) {
          "🎉 Goal achieved today! You're on fire!"
        } else {
          "🔥 ${goalMinutes - currentMinutes} mins remaining to meet today's streak target"
        },
        style = MaterialTheme.typography.bodySmall,
        color = if (progress >= 1f) AccentGreen else MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}

@Composable
fun PhraseInteractiveCard(
  phrase: PhraseCard,
  index: Int,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(
      modifier = Modifier.padding(16.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = CircleShape,
          color = MaterialTheme.colorScheme.secondaryContainer,
          modifier = Modifier.size(24.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Text(
              text = "$index",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSecondaryContainer
            )
          }
        }

        Text(
          text = phrase.contextNote,
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = phrase.original,
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = "🗣️ ${phrase.pronunciation}",
        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
        color = MaterialTheme.colorScheme.primary
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = phrase.translation,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}
