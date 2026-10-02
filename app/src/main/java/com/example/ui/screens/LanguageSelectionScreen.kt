package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LanguageCatalog
import com.example.ui.components.LanguageCard

enum class SelectionStep {
  NATIVE_LANGUAGE,
  TARGET_LANGUAGE
}

@Composable
fun LanguageSelectionScreen(
  initialNativeId: String,
  initialLearningId: String,
  onLanguagesConfirmed: (nativeId: String, learningId: String) -> Unit,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler(onBack = onBack)

  var selectedStepIndex by rememberSaveable { mutableIntStateOf(1) } // Default to target or allow toggle
  var selectedNativeId by rememberSaveable { mutableStateOf(initialNativeId) }
  var selectedLearningId by rememberSaveable { mutableStateOf(initialLearningId) }
  var searchQuery by rememberSaveable { mutableStateOf("") }
  var selectedRegionFilter by rememberSaveable { mutableStateOf("All") }

  val regions = listOf("All", "Popular", "Europe", "Asia", "Americas", "Africa & Middle East")

  val filteredLanguages by remember(searchQuery, selectedRegionFilter) {
    derivedStateOf {
      LanguageCatalog.filterLanguages(searchQuery, selectedRegionFilter)
    }
  }

  val nativeLang = remember(selectedNativeId) {
    LanguageCatalog.getLanguageById(selectedNativeId)
  }
  val learningLang = remember(selectedLearningId) {
    LanguageCatalog.getLanguageById(selectedLearningId)
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
      // Top App Bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(
          onClick = onBack,
          modifier = Modifier.testTag("language_back_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = MaterialTheme.colorScheme.onBackground
          )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Column {
          Text(
            text = "Language Selection",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
          )
          Text(
            text = "Data-driven catalog ready for 200+ world languages",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      // Step Tabs
      TabRow(
        selectedTabIndex = selectedStepIndex,
        containerColor = MaterialTheme.colorScheme.surface,
        indicator = { tabPositions ->
          TabRowDefaults.SecondaryIndicator(
            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedStepIndex]),
            color = MaterialTheme.colorScheme.primary,
            height = 3.dp
          )
        }
      ) {
        Tab(
          selected = selectedStepIndex == 0,
          onClick = { selectedStepIndex = 0 },
          text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(text = nativeLang.flagEmoji, fontSize = 16.sp)
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "1. I speak (${nativeLang.name})",
                fontWeight = if (selectedStepIndex == 0) FontWeight.Bold else FontWeight.Normal
              )
            }
          }
        )
        Tab(
          selected = selectedStepIndex == 1,
          onClick = { selectedStepIndex = 1 },
          text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(text = learningLang.flagEmoji, fontSize = 16.sp)
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "2. I learn (${learningLang.name})",
                fontWeight = if (selectedStepIndex == 1) FontWeight.Bold else FontWeight.Normal
              )
            }
          }
        )
      }

      // Context prompt banner
      Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = if (selectedStepIndex == 0) {
              "Select your primary / native language:"
            } else {
              "Select the language you want to master:"
            },
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.primaryContainer
          ) {
            Text(
              text = "${filteredLanguages.size} available",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onPrimaryContainer,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
          }
        }
      }

      // Search Field
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp)
          .testTag("language_search_input"),
        placeholder = { Text("Search by language, script, or country…") },
        leadingIcon = {
          Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = "Search",
            tint = MaterialTheme.colorScheme.primary
          )
        },
        trailingIcon = {
          if (searchQuery.isNotEmpty()) {
            IconButton(onClick = { searchQuery = "" }) {
              Icon(
                imageVector = Icons.Filled.Clear,
                contentDescription = "Clear search",
                tint = MaterialTheme.colorScheme.outline
              )
            }
          }
        },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedContainerColor = MaterialTheme.colorScheme.surface,
          unfocusedContainerColor = MaterialTheme.colorScheme.surface,
          focusedBorderColor = MaterialTheme.colorScheme.primary,
          unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        )
      )

      // Filter chips
      LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(bottom = 6.dp)
      ) {
        items(regions) { region ->
          val isSelected = selectedRegionFilter == region
          FilterChip(
            selected = isSelected,
            onClick = { selectedRegionFilter = region },
            label = { Text(region) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = MaterialTheme.colorScheme.primary,
              selectedLabelColor = MaterialTheme.colorScheme.onPrimary
            )
          )
        }
      }

      // Language Cards List
      LazyColumn(
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(filteredLanguages, key = { it.id }) { language ->
          val isSelected = if (selectedStepIndex == 0) {
            language.id == selectedNativeId
          } else {
            language.id == selectedLearningId
          }

          LanguageCard(
            language = language,
            isSelected = isSelected,
            onClick = {
              if (selectedStepIndex == 0) {
                selectedNativeId = language.id
              } else {
                selectedLearningId = language.id
              }
            }
          )
        }
      }

      // Bottom persistent confirmation bar
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
          // Summary pills
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "Path: ",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
              ) {
                Text(
                  text = "${nativeLang.flagEmoji} ${nativeLang.name}",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
              Text(
                text = " ➔ ",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
              )
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primaryContainer
              ) {
                Text(
                  text = "${learningLang.flagEmoji} ${learningLang.name}",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onPrimaryContainer,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }

            if (selectedNativeId == selectedLearningId) {
              Text(
                text = "Pick different languages",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Button
          if (selectedStepIndex == 0) {
            Button(
              onClick = { selectedStepIndex = 1 },
              modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("next_step_button"),
              shape = RoundedCornerShape(14.dp),
              colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
              Text(
                text = "Next: Choose Language to Learn",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
            }
          } else {
            Button(
              onClick = {
                onLanguagesConfirmed(selectedNativeId, selectedLearningId)
              },
              modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("confirm_languages_button"),
              shape = RoundedCornerShape(14.dp),
              colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
              Icon(imageVector = Icons.Filled.Check, contentDescription = null)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Start Learning ${learningLang.name}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
              )
            }
          }
        }
      }
    }
  }
}
