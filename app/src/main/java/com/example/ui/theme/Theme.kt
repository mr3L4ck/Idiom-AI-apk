package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
  primary = TealPrimaryDark,
  onPrimary = TealOnPrimaryDark,
  primaryContainer = TealPrimaryContainerDark,
  onPrimaryContainer = TealOnPrimaryContainerDark,
  secondary = AmberSecondaryDark,
  onSecondary = AmberOnSecondaryDark,
  secondaryContainer = AmberSecondaryContainerDark,
  onSecondaryContainer = AmberOnSecondaryContainerDark,
  tertiary = IndigoTertiaryDark,
  onTertiary = IndigoOnTertiaryDark,
  tertiaryContainer = IndigoTertiaryContainerDark,
  onTertiaryContainer = IndigoOnTertiaryContainerDark,
  background = BackgroundDark,
  onBackground = SurfaceLight,
  surface = SurfaceDark,
  onSurface = SurfaceLight,
  surfaceVariant = SurfaceVariantDark,
  onSurfaceVariant = OutlineLight,
  outline = OutlineDark
)

private val LightColorScheme = lightColorScheme(
  primary = TealPrimary,
  onPrimary = TealOnPrimary,
  primaryContainer = TealPrimaryContainer,
  onPrimaryContainer = TealOnPrimaryContainer,
  secondary = AmberSecondary,
  onSecondary = AmberOnSecondary,
  secondaryContainer = AmberSecondaryContainer,
  onSecondaryContainer = AmberOnSecondaryContainer,
  tertiary = IndigoTertiary,
  onTertiary = IndigoOnTertiary,
  tertiaryContainer = IndigoTertiaryContainer,
  onTertiaryContainer = IndigoOnTertiaryContainer,
  background = BackgroundLight,
  onBackground = BackgroundDark,
  surface = SurfaceLight,
  onSurface = BackgroundDark,
  surfaceVariant = SurfaceVariantLight,
  onSurfaceVariant = BackgroundDark,
  outline = OutlineLight
)

@Composable
fun IdiomTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Keep app distinctive with custom navy/teal palette by default
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    shapes = androidx.compose.material3.Shapes(
      small = DesignTokens.ShapeSmall,
      medium = DesignTokens.ShapeMedium,
      large = DesignTokens.ShapeLarge,
      extraLarge = DesignTokens.ShapeExtraLarge
    ),
    content = content
  )
}

@Composable
fun LinguaSphereTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  IdiomTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}
