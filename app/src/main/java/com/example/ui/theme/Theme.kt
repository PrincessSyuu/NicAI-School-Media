package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val NovaDarkColorScheme = darkColorScheme(
    primary = ElectricIndigo,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF242759),
    onPrimaryContainer = Color(0xFFE0E2FF),
    secondary = AquaGlow,
    onSecondary = ObsidianBackground,
    secondaryContainer = Color(0xFF0B3747),
    onSecondaryContainer = Color(0xFFA5F3FC),
    tertiary = MintSoft,
    onTertiary = ObsidianBackground,
    tertiaryContainer = Color(0xFF0C3B2D),
    onTertiaryContainer = Color(0xFFA7F3D0),
    background = ObsidianBackground,
    onBackground = TextPrimaryDark,
    surface = MidnightSlateSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = MidnightSlateVariant,
    onSurfaceVariant = TextSecondaryDark,
    surfaceContainerHigh = MidnightSlateElevated,
    outline = MidnightOutline,
    error = CoralRose,
    onError = ObsidianBackground
)

private val NovaLightColorScheme = lightColorScheme(
    primary = ElectricIndigo,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0E7FF),
    onPrimaryContainer = Color(0xFF1E1B4B),
    secondary = CyanGlow,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCFFAFE),
    onSecondaryContainer = Color(0xFF164E63),
    tertiary = MintDeep,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFD1FAE5),
    onTertiaryContainer = Color(0xFF064E3B),
    background = WarmAlabasterBackground,
    onBackground = TextPrimaryLight,
    surface = SoftIceSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = SoftIceVariant,
    onSurfaceVariant = TextSecondaryLight,
    outline = SoftIceOutline,
    error = ErrorCrimson,
    onError = Color.White
)

val NovaShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun NovaDeckTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) NovaDarkColorScheme else NovaLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = NovaDeckTypography,
        shapes = NovaShapes,
        content = content
    )
}
