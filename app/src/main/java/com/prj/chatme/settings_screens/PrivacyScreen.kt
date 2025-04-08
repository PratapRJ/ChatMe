package com.prj.chatme.settings_screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.prj.chatme.CMViewModel
import com.prj.chatme.ui.theme.ChatMeColors
import com.prj.chatme.ui.theme.ChatMeTheme
import com.prj.chatme.ui.theme.ChatMeTypography

@Composable
fun PrivacyScreen(navController: NavController, vm: CMViewModel) {
    ChatMeTheme {
        var showOnlineStatus by remember { mutableStateOf(true) }
        var readReceiptsEnabled by remember { mutableStateOf(true) }
        var typingIndicatorsEnabled by remember { mutableStateOf(true) }
        var profileVisibilityPublic by remember { mutableStateOf(false) }

        Scaffold(
            topBar = { PrivacyTopBar(navController) },
            containerColor = ChatMeColors.lightBackground
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.Top
            ) {
                // Privacy Policy Title
                Text(
                    text = "Privacy Settings",
                    style = ChatMeTypography.headlineSmall,
                    color = ChatMeColors.lightPrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )

                // Online Status
                PrivacySettingItem(
                    title = "Show Online Status",
                    description = "Allow others to see when you're online",
                    isEnabled = showOnlineStatus,
                    onToggle = { showOnlineStatus = it }
                )

                // Read Receipts
                PrivacySettingItem(
                    title = "Read Receipts",
                    description = "Let others know when you've read their messages",
                    isEnabled = readReceiptsEnabled,
                    onToggle = { readReceiptsEnabled = it }
                )

                // Typing Indicators
                PrivacySettingItem(
                    title = "Typing Indicators",
                    description = "Show when you're typing a message",
                    isEnabled = typingIndicatorsEnabled,
                    onToggle = { typingIndicatorsEnabled = it }
                )

                // Profile Visibility
                PrivacySettingItem(
                    title = "Public Profile",
                    description = "Make your profile visible to all users",
                    isEnabled = profileVisibilityPublic,
                    onToggle = { profileVisibilityPublic = it }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Privacy Policy Section
                Text(
                    text = "Privacy Policy",
                    style = ChatMeTypography.titleMedium,
                    color = ChatMeColors.lightPrimary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "At ChatMe, we take your privacy seriously. We collect only the necessary information to provide our services and never share your data with third parties without your consent.",
                    style = ChatMeTypography.bodyMedium,
                    color = ChatMeColors.lightOnSurface,
                    textAlign = TextAlign.Justify,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "Data Collection",
                    style = ChatMeTypography.titleSmall,
                    color = ChatMeColors.lightPrimary,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                Text(
                    text = "• Account information (name, email, phone number)\n" +
                            "• Messages and media you send and receive\n" +
                            "• Device information for security purposes",
                    style = ChatMeTypography.bodyMedium,
                    color = ChatMeColors.lightOnSurface,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "Last Updated: March 2025",
                    style = ChatMeTypography.labelSmall,
                    color = ChatMeColors.lightOnSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PrivacyTopBar(navController: NavController) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Privacy",
                style = ChatMeTypography.titleLarge,
                color = ChatMeColors.darkPrimaryContainer
            )
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = ChatMeColors.darkPrimaryContainer
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = ChatMeColors.lightPrimaryContainer,
            titleContentColor = ChatMeColors.darkPrimaryContainer,
            actionIconContentColor = ChatMeColors.darkPrimaryContainer
        )
    )
}

@Composable
private fun PrivacySettingItem(
    title: String,
    description: String,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = ChatMeTypography.titleMedium,
                    color = ChatMeColors.lightOnSurface
                )
                Text(
                    text = description,
                    style = ChatMeTypography.bodySmall,
                    color = ChatMeColors.lightOnSurface.copy(alpha = 0.7f)
                )
            }
            Switch(
                checked = isEnabled,
                onCheckedChange = onToggle
            )
        }
        Divider(
            modifier = Modifier.padding(top = 12.dp),
            color = ChatMeColors.divider,
            thickness = 1.dp
        )
    }
}