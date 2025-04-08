package com.prj.chatme.settings_screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.prj.chatme.CMViewModel
import com.prj.chatme.R
import com.prj.chatme.ui.theme.ChatMeColors
import com.prj.chatme.ui.theme.ChatMeTheme
import com.prj.chatme.ui.theme.ChatMeTypography

@Composable
fun AboutScreen(navController: NavController, vm: CMViewModel) {
    ChatMeTheme {
        Scaffold(
            topBar = { AboutTopBar(navController) },
            containerColor = ChatMeColors.lightBackground
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                // App Logo
                Image(
                    painter = painterResource(R.drawable.ic_launcher_round),
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .size(120.dp)
                        .padding(top = 32.dp)
                )

                // App Name
                Text(
                    text = "ChatMe",
                    style = ChatMeTypography.headlineMedium,
                    color = ChatMeColors.lightPrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp)
                )

                // Version Info
                Text(
                    text = "Version 1.0.0",
                    style = ChatMeTypography.bodyMedium,
                    color = ChatMeColors.lightOnSurface.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // About Content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    Text(
                        text = "About ChatMe",
                        style = ChatMeTypography.titleLarge,
                        color = ChatMeColors.lightOnSurface,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = "ChatMe is a modern messaging application that helps you stay connected with your friends and family. Our mission is to provide a secure, fast, and user-friendly chat experience.",
                        style = ChatMeTypography.bodyMedium,
                        color = ChatMeColors.lightOnSurface,
                        textAlign = TextAlign.Justify,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Text(
                        text = "Features",
                        style = ChatMeTypography.titleMedium,
                        color = ChatMeColors.lightPrimary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = "• Secure end-to-end encrypted messaging\n" +
                                "• Status updates with images\n" +
                                "• Group chats\n" +
                                "• Read receipts\n" +
                                "• Dark mode support",
                        style = ChatMeTypography.bodyMedium,
                        color = ChatMeColors.lightOnSurface,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Text(
                        text = "Contact Us",
                        style = ChatMeTypography.titleMedium,
                        color = ChatMeColors.lightPrimary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = "For any questions or feedback, please contact us at:\nsupport@chatme.com",
                        style = ChatMeTypography.bodyMedium,
                        color = ChatMeColors.lightOnSurface,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AboutTopBar(navController: NavController) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "About",
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