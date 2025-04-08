package com.prj.chatme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.prj.chatme.DestinatinScreen
import com.prj.chatme.CMViewModel
import com.prj.chatme.R
import com.prj.chatme.data.ChatData
import com.prj.chatme.data.ChatUser
import com.prj.chatme.data.UserData
import com.prj.chatme.navigateTo
import com.prj.chatme.ui.theme.ChatMeColors
import com.prj.chatme.ui.theme.ChatMeShapes
import com.prj.chatme.ui.theme.ChatMeTheme
import com.prj.chatme.ui.theme.ChatMeTypography
import kotlinx.coroutines.delay

@Composable
fun ChatListScreen(
    navController: NavController,
    vm: CMViewModel
) {
    ChatMeTheme {
        val inProgress = vm.inProgressChats.value
        val chats = vm.chats.value
        val userData = vm.userData.value

        if (inProgress) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = ChatMeColors.lightPrimary)
            }
        } else {
            var searchValue by rememberSaveable { mutableStateOf("") }
            var showAddChatDialog by rememberSaveable { mutableStateOf(false) }

            val filteredChats = remember(searchValue, chats) {
                if (searchValue.isBlank()) chats else {
                    chats.filter { chat ->
                        val chatUser = if (chat.user1.userId == userData?.userId) chat.user2 else chat.user1
                        chatUser.name?.contains(searchValue, ignoreCase = true) ?: false
                    }
                }
            }

            Scaffold(
                topBar = { ChatListTopBar(navController) },
                floatingActionButton = {
                    AddChatFAB(
                        onClick = { showAddChatDialog = true }
                    )
                },
                content = { padding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .background(ChatMeColors.lightBackground)
                    ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            // Search Bar
                            SearchBar(
                                value = searchValue,
                                onValueChange = { searchValue = it },
                                modifier = Modifier.padding(16.dp)
                            )

                            // Filter Chips
                            FilterChipsRow(
                                vm = vm,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )

                            // Chat List
                            when {
                                chats.isEmpty() -> EmptyChatListPlaceholder()
                                filteredChats.isEmpty() -> NoResultsPlaceholder()
                                else -> ChatList(
                                    chats = filteredChats,
                                    userData = userData,
                                    vm = vm,
                                    navController = navController,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        // Add Chat Dialog
                        if (showAddChatDialog) {
                            AddChatDialog(
                                onDismiss = { showAddChatDialog = false },
                                onAddChat = { number ->
                                    showAddChatDialog = false
                                    vm.onAddChat(number)
                                }
                            )
                        }
                    }
                },
                bottomBar = {
                    BottomNavigationMenu(
                        selectedItem = BottomNavigationItem.CHATLIST,
                        navController = navController
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatListTopBar(navController: NavController) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Chats",
                style = ChatMeTypography.headlineSmall,
                color = ChatMeColors.darkPrimaryContainer,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(onClick = { /* Handle menu */ }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = ChatMeColors.darkPrimaryContainer
                )
            }
        },
        actions = {
            IconButton(
                onClick = { navigateTo(navController, DestinatinScreen.Settings.route) }
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings"
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = ChatMeColors.lightPrimary
            )
        },
        placeholder = {
            Text(
                text = "Search chats...",
                color = ChatMeColors.lightOnSurface.copy(alpha = 0.6f)
            )
        },
        singleLine = true,
        shape = ChatMeShapes.medium,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedTextColor = ChatMeColors.lightOnSurface,
            unfocusedTextColor = ChatMeColors.lightOnSurface,
            focusedPlaceholderColor = ChatMeColors.lightOnSurface.copy(alpha = 0.6f),
            unfocusedPlaceholderColor = ChatMeColors.lightOnSurface.copy(alpha = 0.6f),
            focusedLeadingIconColor = ChatMeColors.lightPrimary,
            unfocusedLeadingIconColor = ChatMeColors.lightPrimary,
            focusedBorderColor = ChatMeColors.lightPrimary,
            unfocusedBorderColor = ChatMeColors.divider,
        ),
        modifier = modifier.fillMaxWidth()
    )
}
@Composable
private fun FilterChipsRow(
    vm: CMViewModel,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        FilterChip(
            selected = vm.AllButtonClicked,
            onClick = { vm.setFilter(CMViewModel.FilterType.ALL) },
            label = {
                Text(
                    text = "All",
                    style = ChatMeTypography.labelLarge
                )
            },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = ChatMeColors.lightPrimaryContainer,
                selectedLabelColor = ChatMeColors.darkPrimaryContainer,
                containerColor = ChatMeColors.lightSurface,
                labelColor = ChatMeColors.lightOnSurface
            ),
            modifier = Modifier.padding(end = 8.dp)
        )

        FilterChip(
            selected = vm.ReadButtonClicked,
            onClick = { vm.setFilter(CMViewModel.FilterType.READ) },
            label = {
                Text(
                    text = "Read",
                    style = ChatMeTypography.labelLarge
                )
            },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = ChatMeColors.lightPrimaryContainer,
                selectedLabelColor = ChatMeColors.darkPrimaryContainer,
                containerColor = ChatMeColors.lightSurface,
                labelColor = ChatMeColors.lightOnSurface
            ),
            modifier = Modifier.padding(end = 8.dp)
        )

        FilterChip(
            selected = vm.UnreadButtonClicked,
            onClick = { vm.setFilter(CMViewModel.FilterType.UNREAD) },
            label = {
                Text(
                    text = "Unread",
                    style = ChatMeTypography.labelLarge
                )
            },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = ChatMeColors.lightPrimaryContainer,
                selectedLabelColor = ChatMeColors.darkPrimaryContainer,
                containerColor = ChatMeColors.lightSurface,
                labelColor = ChatMeColors.lightOnSurface
            )
        )
    }
}

