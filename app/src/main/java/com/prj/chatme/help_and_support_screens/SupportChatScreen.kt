package com.prj.chatme.help_and_support_screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.prj.chatme.CMViewModel
import com.prj.chatme.DestinatinScreen
import com.prj.chatme.R
import com.prj.chatme.data.SupportMessage
//import com.prj.chatme.data.SupportMessage
import com.prj.chatme.navigateTo
import com.prj.chatme.ui.theme.ChatMeColors
import com.prj.chatme.ui.theme.ChatMeShapes
import com.prj.chatme.ui.theme.ChatMeTheme
import com.prj.chatme.ui.theme.ChatMeTypography
import kotlinx.coroutines.delay

@Composable
fun SupportChatScreen(navController: NavController, vm: CMViewModel) {
    ChatMeTheme {
        val inProgress = vm.inProgress.value
       val supportMessages = vm.supportMessages.value
        val userData = vm.userData.value

        var messageText by remember { mutableStateOf("") }
        var showSendingIndicator by remember { mutableStateOf(false) }

        Scaffold(
            topBar = {
                SupportTopAppBar(
                    onBack = { navController.popBackStack() }
                )
            },
            bottomBar = {
                MessageInputBar(
                    message = messageText,
                    onMessageChange = { messageText = it },
                    onSend = {
                        if (messageText.isNotBlank()) {
                            showSendingIndicator = true
                            vm.sendSupportMessage(messageText) {
                                showSendingIndicator = false
                                messageText = ""
                            }
                        }
                    },
                    showSendingIndicator = showSendingIndicator
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(ChatMeColors.lightBackground)
            ) {
                when {
                    inProgress -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = ChatMeColors.lightPrimary)
                        }
                    }
                    supportMessages.isEmpty() -> EmptySupportPlaceholder()
                    else -> SupportMessageList(
                        messages = supportMessages,
                        currentUserId = userData?.userId
                    )
                }
            }
        }

        // Scroll to bottom when new messages arrive
        LaunchedEffect(supportMessages.size) {
            delay(100) // Small delay to allow composition
            // You'd typically scroll to bottom here if using a scroll state
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SupportTopAppBar(onBack: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                "Support Chat",
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
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = ChatMeColors.lightPrimaryContainer,
            titleContentColor = ChatMeColors.darkPrimaryContainer,
            actionIconContentColor = ChatMeColors.darkPrimaryContainer
        )
    )
}

@Composable
private fun SupportMessageList(
    messages: List<SupportMessage>,
    currentUserId: String?
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        reverseLayout = true,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(messages) { message ->
            SupportMessageItem(
                message = message,
                isCurrentUser = message.senderId == currentUserId
            )
        }
    }
}

@Composable
private fun SupportMessageItem(
    message: SupportMessage,
    isCurrentUser: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalAlignment = if (isCurrentUser) Alignment.End else Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start
        ) {
            if (!isCurrentUser) {
                AsyncImage(
                    model = message.senderImageUrl,
                    contentDescription = "Support Agent",
                    modifier = Modifier
                        .size(32.dp)
                        .clip(ChatMeShapes.small),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            Column(
                horizontalAlignment = if (isCurrentUser) Alignment.End else Alignment.Start
            ) {
                if (!isCurrentUser) {
                    Text(
                        text = message.senderName ?: "Support",
                        style = ChatMeTypography.labelMedium,
                        color = ChatMeColors.lightOnSurface.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                }

                Text(
                    text = message.text?:"",
                    modifier = Modifier
                        .background(
                            color = if (isCurrentUser) ChatMeColors.lightPrimary else ChatMeColors.lightSurface,
                            shape = ChatMeShapes.medium
                        )
                        .padding(12.dp),
                    style = ChatMeTypography.bodyMedium,
                    color = if (isCurrentUser) ChatMeColors.lightOnPrimary else ChatMeColors.lightOnSurface
                )

                Text(
                    text = message.timestamp?.let { formatTimestamp(it) } ?: "",
                    style = ChatMeTypography.labelSmall,
                    color = ChatMeColors.lightOnSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MessageInputBar(
    message: String,
    onMessageChange: (String) -> Unit,
    onSend: () -> Unit,
    showSendingIndicator: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(ChatMeColors.lightBackground),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = message,
            onValueChange = onMessageChange,
            modifier = Modifier.weight(1f),
            placeholder = {
                Text(
                    "Type your message...",
                    color = ChatMeColors.lightOnSurface.copy(alpha = 0.6f))
            },
            shape = ChatMeShapes.extraLarge,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = ChatMeColors.lightPrimary,
                unfocusedBorderColor = ChatMeColors.divider,
                focusedTextColor = ChatMeColors.lightOnSurface,
                unfocusedTextColor = ChatMeColors.lightOnSurface,
                containerColor = ChatMeColors.lightSurface
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            singleLine = false,
            maxLines = 3
        )

        Spacer(modifier = Modifier.width(8.dp))

        Button(
            onClick = onSend,
            modifier = Modifier
                .height(56.dp)
                .padding(vertical = 8.dp),
            shape = ChatMeShapes.medium,
            colors = ButtonDefaults.buttonColors(
                containerColor = ChatMeColors.lightPrimary,
                contentColor = ChatMeColors.lightOnPrimary,
                disabledContainerColor = ChatMeColors.lightPrimary.copy(alpha = 0.5f),
                disabledContentColor = ChatMeColors.lightOnPrimary.copy(alpha = 0.5f)
            ),
            enabled = message.isNotBlank() && !showSendingIndicator
        ) {
            if (showSendingIndicator) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = ChatMeColors.lightOnPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    Icons.Default.Send,
                    contentDescription = "Send",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun EmptySupportPlaceholder() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Chat with our support team",
            style = ChatMeTypography.titleMedium,
            color = ChatMeColors.lightOnSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Type your question or issue in the box below and our team will get back to you soon.",
            style = ChatMeTypography.bodyMedium,
            color = ChatMeColors.lightOnSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

private fun formatTimestamp(timestamp: Long): String {
    // Implement your timestamp formatting logic here
    return "Today, ${java.text.SimpleDateFormat("HH:mm").format(java.util.Date(timestamp))}"
}