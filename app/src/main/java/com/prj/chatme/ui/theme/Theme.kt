package com.prj.chatme.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


// Dark theme variants
val DarkCream = Color(0xFF1E1E1E)
val DarkLightGreen = Color(0xFF7DA67A)
val DarkDarkGreen = Color(0xFF2A5C35)
val DarkOrange = Color(0xFFC45A0E)

private val LightColorPalette = lightColorScheme(
    primary = DarkGreen,
    onPrimary = Color.White,
    primaryContainer = DarkGreen.copy(alpha = 0.1f),
    onPrimaryContainer = DarkGreen,

    secondary = LightGreen,
    onSecondary = Color.Black,
    secondaryContainer = LightGreen.copy(alpha = 0.1f),
    onSecondaryContainer = LightGreen,

    tertiary = Orange,
    onTertiary = Color.White,
    tertiaryContainer = Orange.copy(alpha = 0.1f),
    onTertiaryContainer = Orange,

    background = Cream,
    onBackground = Color(0xFF333333),

    surface = Color.White,
    onSurface = Color(0xFF333333),

    surfaceVariant = Cream,
    onSurfaceVariant = Color(0xFF666666),

    error = Color(0xFFE53935),
    onError = Color.White
)

private val DarkColorPalette = darkColorScheme(
    primary = DarkLightGreen,
    onPrimary = Color.Black,
    primaryContainer = DarkDarkGreen,
    onPrimaryContainer = Color.White,

    secondary = DarkLightGreen,
    onSecondary = Color.Black,
    secondaryContainer = DarkDarkGreen,
    onSecondaryContainer = Color.White,

    tertiary = DarkOrange,
    onTertiary = Color.Black,
    tertiaryContainer = DarkOrange.copy(alpha = 0.2f),
    onTertiaryContainer = Color.White,

    background = DarkCream,
    onBackground = Color.White,

    surface = Color(0xFF2A2A2A),
    onSurface = Color.White,

    surfaceVariant = Color(0xFF3A3A3A),
    onSurfaceVariant = Color(0xFFCCCCCC),

    error = Color(0xFFCF6679),
    onError = Color.Black
)

@Composable
fun ChatMeTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorPalette else LightColorPalette

    MaterialTheme(
        colorScheme = colors,
        typography = ChatMeTypography,
        shapes = ChatMeShapes,
        content = content
    )
}

object ChatMeTheme {
    val colors: ChatMeColors
        @Composable
        get() = ChatMeColors
}

object ChatMeColors {
    // Light Theme Colors
    val lightPrimary = DarkGreen
    val lightSecondary = LightGreen
    val lightTertiary = Orange
    val lightBackground = Cream
    val lightSurface = Color.White
    val lightOnPrimary = Color.White
    val lightOnSecondary = Color.Black
    val lightOnTertiary = Color.White
    val lightOnBackground = Color(0xFF333333)
    val lightOnSurface = Color(0xFF333333)
    val lightPrimaryContainer = DarkGreen.copy(alpha = 0.1f)

    // Dark Theme Colors
    val darkPrimary = DarkLightGreen
    val darkSecondary = DarkLightGreen
    val darkTertiary = DarkOrange
    val darkBackground = DarkCream
    val darkSurface = Color(0xFF2A2A2A)
    val darkOnPrimary = Color.Black
    val darkOnSecondary = Color.Black
    val darkOnTertiary = Color.Black
    val darkOnBackground = Color.White
    val darkOnSurface = Color.White
    val darkPrimaryContainer = DarkDarkGreen
    val darkOverlay = DarkOverlay

    // Common Colors
    val accent = Orange
    val success = Color(0xFF4CAF50)
    val error = Color(0xFFE53935)
    val warning = Color(0xFFFFA000)
    val info = Color(0xFF2196F3)
    val divider = Color(0xFFE0E0E0).copy(alpha = 0.6f)
    val disabled = Color(0xFFBDBDBD)
    val darkRed = Color(0xFF8B0000)

    // Chat Specific
    val sentMessage = DarkGreen
    val receivedMessage = LightGreen
    val chatBackground = Cream
    val messageTime = Color(0xFF666666)
    val statusActive = DarkGreen
    val statusInactive = Color(0xFF424242)

    //Status Colors
    val statusProgressCompleted = Color(0xFF4CAF50)
    val statusProgressActive = Color(0xFFFFA000)
    val statusProgressInactive = Color(0xFFE0E0E0)
}