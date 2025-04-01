package com.prj.chatme.screens

import android.widget.ImageButton
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Animatable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.icons.filled.Send
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import com.prj.chatme.CommonAlertDialog
import com.prj.chatme.R
import com.prj.chatme.data.MessageStatus
import com.prj.chatme.data.UserData
import com.prj.chatme.ui.theme.DarkGreen
import com.prj.chatme.ui.theme.DarkOrange
import com.prj.chatme.ui.theme.Green
import com.prj.chatme.ui.theme.sendMessageBgColor
import java.time.LocalDate
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.ui.draw.scale
import kotlinx.coroutines.delay

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
    val focusManager = LocalFocusManager.current

    LaunchedEffect(key1 = Unit) {
        vm.pupulateMessages(chatId)
        vm.updateChatUserStatus(chatId)
    }
    BackHandler {
        vm.dePopulateMessages()
        navController.popBackStack()
    }


    Column(
        modifier = Modifier.clickable {
            focusManager.clearFocus()
        }
    ) {
        ChatHeader(
            name = chatUser.name ?: "",
            imageUrl = chatUser.imageUrl ?: "",
            chatUser.online,
            chatUser.lastSeen.toString()
        ) {
            navController.popBackStack()
            vm.dePopulateMessages()

        }

        Box(Modifier.weight(1f)) {
            Image(
                painter = painterResource(id = R.drawable.chat_wallpaper),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )
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
                        .align(Alignment.BottomStart)
                )
            }
            ReplyBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .align(Alignment.BottomCenter),
                reply = reply,
                onReplyChange = { reply = it },
                onSendReply = onSendReply,
                vm = vm,
                chatId = chatId,
                focusManager = focusManager
            )
        }


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
    var showDialog = rememberSaveable {
        mutableStateOf(false)
    }

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
        var prevDate: String = ""
        val formatter = DateTimeFormatter.ofPattern("EEE MMM dd", Locale.ENGLISH)

        val today = LocalDate.now()
        val yesterday = today.minusDays(1)
        var dateMsgStamp = ""
        items(chatMessages) { msg ->
            LaunchedEffect(msg) {
                if (msg.sendBy != currentUserId && msg.status == MessageStatus.SENT) {
                    vm.updateMessageStatus(chatId, msg.timestamp ?: "", MessageStatus.READ)
                }
            }


            val alignment = if (msg.sendBy != currentUserId) Alignment.Start else Alignment.End
            val color = if (msg.sendBy == currentUserId) sendMessageBgColor else Color.White


                dateMsgStamp = if (msg.timestamp.toString().substring(0, 10) == yesterday.format(
                        formatter
                    )
                ) "Yesterday" else if (msg.timestamp.toString()
                        .substring(0, 10) == today.format(formatter)
                ) "Today" else msg.timestamp.toString().substring(0, 10)


            if(prevDate!=msg.timestamp.toString().substring(0, 10)) {
                Row (modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center) {
                    Text(
                        text = "  $dateMsgStamp  ",
                        color = Color.Gray,
                        modifier = Modifier
                            .padding(top = 8.dp, bottom = 8.dp)
                            .background(Color.White, shape = RoundedCornerShape(80.dp))
                            .clip(RoundedCornerShape(20.dp))
                    )
                }
                prevDate = msg.timestamp.toString().substring(0, 10)
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = {
                                showDialog.value = true
                            }
                        )
                    },
                horizontalAlignment = alignment
            ) {
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if(msg.message=="Message Deleted") Color.Gray else color)
                        .padding(start = 8.dp, top = 4.dp, end = 4.dp, bottom = 4.dp)
                ) {
                    Text(
                        text = msg.message ?: "",
                        color = if(msg.message=="Message Deleted") Color.White else Color.Black,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(top = 4.dp, start = 4.dp, bottom = 5.dp, end = 10.dp)
                            .widthIn(min = 0.dp, max = 300.dp) // Max width 300.dp, wrap if smaller
                            .wrapContentWidth()
                    )

                    // Convert timestamp to 12-hour format
                    val formattedTime = try {
                        val inputFormatter = DateTimeFormatter.ofPattern(
                            "EEE MMM dd HH:mm:ss z yyyy",
                            Locale.ENGLISH
                        )
                        val outputFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH)
                        val parsedDate = LocalDateTime.parse(msg.timestamp, inputFormatter)
                        parsedDate.format(outputFormatter)
                    } catch (e: Exception) {
                        "Invalid Time"
                    }
                    if(msg.message!="Message Deleted") {
                        Row(modifier = Modifier.align(Alignment.End)) {
                            Text(
                                text = formattedTime,
                                color = Color.Gray,
                                fontSize = 12.sp,
                                modifier = Modifier
                                    .padding(start = 4.dp, end = 4.dp)
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
                                )
                            }
                        }
                    }
                }
            }
            if (showDialog.value) {
                CommonAlertDialog(
                    message = "Do you still want to delete this message?",
                    showDialog,
                    onSuccess = {
                        vm.deleteMsg(chatId,msg.timestamp.toString())
                        showDialog.value = false
                    },
                    onDismiss = {
                        showDialog.value = false
                    }
                )
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


@OptIn(
    ExperimentalComposeUiApi::class,
    ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun ReplyBox(
    modifier: Modifier = Modifier,
    reply: String,
    onReplyChange: (String) -> Unit,
    vm: CMViewModel,
    chatId: String,
    onSendReply: () -> Unit,
    focusManager: FocusManager
) {

    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()

    val bringIntoViewRequester =
        remember { androidx.compose.foundation.relocation.BringIntoViewRequester() }

    // Count lines entered
    val lineCount = reply.count { it == '\n' } + 2
    val maxVisibleLines = 5 // Maximum height for 5 lines

    // Adjust height dynamically
    val textFieldHeight = (lineCount * 25).coerceAtMost(maxVisibleLines * 25)

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedTextField(
                value = reply,
                onValueChange = {
                    onReplyChange(it)
                    vm.updateTypingStatus(it.isNotEmpty())
                    vm.updateChatUserStatus(chatId = chatId)
                },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .align(Alignment.CenterVertically)
                    .height(textFieldHeight.dp) // 🔥 Dynamic height
                    .padding(start = 8.dp, end = 16.dp)
                    .onFocusChanged { focusState ->
                        coroutineScope.launch {
                            bringIntoViewRequester.bringIntoView()
                        }
                        if (!focusState.isFocused) {
                            vm.updateTypingStatus(false)
                            vm.updateChatUserStatus(chatId = chatId)
                        }
                    }
                    .background(Color.White, shape = RoundedCornerShape(80.dp))
                    .clip(RoundedCornerShape(20.dp)),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Default,  // 🔹 Allow Enter key
                    keyboardType = KeyboardType.Text
                ),
                keyboardActions = KeyboardActions(
                    onSend = {
                        onSendReply()
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    }
                ),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    containerColor = Color.White
                )
            )


            Card(
                shape = CircleShape,
                modifier = Modifier
                    .size(50.dp)
                    .clickable {
                        onSendReply()
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    }
                    .align(Alignment.Bottom),
                colors = CardDefaults.cardColors(
                    containerColor = Green,
                    contentColor = Color.White,
                    disabledContainerColor = Color.Gray,
                    disabledContentColor = Color.White
                )
            ) {
                Icon(
                    Icons.Default.Send,
                    contentDescription = "Send",
                    Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))

        }
        Spacer(modifier = Modifier.height(12.dp))
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