@Composable
private fun ChatList(
    chats: List<ChatData>,
    userData: UserData?,
    vm: CMViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    // Collect the message statuses as state
    val messageStatuses by vm.messageStatuses.collectAsState()

    // Fetch statuses when chats change
    LaunchedEffect(chats) {
        vm.fetchMessageStatuses(chats)
    }

    // Filter chats based on selected filter
    val filteredChats = remember(chats, vm.AllButtonClicked, vm.ReadButtonClicked, vm.UnreadButtonClicked, messageStatuses) {
        when {
            vm.AllButtonClicked -> chats
            vm.ReadButtonClicked -> chats.filter { chat ->
                messageStatuses[chat.chatId ?: ""] == "READ"
            }
            vm.UnreadButtonClicked -> chats.filter { chat ->
                messageStatuses[chat.chatId ?: ""] == "SENT"
            }
            else -> chats
        }
    }

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(filteredChats, key = { it.chatId.toString() }) { chat ->
            val chatUser = if (chat.user1.userId == userData?.userId) chat.user2 else chat.user1
            val status = messageStatuses[chat.chatId ?: ""] ?: "UNKNOWN"

            var showDeleteDialog by remember { mutableStateOf(false) }

            ChatListItem(
                chat = chat,
                chatUser = chatUser,
                status = status,
                onItemClick = {
                    chat.chatId?.let {
                        navigateTo(navController, DestinatinScreen.Chat.createRoute(it))
                    }
                },
                onLongPress = { showDeleteDialog = true }
            )

            if (showDeleteDialog) {
                DeleteChatDialog(
                    onDismiss = { showDeleteDialog = false },
                    onConfirm = {
                        showDeleteDialog = false
                        vm.deleteChat(chat.chatId.toString())
                    }
                )
            }
        }
    }
}
@Composable
private fun ChatListItem(
    chat: ChatData,
    chatUser: ChatUser,
    status: String,
    onItemClick: () -> Unit,
    onLongPress: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { onLongPress() },
                    onTap = { onItemClick() }
                )
            },
        shape = ChatMeShapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = ChatMeColors.lightSurface,
            contentColor = ChatMeColors.lightOnSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Image
            AsyncImage(
                model = chatUser.imageUrl,
                contentDescription = "Profile image",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.user),
                error = painterResource(R.drawable.user)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Chat Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = chatUser.name ?: "Unknown",
                    style = ChatMeTypography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = ChatMeColors.lightOnSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = chat.lastMessage ?: "",
                    style = ChatMeTypography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = ChatMeColors.lightOnSurface.copy(alpha = 0.8f)
                )
            }

            // Time and Status
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = chat.lastMessageTime ?: "",
                    style = ChatMeTypography.labelSmall,
                    color = ChatMeColors.lightOnSurface.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(
                            color = when (status) {
                                "READ" -> ChatMeColors.success
                                "SENT" -> ChatMeColors.divider
                                else -> Color.Transparent
                            },
                            shape = CircleShape
                        )
                )
            }
        }
    }
}

