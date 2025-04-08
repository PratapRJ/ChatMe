package com.prj.chatme.screens

import android.util.Log
import com.prj.chatme.ui.theme.DarkGreen
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.prj.chatme.CommonDivider
import com.prj.chatme.CommonImage
import com.prj.chatme.CommonProgressBar
import com.prj.chatme.DestinatinScreen
import com.prj.chatme.CMViewModel
import com.prj.chatme.R
import com.prj.chatme.TextFieldWithIcons
import com.prj.chatme.navigateTo
import com.prj.chatme.ui.theme.ChatMeColors
import com.prj.chatme.ui.theme.ChatMeTheme
import com.prj.chatme.ui.theme.ChatMeTypography
import com.prj.chatme.ui.theme.DarkOrange

@Composable
fun UserProfileScreen(navController: NavController, vm: CMViewModel) {
    ChatMeTheme {
        val inProgress = vm.inProgress.value
        val userData = vm.userData.value

        if (inProgress) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = ChatMeColors.lightPrimary)
            }
        } else {
            var showLogoutDialog by rememberSaveable { mutableStateOf(false) }

            Scaffold(
                topBar = {
                    ProfileTopAppBar(
                        onBack = { navController.popBackStack() },
                        onEdit = {
                            navigateTo(navController, DestinatinScreen.EditProfile.route)
                        }
                    )
                },
                bottomBar = {
                    BottomNavigationMenu(
                        selectedItem = BottomNavigationItem.PROFILE,
                        navController = navController
                    )
                }
            ) { padding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .background(ChatMeColors.lightBackground)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Profile Image Section (View Only)
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(bottom = 24.dp)
                        ) {
                            Card(
                                shape = CircleShape,
                                modifier = Modifier.size(120.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = ChatMeColors.lightSurface
                                )
                            ) {
                                CommonImage(
                                    data = userData?.imageUrl,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }

                        // Profile Info Section (View Only)
                        ProfileInfoSection(
                            name = userData?.name ?: "",
                            number = userData?.number ?: "",
                            email = userData?.email ?: "",
                            bio = userData?.bio ?: "No bio yet",
                            modifier = Modifier.padding(bottom = 24.dp)
                        )

                        // Logout Button
                        Button(
                            onClick = { showLogoutDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ChatMeColors.error,
                                contentColor = ChatMeColors.lightOnPrimary
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 32.dp)
                        ) {
                            Text("Logout", modifier = Modifier.padding(8.dp))
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
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileTopAppBar(onBack: () -> Unit, onEdit: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                "My Profile",
                style = ChatMeTypography.titleLarge,
                color = ChatMeColors.darkPrimaryContainer
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = ChatMeColors.darkPrimaryContainer
                )
            }
        },
        actions = {
            TextButton(onClick = onEdit) {
                Text(
                    "Edit",
                    style = ChatMeTypography.bodyLarge,
                    color = ChatMeColors.darkPrimaryContainer
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
private fun ProfileInfoSection(
    name: String,
    number: String,
    email: String,
    bio: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Name Field
        ProfileInfoItem(
            label = "Name",
            value = name,
            icon = Icons.Default.Person
        )

        // Number Field
        ProfileInfoItem(
            label = "Phone",
            value = number,
            icon = Icons.Default.Call
        )

        // Email Field
        ProfileInfoItem(
            label = "Email",
            value = email,
            icon = Icons.Default.Email
        )

        // Bio Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                Icon(
                    Icons.Default.Face,
                    contentDescription = "Bio",
                    tint = ChatMeColors.lightPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Bio",
                    style = ChatMeTypography.bodyMedium,
                    color = ChatMeColors.lightPrimary
                )
            }
            Text(
                text = bio,
                style = ChatMeTypography.bodyMedium,
                color = ChatMeColors.lightOnSurface,
                modifier = Modifier.padding(start = 32.dp)
            )
        }
    }
}

@Composable
private fun ProfileInfoItem(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = label,
            tint = ChatMeColors.lightPrimary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = label,
                style = ChatMeTypography.labelSmall,
                color = ChatMeColors.lightOnSurface.copy(alpha = 0.6f)
            )
            Text(
                text = value.ifEmpty { "Not provided" },
                style = ChatMeTypography.bodyMedium,
                color = ChatMeColors.lightOnSurface
            )
        }
    }
}

// Keep the same LogoutConfirmationDialog as before

@Composable
 fun LogoutConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Default.Warning,
                contentDescription = "Warning",
                tint = ChatMeColors.error
            )
        },
        title = {
            Text(
                text = "Confirm Logout",
                style = ChatMeTypography.titleLarge,
                color = ChatMeColors.error
            )
        },
        text = {
            Text(
                text = "Are you sure you want to logout?",
                style = ChatMeTypography.bodyLarge,
                color = ChatMeColors.lightOnSurface
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = ChatMeColors.error
                )
            ) {
                Text("Logout")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = ChatMeColors.lightOnSurface
                )
            ) {
                Text("Cancel")
            }
        },
        containerColor = ChatMeColors.lightSurface,
        titleContentColor = ChatMeColors.error,
        textContentColor = ChatMeColors.lightOnSurface
    )
}