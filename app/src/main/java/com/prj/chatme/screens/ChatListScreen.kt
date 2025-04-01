package com.prj.chatme.screens

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.prj.chatme.CommonProgressBar
import com.prj.chatme.CommonRow
import com.prj.chatme.DestinatinScreen
import com.prj.chatme.CMViewModel
import com.prj.chatme.CommonAlertDialog
import com.prj.chatme.R
import com.prj.chatme.TextFieldWithIcons
import com.prj.chatme.TitleText
import com.prj.chatme.UserInfoRow
import com.prj.chatme.navigateTo
import com.prj.chatme.ui.theme.DarkGreen
import com.prj.chatme.ui.theme.DarkOrange
import com.prj.chatme.ui.theme.LightOrange
import com.prj.chatme.ui.theme.chatListBackgroundColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ChatListScreen(
    navController: NavController,
    vm: CMViewModel
) {
    val inProgress = vm.inProgressChats.value
    if (inProgress) {
        CommonProgressBar()
    } else {
        val chats = vm.chats.value
        val userData = vm.userData.value
        val showDialog = remember {
            mutableStateOf(false)
        }
        val onFabClicked: () -> Unit = { showDialog.value = true }
        val onDismiss: () -> Unit = { showDialog.value = false }
        val onAddChat: (String) -> Unit = {
            showDialog.value = false
            vm.onAddChat(it)
        }


        var searchValue by rememberSaveable {
            mutableStateOf("")
        }

        // 🔹 Filtered chats based on search query
        val filteredChats by remember {
            derivedStateOf {
                if (searchValue.isBlank()) chats // Show all chats if search is empty
                else chats.filter { chat ->
                    val chatUser =
                        if (chat.user1.userId == userData?.userId) chat.user2 else chat.user1
                    chatUser.name!!.contains(
                        searchValue,
                        ignoreCase = true
                    ) // Case-insensitive search
                }
            }
        }



        Scaffold(
            floatingActionButton = {
                FAB(
                    showDialog = showDialog.value,
                    onFabClicked = onFabClicked,
                    onDismiss = onDismiss,
                    onAddChat = onAddChat
                )
            },
            content = {
                Column(
                    modifier = Modifier
                        .padding(it)
                        .background(chatListBackgroundColor)
                        .fillMaxSize()
                ) {

                    TitleText(text = "Chats", color = DarkOrange)

                    Spacer(modifier = Modifier.height(8.dp))


                    if (chats.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = "No Chats Available")
                        }
                    } else {
                        LaunchedEffect(Unit) {
                            vm.fetchMessageStatuses(chats)
                        }

                        LazyColumn(
                            modifier = Modifier.weight(1f)
                        ) {
                            // ✅ Make the button row scrollable inside LazyColumn
                            item {
                                TextFieldWithIcons(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentHeight()

                                        .padding(start = 16.dp, end = 16.dp)
                                        .background(
                                            Color.White,
                                            shape = RoundedCornerShape(80.dp)
                                        ) // Apply white background and rounded corners
                                        .clip(RoundedCornerShape(80.dp)), // Clip to match the shape,
                                    icon = Icons.Default.Search,
                                    value = searchValue,
                                    label = "",
                                    placeholder = "Search",
                                    contentDescription = "Search",
                                    containerColor = Color.Transparent,
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedBorderColor = Color.Transparent
                                ) {
                                    searchValue = it
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                            if (filteredChats.isEmpty()) {
                                item {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(text = "No Chats Available")
                                    }
                                }
                            } else {
                                item {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .wrapContentHeight()
                                            .background(Color.Transparent),
                                    ) {
                                        Button(
                                            onClick = {
                                                vm.AllButtonClicked = true
                                                vm.ReadButtonClicked = false
                                                vm.UnreadButtonClicked = false
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (vm.AllButtonClicked) DarkOrange else Color.Transparent,
                                                contentColor = if (vm.AllButtonClicked) Color.White else Color.Black
                                            ),
                                            border = BorderStroke(2.dp, DarkOrange),
                                            modifier = Modifier
                                                .padding(start = 16.dp)
                                                .height(35.dp)
                                                .align(Alignment.CenterVertically)
                                        ) {
                                            Text(text = "All")
                                        }

                                        Button(
                                            onClick = {
                                                vm.AllButtonClicked = false
                                                vm.ReadButtonClicked = true
                                                vm.UnreadButtonClicked = false
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (vm.ReadButtonClicked) DarkOrange else Color.Transparent,
                                                contentColor = if (vm.ReadButtonClicked) Color.White else Color.Black
                                            ),
                                            border = BorderStroke(2.dp, DarkOrange),
                                            modifier = Modifier
                                                .padding(start = 8.dp)
                                                .height(35.dp)
                                                .align(Alignment.CenterVertically)
                                        ) {
                                            Text(text = "Read")
                                        }

                                        Button(
                                            onClick = {
                                                vm.AllButtonClicked = false
                                                vm.ReadButtonClicked = false
                                                vm.UnreadButtonClicked = true
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (vm.UnreadButtonClicked) DarkOrange else Color.Transparent,
                                                contentColor = if (vm.UnreadButtonClicked) Color.White else Color.Black
                                            ),
                                            border = BorderStroke(2.dp, DarkOrange),
                                            modifier = Modifier
                                                .padding(start = 8.dp)
                                                .height(35.dp)
                                                .align(Alignment.CenterVertically)
                                        ) {
                                            Text(text = "Unread")
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(20.dp)) // Add spacing below buttons
                                }

                                items(filteredChats, key = { it.chatId.toString() }) { chat ->
                                    val chatUser =
                                        if (chat.user1.userId == userData?.userId) chat.user2 else chat.user1
                                    val status =
                                        vm.messageStatuses.collectAsState().value[chat.chatId]
                                            ?: "UNKNOWN"

                                    val addOrNot = when {
                                        vm.ReadButtonClicked -> status == "READ"
                                        vm.UnreadButtonClicked -> status == "SENT"
                                        else -> true
                                    }

                                    val showDialog = remember { mutableStateOf(false) }

                                    if (addOrNot) {
                                        UserInfoRow(
                                            imageUrl = chatUser.imageUrl,
                                            name = chatUser.name.toString(),
                                            lastMessage = chat.lastMessage.toString(),
                                            lastMessageTime = chat.lastMessageTime.toString(),
                                            onLongPress = {
                                                showDialog.value = true // ✅ Now it works!
                                            },
                                            onItemClick =
                                            {
                                                chat.chatId?.let {
                                                    navigateTo(
                                                        navController,
                                                        DestinatinScreen.Chat.createRoute(chatId = it)
                                                    )
                                                }
                                            })


                                        // ✅ Show delete dialog properly
                                        if (showDialog.value) {
                                            CommonAlertDialog(
                                                message = "Do you want to delete this chat?",
                                                showDialog = showDialog,
                                                onSuccess = {
                                                    showDialog.value = false
                                                    vm.deleteChat(chat.chatId.toString())

                                                },
                                                onDismiss = {
                                                    showDialog.value = false
                                                }
                                            )
                                        }
                                    }
                                }

                            }
                        }


                    }
                    BottomNavigationMenu(BottomNavigationItem.CHATLIST, navController)
                }
            })
    }


}

