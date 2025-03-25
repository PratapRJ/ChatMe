package com.prj.chatme.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.prj.chatme.CommonDivider
import com.prj.chatme.CommonImage
import com.prj.chatme.CMViewModel
import com.prj.chatme.data.Message

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prj.chatme.data.ChatUser
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import androidx.compose.material.icons.rounded.Done
import com.prj.chatme.data.MessageStatus
import com.prj.chatme.data.UserData

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun ChatScreen(navController: NavController, vm: CMViewModel, chatId: String) {
    var reply by rememberSaveable {
        mutableStateOf("")
    }
    val myUser = vm.userData.value
    val currentChat = vm.chats.value.first { it.chatId == chatId }
    val chatUser =
        if (currentChat.user1.userId == myUser?.userId) currentChat.user2 else currentChat.user1
    val onSendReply = {
        vm.onSendReply(chatId, reply, chatUser.online)
        reply = ""
    }
    val chatMessage = vm.chatMessages.value

    LaunchedEffect(key1 = Unit) {
        vm.pupulateMessages(chatId)
        vm.updateChatUserStatus(chatId)
    }
    BackHandler {
        vm.dePopulateMessages()
        navController.popBackStack()
    }


    Column {
        ChatHeader(
            name = chatUser.name ?: "",
            imageUrl = chatUser.imageUrl ?: "",
            chatUser.online,
            chatUser.lastSeen.toString()
        ) {
            navController.popBackStack()
            vm.dePopulateMessages()

        }

        Column(Modifier.weight(1f)) {
            MessageBox(
                chatMessages = chatMessage,
                chatUser = chatUser,
                currentUserId = myUser?.userId ?: "",
                vm = vm,
                chatId = chatId
            )
            if (chatUser.typing) {
                Text(
                    text = "Typing...",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .padding(start = 16.dp, top = 4.dp)
                        .align(alignment = Alignment.Start)
                )
            }
        }



        ReplyBox(
            reply = reply,
            onReplyChange = { reply = it },
            onSendReply = onSendReply,
            vm = vm,
            chatId = chatId
        )
    }

}




@Composable
fun MessageBox(
    modifier: Modifier = Modifier,
    chatMessages: List<Message>,
    chatUser: ChatUser,
    vm: CMViewModel,
    chatId: String,
    currentUserId: String
) {
    val listState = rememberLazyListState()

    // Scroll to the bottom when chatMessages change
    LaunchedEffect(chatMessages) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    LazyColumn(
        modifier = modifier,
        state = listState
    ) {
        items(chatMessages) { msg ->
            LaunchedEffect(msg) {
                if (msg.sendBy != currentUserId && msg.status == MessageStatus.SENT) {
                    vm.updateMessageStatus(chatId, msg.timestamp ?: "", MessageStatus.READ)
                }
            }

            val alignment = if (msg.sendBy != currentUserId) Alignment.Start else Alignment.End
            val color = if (msg.sendBy == currentUserId) Color(0xFF68C400) else Color(0xFFC0C0C0)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = alignment
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(color)
                        .padding(start = 8.dp, top = 4.dp, end = 4.dp, bottom = 4.dp)
                ) {
                    Text(
                        text = msg.message ?: "",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(top = 4.dp, start = 4.dp, bottom = 20.dp, end = 10.dp)
                    )

                    // Convert timestamp to 12-hour format
                    val formattedTime = try {
                        val inputFormatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)
                        val outputFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH)
                        val parsedDate = LocalDateTime.parse(msg.timestamp, inputFormatter)
                        parsedDate.format(outputFormatter)
                    } catch (e: Exception) {
                        "Invalid Time"
                    }

                    Text(
                        text = formattedTime,
                        color = Color.White,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .padding(start = 4.dp, end = 4.dp)
                            .align(Alignment.Bottom)
                    )

                    if (msg.sendBy == currentUserId) {
                        val tickIcon = when (msg.status) {
                            MessageStatus.SENT -> Icons.Rounded.Done
                            MessageStatus.DELIVERED -> Icons.Rounded.Done
                            MessageStatus.READ -> Icons.Rounded.Done
                        }
                        val tickColor = when (msg.status) {
                            MessageStatus.READ -> Color.Blue
                            MessageStatus.DELIVERED -> Color.Gray
                            else -> Color.Red
                        }

                        Icon(
                            imageVector = tickIcon,
                            contentDescription = "Message Status",
                            tint = tickColor,
                            modifier = Modifier
                                .size(18.dp)
                                .padding(start = 4.dp)
                                .align(Alignment.Bottom)
                        )
                    }
                }
            }
        }
    }
}