@Composable
private fun AddChatFAB(
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = ChatMeColors.lightPrimary,
        contentColor = ChatMeColors.lightOnPrimary,
        shape = CircleShape,
        modifier = Modifier.padding(bottom = 72.dp)
    ) {
        Icon(
            imageVector = Icons.Rounded.Add,
            contentDescription = "Add chat"
        )
    }
}

@Composable
private fun AddChatDialog(
    onDismiss: () -> Unit,
    onAddChat: (String) -> Unit
) {
    var phoneNumber by rememberSaveable { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add New Chat",
                style = ChatMeTypography.titleLarge,
                color = ChatMeColors.lightOnSurface
            )
        },
        text = {
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Phone Number") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Phone",
                        tint = ChatMeColors.lightPrimary
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = ChatMeColors.lightSurface,
                    unfocusedContainerColor = ChatMeColors.lightSurface,
                    focusedTextColor = ChatMeColors.lightOnSurface,
                    unfocusedTextColor = ChatMeColors.lightOnSurface,
                ),
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onAddChat(phoneNumber) },
                enabled = phoneNumber.isNotBlank(),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = ChatMeColors.lightPrimary
                )
            ) {
                Text("Add")
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
        titleContentColor = ChatMeColors.lightOnSurface,
        textContentColor = ChatMeColors.lightOnSurface
    )
}

@Composable
private fun DeleteChatDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = ChatMeColors.darkRed
            )
        },
        title = {
            Text(
                text = "Delete Chat",
                style = ChatMeTypography.titleLarge,
                color = ChatMeColors.lightOnSurface
            )
        },
        text = {
            Text(
                text = "Are you sure you want to delete this chat?",
                color = ChatMeColors.lightOnSurface.copy(alpha = 0.8f)
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = ChatMeColors.darkRed
                )
            ) {
                Text("Delete")
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
        titleContentColor = ChatMeColors.lightOnSurface,
        textContentColor = ChatMeColors.lightOnSurface
    )
}

@Composable
private fun EmptyChatListPlaceholder() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_empty_chat),
                contentDescription = "No chats",
                modifier = Modifier.size(64.dp),
                tint = ChatMeColors.lightPrimary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No chats yet",
                style = ChatMeTypography.titleMedium,
                color = ChatMeColors.lightOnSurface
            )
            Text(
                text = "Tap the + button to start a new chat",
                style = ChatMeTypography.bodyMedium,
                color = ChatMeColors.lightOnSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun NoResultsPlaceholder() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.chat),
                contentDescription = "No results",
                modifier = Modifier.size(64.dp),
                tint = ChatMeColors.lightPrimary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No matching chats found",
                style = ChatMeTypography.titleMedium,
                color = ChatMeColors.lightOnSurface
            )
        }
    }
}