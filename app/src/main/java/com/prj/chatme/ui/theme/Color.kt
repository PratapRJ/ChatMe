package com.prj.chatme.ui.theme

import androidx.compose.ui.graphics.Color

// New Primary Colors based on your palette
val Cream = Color(0xFFF8F5E9)       // Light background color
val LightGreen = Color(0xFF9DC08B)  // Secondary color
val DarkGreen = Color(0xFF3A7D44)   // Primary color
val Orange = Color(0xFFDF6D14)      // Accent color

// Updated Color Scheme
val PrimaryColor = DarkGreen
val SecondaryColor = LightGreen
val BackgroundColor = Cream
val AccentColor = Orange

// Text Colors
val TextPrimary = Color(0xFF333333)  // Dark text for light background
val TextSecondary = Color(0xFF666666)
val TextOnPrimary = Color.White      // Text on primary colored buttons/surfaces
val TextOnSecondary = Color.Black    // Text on secondary colored surfaces

// Status Colors
val SuccessColor = Color(0xFF4CAF50)
val ErrorColor = Color(0xFFE53935)
val WarningColor = Color(0xFFFFA000)
val InfoColor = Color(0xFF2196F3)
val sentMessageBackgroundColor = Color(0xFFd8fdd2)

// UI Colors
val DividerColor = Color(0xFFE0E0E0).copy(alpha = 0.6f)
val DisabledColor = Color(0xFFBDBDBD)
val RippleColor = Color.White.copy(alpha = 0.2f)

// Chat Specific Colors
val SentMessageColor = PrimaryColor
val ReceivedMessageColor = SecondaryColor
val ChatBackground = BackgroundColor
val DarkChatBackground = Color(0xFF121212)  // For dark theme
val MessageTimeColor = TextSecondary.copy(alpha = 0.6f)

// Dark Theme Colors
val DarkPrimary = LightGreen
val DarkSecondary = PrimaryColor
val DarkBackground = Color(0xFF121212)
val DarkSurface = Color(0xFF1E1E1E)
val DarkTextPrimary = Color.White
val DarkTextSecondary = Color(0xFFBDBDBD)

// Status Colors (unchanged)
val StatusProgressActive = PrimaryColor
val StatusProgressInactive = Color(0xFF424242)
val StatusProgressCompleted = SuccessColor
val DarkOverlay = Color(0x80000000)  // 50% transparent black