@Composable
fun FAB(
    showDialog: Boolean,
    onFabClicked: () -> Unit,
    onDismiss: () -> Unit,
    onAddChat: (String) -> Unit
) {
    val addChatNumber = remember {
        mutableStateOf("")
    }
    val onNumberChanged: (String) -> Unit = {
        addChatNumber.value = it
    }
    if (showDialog) {
        AlertDialog(onDismissRequest = {
            onDismiss.invoke()
        },
            confirmButton = {
                Button(
                    onClick = {
                        onAddChat(addChatNumber.value)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkOrange,
                        contentColor = Color.White,
                        disabledContainerColor = Color.Gray,
                        disabledContentColor = Color.White
                    )
                ) {
                    Text(text = "Add Chat")
                }
            },
            title = { Text(text = "Add Chat") },
            text = {
                TextFieldWithIcons(
                    icon = Icons.Default.Call,
                    label = "Phone Number",
                    value = addChatNumber.value,
                    placeholder = "Enter your phone number",
                    contentDescription = "Phone Number",
                    onValueChange = { onNumberChanged(it) }
                )
            }

        )
    }
    FloatingActionButton(
        onClick = { onFabClicked.invoke() },
        containerColor = DarkOrange,
        shape = CircleShape,
        modifier = Modifier.padding(bottom = 40.dp),

        ) {
        Icon(
            imageVector = Icons.Rounded.Add,
            contentDescription = "Add Chat",
            tint = Color.White
        )
    }


}