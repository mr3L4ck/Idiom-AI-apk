package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R

@Composable
fun WelcomeScreen(
  onGetStarted: () -> Unit,
  onSkipToHome: () -> Unit,
  hasExistingSelection: Boolean,
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
        .padding(horizontal = 24.dp, vertical = 20.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // App badge with pronunciation guide
        Surface(
          shape = RoundedCornerShape(30.dp),
          color = MaterialTheme.colorScheme.primaryContainer,
          modifier = Modifier.padding(top = 12.dp)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(text = "🌐", fontSize = 16.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = com.example.data.BrandConfig.APP_NAME,
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "• /${com.example.data.BrandConfig.PRONUNCIATION_GUIDE}/",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Hero Illustration Card
        Card(
          shape = RoundedCornerShape(24.dp),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
        ) {
          Image(
            painter = painterResource(id = R.drawable.img_welcome_hero),
            contentDescription = "Welcome to ${com.example.data.BrandConfig.APP_NAME} - ${com.example.data.BrandConfig.TAGLINE}",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
          )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Headline
        Text(
          text = stringResource(id = R.string.welcome_headline),
          style = MaterialTheme.typography.headlineMedium.copy(
            fontWeight = FontWeight.ExtraBold,
            lineHeight = 32.sp
          ),
          color = MaterialTheme.colorScheme.onBackground,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Subtitle
        Text(
          text = stringResource(id = R.string.welcome_subtitle),
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Key Value Props
        Column(
          verticalArrangement = Arrangement.spacedBy(10.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          FeaturePill(
            icon = Icons.Filled.Language,
            title = "World Languages Foundation",
            description = "Explore essential world languages with accurate phonetics and phrases"
          )
          FeaturePill(
            icon = Icons.Filled.Timer,
            title = "Bite-Sized Daily Goals",
            description = "Build consistency with easy 5 to 15 minute daily practice sessions"
          )
          FeaturePill(
            icon = Icons.Filled.Psychology,
            title = "${com.example.data.BrandConfig.AI_TUTOR_NAME} & ${com.example.data.BrandConfig.VOICE_FEATURE_NAME}",
            description = "Natural speech practice with patient, personalized tutor feedback"
          )
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Action Area
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Button(
          onClick = onGetStarted,
          modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .testTag("get_started_button"),
          shape = RoundedCornerShape(16.dp),
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
          Text(
            text = stringResource(id = R.string.get_started),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onPrimary
          )
          Spacer(modifier = Modifier.width(8.dp))
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimary
          )
        }

        if (hasExistingSelection) {
          Spacer(modifier = Modifier.height(8.dp))
          TextButton(
            onClick = onSkipToHome,
            modifier = Modifier.testTag("skip_to_home_button")
          ) {
            Text(
              text = "Continue where you left off →",
              style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
              color = MaterialTheme.colorScheme.primary
            )
          }
        }
      }
    }
  }
}

@Composable
private fun FeaturePill(
  icon: ImageVector,
  title: String,
  description: String
) {
  Surface(
    shape = RoundedCornerShape(14.dp),
    color = MaterialTheme.colorScheme.surface,
    tonalElevation = 1.dp,
    border = CardDefaults.outlinedCardBorder()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.size(38.dp)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(20.dp)
          )
        }
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column {
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
    }
  }
}
