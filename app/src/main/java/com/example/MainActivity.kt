package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MainViewModel
import com.example.ui.navigation.AppNavigation
import com.example.ui.theme.IdiomTheme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val viewModel: MainViewModel = viewModel()
      val preferences by viewModel.userPreferences.collectAsStateWithLifecycle()
      val systemDark = isSystemInDarkTheme()
      val useDarkTheme = when (preferences.themeMode) {
        "DARK" -> true
        "LIGHT" -> false
        else -> systemDark
      }

      IdiomTheme(darkTheme = useDarkTheme) {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = MaterialTheme.colorScheme.background
        ) {
          AppNavigation(viewModel = viewModel)
        }
      }
    }
  }
}
