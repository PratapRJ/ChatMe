package com.prj.chatme.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.prj.chatme.CommonProgressBar
import com.prj.chatme.CommonRow
import com.prj.chatme.DestinatinScreen
import com.prj.chatme.CMViewModel
import com.prj.chatme.TitleText
import com.prj.chatme.navigateTo

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
                        .fillMaxSize()
                ) {
                    TitleText(text = "Chats")
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
                        LazyColumn(modifier = Modifier.weight(1f)) {
                            items(chats) { chat ->
                                val chatUser =
                                    if (chat.user1.userId == userData?.userId) chat.user2 else chat.user1
                                CommonRow(
                                    imageUrl = chatUser.imageUrl.toString(),
                                    name = chatUser.name.toString()
                                ) {
                                    chat.chatId?.let {

                                        navigateTo(
                                            navController,
                                            DestinatinScreen.Chat.createRoute(chatId = it)
                                        )
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
    if (showDialog) {
        AlertDialog(onDismissRequest = {
            onDismiss.invoke()
        },
            confirmButton = {
                Button(onClick = {
                    onAddChat(addChatNumber.value)
                }) {
                    Text(text = "Add Chat")
                }
            },
            title = { Text(text = "Add Chat") },
            text = {
                OutlinedTextField(
                    value = addChatNumber.value, onValueChange = { addChatNumber.value = it }, label = {Text(text = "Enter Number")},
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

        )
    }
    FloatingActionButton(
        onClick = { onFabClicked.invoke() },
        containerColor = MaterialTheme.colorScheme.secondary,
        shape = CircleShape,
        modifier = Modifier.padding(bottom = 40.dp)
    ) {
        Icon(
            imageVector = Icons.Rounded.Add,
            contentDescription = "Add Chat",
            tint = Color.White
        )
    }


}