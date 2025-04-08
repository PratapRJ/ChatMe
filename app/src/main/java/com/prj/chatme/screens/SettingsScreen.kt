package com.prj.chatme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.prj.chatme.CMViewModel
import com.prj.chatme.DestinatinScreen
import com.prj.chatme.navigateTo
import com.prj.chatme.ui.theme.ChatMeColors
import com.prj.chatme.ui.theme.ChatMeTheme
import com.prj.chatme.ui.theme.ChatMeTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController, vm: CMViewModel) {
    var showLogoutDialog by rememberSaveable { mutableStateOf(false) }
    ChatMeTheme {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "Settings",
                            style = ChatMeTypography.titleLarge,
                            color = ChatMeColors.darkPrimaryContainer
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = ChatMeColors.darkPrimaryContainer
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = ChatMeColors.lightPrimaryContainer,
                        titleContentColor = ChatMeColors.darkPrimaryContainer,
                        navigationIconContentColor = ChatMeColors.darkPrimaryContainer
                    )
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(ChatMeColors.lightBackground),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item { SettingsHeader(title = "Account") }

                item {
                    SettingsItem(
                        icon = Icons.Default.Person,
                        title = "Edit Profile",
                        onClick = {
                            navigateTo(navController, DestinatinScreen.EditProfile.route)
                        }
                    )
                }

                item {
                    SettingsItem(
                        icon = Icons.Default.Notifications,
                        title = "Notifications",
                        onClick = { navigateTo(navController, DestinatinScreen.Notification.route)
                             }
                    )
                }

                item {
                    SettingsItem(
                        icon = Icons.Default.PrivacyTip,
                        title = "Privacy",
                        onClick = { navigateTo(navController, DestinatinScreen.Privacy.route)
                             }
                    )
                }

                item { SettingsHeader(title = "App") }

                item {
                    SettingsItem(
                        icon = Icons.Default.Info,
                        title = "About",
                        onClick = { navigateTo(navController, DestinatinScreen.About.route)
                             }
                    )
                }

                item {
                    SettingsItem(
                        icon = Icons.Default.Help,
                        title = "Help & Support",
                        onClick = { navigateTo(navController, DestinatinScreen.HelpAndSupport.route)
                             }
                    )
                }

                item {
                    SettingsItem(
                        icon = Icons.Default.Logout,
                        title = "Log Out",
                        onClick = {
                            showLogoutDialog = true
                        },
                        isDestructive = true
                    )
                }
            }
        }
        // Logout Confirmation Dialog
        if (showLogoutDialog) {
            LogoutConfirmationDialog(
                onDismiss = { showLogoutDialog = false },
                onConfirm = {
                    vm.userIsOffline()
                    vm.logout()
                    navigateTo(navController, DestinatinScreen.Login.route)
                }
            )
        }
    }
}

@Composable
private fun SettingsHeader(title: String) {
    Text(
        text = title,
        style = ChatMeTypography.titleMedium,
        color = ChatMeColors.lightPrimary,
        modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 8.dp)
    )
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isDestructive) ChatMeColors.error else ChatMeColors.lightPrimary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = ChatMeTypography.bodyLarge,
                color = if (isDestructive) ChatMeColors.error else ChatMeColors.lightOnSurface,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Navigate",
                tint = ChatMeColors.lightPrimary.copy(alpha = 0.6f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}