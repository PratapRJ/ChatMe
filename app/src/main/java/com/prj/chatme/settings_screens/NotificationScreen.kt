package com.prj.chatme.settings_screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.prj.chatme.CMViewModel
import com.prj.chatme.ui.theme.ChatMeColors
import com.prj.chatme.ui.theme.ChatMeTheme
import com.prj.chatme.ui.theme.ChatMeTypography

@Composable
fun NotificationScreen(navController: NavController, vm: CMViewModel) {
    ChatMeTheme {
        val context = LocalContext.current
        val currentSettings by remember {
            mutableStateOf(vm.getCurrentNotificationSettings(context))
        }
        var notificationsEnabled by remember { mutableStateOf(true) }
        var messagePreviewEnabled by remember { mutableStateOf(true) }
        var vibrationEnabled by remember { mutableStateOf(false) }
        var soundEnabled by remember { mutableStateOf(true) }
        var groupNotificationsEnabled by remember { mutableStateOf(true) }

        // Save settings when they change
        LaunchedEffect(notificationsEnabled, messagePreviewEnabled, vibrationEnabled, soundEnabled) {
            vm.updateNotificationSettings(
                context = context,
                notificationsEnabled = notificationsEnabled,
                showPreview = messagePreviewEnabled,
                soundEnabled = soundEnabled,
                vibrationEnabled = vibrationEnabled
            )
        }

        Scaffold(
            topBar = { NotificationTopBar(navController) },
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
                // Notification Settings Section
                Text(
                    text = "Notification Settings",
                    style = ChatMeTypography.titleLarge,
                    color = ChatMeColors.lightPrimary,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )

                // Enable Notifications
                NotificationSettingItem(
                    title = "Enable Notifications",
                    description = "Turn notifications on/off",
                    isChecked = notificationsEnabled,
                    onCheckedChange = { notificationsEnabled = it }
                )

                // Message Previews
                NotificationSettingItem(
                    title = "Message Previews",
                    description = "Show message content in notifications",
                    isChecked = messagePreviewEnabled,
                    onCheckedChange = { messagePreviewEnabled = it },
                    enabled = notificationsEnabled
                )

                // Vibration
                NotificationSettingItem(
                    title = "Vibration",
                    description = "Vibrate when receiving messages",
                    isChecked = vibrationEnabled,
                    onCheckedChange = { vibrationEnabled = it },
                    enabled = notificationsEnabled
                )

                // Sound
                NotificationSettingItem(
                    title = "Sound",
                    description = "Play sound for notifications",
                    isChecked = soundEnabled,
                    onCheckedChange = { soundEnabled = it },
                    enabled = notificationsEnabled
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Group Notifications Section
                Text(
                    text = "Group Notifications",
                    style = ChatMeTypography.titleLarge,
                    color = ChatMeColors.lightPrimary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                NotificationSettingItem(
                    title = "Group Notifications",
                    description = "Receive notifications for group messages",
                    isChecked = groupNotificationsEnabled,
                    onCheckedChange = { groupNotificationsEnabled = it },
                    enabled = notificationsEnabled
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationTopBar(navController: NavController) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Notifications",
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
private fun NotificationSettingItem(
    title: String,
    description: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = ChatMeTypography.titleMedium,
                color = if (enabled) ChatMeColors.lightOnSurface else ChatMeColors.lightOnSurface.copy(alpha = 0.5f)
            )
            Text(
                text = description,
                style = ChatMeTypography.bodySmall,
                color = if (enabled) ChatMeColors.lightOnSurface.copy(alpha = 0.7f)
                else ChatMeColors.lightOnSurface.copy(alpha = 0.3f)
            )
        }

        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            enabled = enabled
        )
    }
}