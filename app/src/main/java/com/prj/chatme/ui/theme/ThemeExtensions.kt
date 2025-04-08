package com.prj.chatme.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun chatTitleTextStyle(): TextStyle {
    return ChatMeTypography.titleLarge.copy(
        fontWeight = FontWeight.Bold,
        color = ChatMeTheme.colors.lightOnSurface
    )
}

@Composable
fun chatMessageTextStyle(): TextStyle {
    return ChatMeTypography.bodyMedium.copy(
        color = ChatMeTheme.colors.lightOnSurface.copy(alpha = 0.8f)
    )
}

@Composable
fun chatTimestampTextStyle(): TextStyle {
    return ChatMeTypography.labelSmall.copy(
        color = ChatMeTheme.colors.lightOnSurface.copy(alpha = 0.6f)
    )
}

@Composable
fun statusUsernameTextStyle(): TextStyle {
    return ChatMeTypography.titleMedium.copy(
        fontWeight = FontWeight.SemiBold,
        color = Color.White
    )
}

@Composable
fun buttonTextStyle(): TextStyle {
    return ChatMeTypography.labelLarge.copy(
        fontWeight = FontWeight.SemiBold,
        color = ChatMeTheme.colors.lightOnPrimary,
        letterSpacing = 0.5.sp
    )
}

@Composable
fun textFieldLabelStyle(): TextStyle {
    return ChatMeTypography.labelMedium.copy(
        color = ChatMeTheme.colors.lightOnSurface.copy(alpha = 0.6f)
    )
}

@Composable
fun errorTextStyle(): TextStyle {
    return ChatMeTypography.labelMedium.copy(
        color = ChatMeTheme.colors.error
    )
}