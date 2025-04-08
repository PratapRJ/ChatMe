package com.prj.chatme.settings_screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.prj.chatme.CMViewModel
import com.prj.chatme.DestinatinScreen
import com.prj.chatme.R
import com.prj.chatme.navigateTo
import com.prj.chatme.ui.theme.ChatMeColors
import com.prj.chatme.ui.theme.ChatMeTheme
import com.prj.chatme.ui.theme.ChatMeTypography

@Composable
fun HelpAndSupportScreen(navController: NavController, vm: CMViewModel) {
    val context = LocalContext.current
    ChatMeTheme {
        Scaffold(
            topBar = { HelpAndSupportTopBar(navController) },
            containerColor = ChatMeColors.lightBackground
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Image
                Image(
                    painter = painterResource(R.drawable.ic_help_and_support),
                    contentDescription = "Help and Support",
                    modifier = Modifier
                        .size(160.dp)
                        .padding(16.dp),
                    colorFilter = ColorFilter.tint(ChatMeColors.lightPrimary)                )

                Text(
                    text = "How can we help you?",
                    style = ChatMeTypography.headlineSmall,
                    color = ChatMeColors.lightPrimary,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Support Options
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SupportOptionCard(
                        icon = Icons.Default.Help,
                        title = "FAQs",
                        description = "Find answers to common questions",
                        onClick = {
                            // Navigate to FAQs screen
                            navigateTo(navController, DestinatinScreen.FAQs.route)
                        }
                    )

                    SupportOptionCard(
                        icon = Icons.Default.QuestionAnswer,
                        title = "Contact Support",
                        description = "Chat with our support team",
                        onClick = {
                            // Open support chat
                            navigateTo(navController, DestinatinScreen.SupportChat.route)
                        }
                    )

                    SupportOptionCard(
                        icon = Icons.Default.Email,
                        title = "Email Us",
                        description = "Send us an email with your questions",
                        onClick = {
                            // Open email intent
                            vm.openEmailClient("support@chatme.com",context)

                        }
                    )
                }

                // Additional Help Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Need more help?",
                        style = ChatMeTypography.titleMedium,
                        color = ChatMeColors.lightOnSurface,
                        modifier = Modifier.padding(bottom = 8.dp))

                    Text(text = "Our support team is available 24/7 to assist you with any issues you might be facing.",
                        style = ChatMeTypography.bodyMedium,
                        color = ChatMeColors.lightOnSurface.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HelpAndSupportTopBar(navController: NavController) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Help & Support",
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
private fun SupportOptionCard(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = ChatMeColors.lightSurface,
            contentColor = ChatMeColors.lightOnSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = ChatMeColors.lightPrimary,
                modifier = Modifier.size(32.dp) )
            Column {
                    Text(
                        text = title,
                        style = ChatMeTypography.titleMedium,
                        color = ChatMeColors.lightPrimary)

                    Text(
                        text = description,
                        style = ChatMeTypography.bodyMedium,
                        color = ChatMeColors.lightOnSurface.copy(alpha = 0.7f))
                }
        }
    }
}