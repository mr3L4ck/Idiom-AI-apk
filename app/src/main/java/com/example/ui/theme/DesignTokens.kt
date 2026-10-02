package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Reusable design tokens for consistent spacing, corner radii, elevations,
 * touch targets, and layout constraints throughout the Idiom app.
 */
object DesignTokens {

  // --- Spacing Grid (8dp base) ---
  val Spacing2: Dp = 2.dp
  val Spacing4: Dp = 4.dp
  val Spacing8: Dp = 8.dp
  val Spacing12: Dp = 12.dp
  val Spacing16: Dp = 16.dp
  val Spacing20: Dp = 20.dp
  val Spacing24: Dp = 24.dp
  val Spacing32: Dp = 32.dp
  val Spacing40: Dp = 40.dp
  val Spacing48: Dp = 48.dp

  // --- Corner Radii ---
  val RadiusSmall: Dp = 8.dp
  val RadiusMedium: Dp = 14.dp
  val RadiusLarge: Dp = 20.dp
  val RadiusExtraLarge: Dp = 28.dp
  val RadiusPill: Dp = 100.dp

  // --- Shapes ---
  val ShapeSmall = RoundedCornerShape(RadiusSmall)
  val ShapeMedium = RoundedCornerShape(RadiusMedium)
  val ShapeLarge = RoundedCornerShape(RadiusLarge)
  val ShapeExtraLarge = RoundedCornerShape(RadiusExtraLarge)
  val ShapePill = RoundedCornerShape(RadiusPill)

  // --- Elevations ---
  val ElevationFlat: Dp = 0.dp
  val ElevationCard: Dp = 1.dp
  val ElevationFloating: Dp = 3.dp
  val ElevationModal: Dp = 6.dp

  // --- Touch Targets & Interactive Heights ---
  val MinTouchTarget: Dp = 48.dp
  val ButtonHeightCompact: Dp = 40.dp
  val ButtonHeightStandard: Dp = 48.dp
  val ButtonHeightHero: Dp = 56.dp
  val IconSizeSmall: Dp = 16.dp
  val IconSizeMedium: Dp = 22.dp
  val IconSizeLarge: Dp = 28.dp

  // --- Layout Dimensions ---
  val MaxContentWidth: Dp = 640.dp
}