//@Composable
//fun MessageBox(
//    modifier: Modifier = Modifier,
//    chatMessages: List<Message>,
//    chatUser: ChatUser,
//    currentUserId: String
//) {
//    LazyColumn(modifier = modifier) {
//        items(chatMessages) { msg ->
//            val alignment = if (msg.sendBy != currentUserId) Alignment.Start else Alignment.End
//            val color = if (msg.sendBy == currentUserId) Color(0xFF68C400) else Color(0xFFC0C0C0)
//
//
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(8.dp),
//                horizontalAlignment = alignment
//            ) {
//
//                Text(
//                    text = msg.message.toString(),
//                    color = Color.Black,
//                    modifier = Modifier
//                        .clip(RoundedCornerShape(8.dp))
//                        .background(color)
//                        .padding(12.dp),
//                    fontWeight = FontWeight.Bold
//                )
////                Text(
////                    text = msg.timestamp.toString().substring(11, 16),
////                    color = Color.Gray,
////                    modifier = Modifier
////                        .background(color)
////                        .padding(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 4.dp)
////                )
//            }
//
//        }
//
//
//    }
//}


@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun ReplyBox(
    reply: String,
    onReplyChange: (String) -> Unit,
    vm: CMViewModel,
    chatId: String,
    onSendReply: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()

    val bringIntoViewRequester =
        remember { androidx.compose.foundation.relocation.BringIntoViewRequester() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
    ) {
        CommonDivider()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BasicTextField(
                value = reply,
                onValueChange = {
                    onReplyChange(it)

                    // Set typing = true when user types something, false when empty
                    vm.updateTypingStatus(it.isNotEmpty())
                    vm.updateChatUserStatus(chatId = chatId)
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
                    .onFocusChanged { focusState ->
                        coroutineScope.launch {
                            bringIntoViewRequester.bringIntoView()
                        }
                        if (!focusState.isFocused) {
                            vm.updateTypingStatus(false)
                            vm.updateChatUserStatus(chatId = chatId)
                        }
                    },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(
                    onSend = {
                        onSendReply()
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    }
                )
            )

            Button(onClick = {
                onSendReply()
                keyboardController?.hide()
                focusManager.clearFocus()
            }) {
                Text(text = "Send")
            }
        }
    }
}


@Composable
fun ChatHeader(
    name: String,
    imageUrl: String,
    isUserOnline: Boolean,
    lastSeen: String,

    onBackClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back", modifier = Modifier
            .padding(8.dp)
            .clickable {
                onBackClicked()
            })
        CommonImage(
            data = imageUrl,
            modifier = Modifier
                .padding(8.dp)
                .clip(CircleShape)
                .size(50.dp)
        )
        Column {
            Text(
                text = name, fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 4.dp)
            )
            Text(
                text = if (isUserOnline) "Online" else (
                        if (lastSeen.isNullOrEmpty() || lastSeen == "null") "Offline"
                        else "Last seen at " + SimpleDateFormat(
                            "dd/MM/yyyy hh:mm a",
                            Locale.getDefault()
                        )
                            .format(Date(lastSeen.toLong()))
                        ),
                fontWeight = FontWeight.Light, fontSize = 12.sp, color = Color.Gray
            )
        }
    }

}