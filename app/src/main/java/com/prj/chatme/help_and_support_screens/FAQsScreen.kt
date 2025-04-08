package com.prj.chatme.help_and_support_screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.prj.chatme.CMViewModel
import com.prj.chatme.ui.theme.ChatMeColors
import com.prj.chatme.ui.theme.ChatMeShapes
import com.prj.chatme.ui.theme.ChatMeTheme
import com.prj.chatme.ui.theme.ChatMeTypography

@Composable
fun FAQsScreen(navController: NavController, vm: CMViewModel) {
    ChatMeTheme {
        LaunchedEffect(Unit) {
            vm.fetchFAQs()
        }
        Scaffold(
            topBar = {
                FAQsTopAppBar(
                    onBack = { navController.popBackStack() }
                )
            },
            containerColor = ChatMeColors.lightBackground
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(top = 16.dp)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(faqItems) { faq ->
                    FAQItem(
                        question = faq.question,
                        answer = faq.answer
                    )
                }
//                items(vm.faqs) { faq ->
//                    FAQItem(
//                        question = faq.question,
//                        answer = faq.answer
//                    )
//                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FAQsTopAppBar(onBack: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "FAQs",
                style = ChatMeTypography.titleLarge,
                color = ChatMeColors.darkPrimaryContainer
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
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
private fun FAQItem(question: String, answer: String) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        onClick = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth(),
        shape = ChatMeShapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = ChatMeColors.lightPrimaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = question,
                style = ChatMeTypography.titleMedium,
                color = ChatMeColors.lightOnSurface
            )

            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = answer,
                    style = ChatMeTypography.bodyMedium,
                    color = ChatMeColors.lightOnSurface.copy(alpha = 0.8f)
                )
            }
        }
    }
}

// Sample FAQ data
private val faqItems = listOf(
    FAQ(
        question = "How do I change my profile picture?",
        answer = "Go to your profile screen and tap on your profile picture. You'll be able to select a new image from your gallery."
    ),
    FAQ(
        question = "How can I reset my password?",
        answer = "On the login screen, tap 'Forgot Password' and follow the instructions sent to your registered email."
    ),
    FAQ(
        question = "Why can't I send messages?",
        answer = "Make sure you have an active internet connection. If the problem persists, try restarting the app."
    ),
    FAQ(
        question = "How do I report a user?",
        answer = "Tap and hold on the message or go to the user's profile to find the report option."
    ),
    FAQ(
        question = "Is ChatMe free to use?",
        answer = "Yes, ChatMe is completely free to use with all basic messaging features."
    ),
    FAQ(
        question = "How can I delete my account?",
        answer = "Go to Settings > Account > Delete Account. Note this action is irreversible."
    ),
    FAQ(
        question = "Can I use ChatMe on multiple devices?",
        answer = "Yes, you can be logged in on multiple devices simultaneously."
    )
)

data class FAQ(
    val question: String,
    val answer: String
)