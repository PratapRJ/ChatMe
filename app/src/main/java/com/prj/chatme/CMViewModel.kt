package com.prj.chatme


import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.core.app.NotificationCompat
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.Request
import com.android.volley.toolbox.Volley

import com.google.android.gms.common.api.Response
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.prj.chatme.data.CHATS
import com.prj.chatme.data.ChatData
import com.prj.chatme.data.ChatUser
import com.prj.chatme.data.Event
import com.prj.chatme.data.MESSAGES
import com.prj.chatme.data.Message
import com.prj.chatme.data.MessageStatus
import com.prj.chatme.data.STATUS
import com.prj.chatme.data.Status
import com.prj.chatme.data.USER_NODE
import com.prj.chatme.data.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.lang.Exception
import java.lang.reflect.Method
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.prj.chatme.data.SupportMessage
import com.prj.chatme.help_and_support_screens.FAQ
import com.prj.chatme.settings_screens.HelpAndSupportScreen
import com.prj.chatme.ui.theme.ChatMeColors
import com.prj.chatme.ui.theme.ChatMeTheme

@HiltViewModel
class CMViewModel @Inject constructor(
    val auth: FirebaseAuth,
    val db: FirebaseFirestore,
    val storage: FirebaseStorage,
    val cloudMessaging: FirebaseMessaging
) : ViewModel() {

    var inProgress = mutableStateOf(false)
    val eventMutableState = mutableStateOf<Event<String>?>(null)
    var signInSuccess = mutableStateOf(false)
    var userData = mutableStateOf<UserData?>(null)
    var inProgressChats = mutableStateOf(false)
    val chats = mutableStateOf<List<ChatData>>(listOf())
    val chatMessages = mutableStateOf<List<Message>>(listOf())
    val inProgressChatsMessages = mutableStateOf(false)
    var currentChatMessageListener: ListenerRegistration? = null

    val status = mutableStateOf<List<Status>>(listOf())
    var inProgressStatus = mutableStateOf(false)

    //These are for updating online and lastSeen status
    private var isUser1Online = mutableStateOf(false)
    private var isUser2Online = mutableStateOf(false)
    private var user1LastSeen: String? = ""
    private var user2LastSeen: String? = ""
    private var isUser1Typing = mutableStateOf(false)
    private var isUser2Typing = mutableStateOf(false)

    private val _messageStatuses = MutableStateFlow<Map<String, String>>(emptyMap())
    val messageStatuses: StateFlow<Map<String, String>> = _messageStatuses

    var AllButtonClicked by  mutableStateOf(true)
    var ReadButtonClicked by  mutableStateOf(false)
    var UnreadButtonClicked by  mutableStateOf(false)

    var chatIdToNavigate by  mutableStateOf<String?>(null)

    var prevDate by mutableStateOf<String?>(null)


    init {
        val currentUser = auth.currentUser
        signInSuccess.value = currentUser != null
        currentUser?.uid?.let {
            getUserData(it)
        }

    }



    fun fetchMessageStatuses(chats: List<ChatData>) {
        viewModelScope.launch {
            val statuses = mutableMapOf<String, String>()
            chats.forEach { chat ->
                getLastMessageStatus(chat.chatId, chat.lastMessageId) { status ->
                    status?.let {
                        statuses[chat.chatId ?: ""] = it
                    } ?: run {
                        statuses[chat.chatId ?: ""] = "UNKNOWN"
                    }
                }
            }
            // Add delay to ensure all callbacks complete
            delay(500)
            _messageStatuses.value = statuses
        }
    }

    fun setFilter(filterType: FilterType) {
        when(filterType) {
            FilterType.ALL -> {
                AllButtonClicked = true
                ReadButtonClicked = false
                UnreadButtonClicked = false
            }
            FilterType.READ -> {
                AllButtonClicked = false
                ReadButtonClicked = true
                UnreadButtonClicked = false
            }
            FilterType.UNREAD -> {
                AllButtonClicked = false
                ReadButtonClicked = false
                UnreadButtonClicked = true
            }
        }
    }

    enum class FilterType {
        ALL, READ, UNREAD
    }

    fun login(email: String, password: String) {
        if (email.isEmpty() or password.isEmpty()) {
            handleException(customMessage = "Please fill all the fields")
            return
        } else {
            inProgress.value = true
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        signInSuccess.value = true
                        inProgress.value = false
                        auth.currentUser?.uid?.let {
                            getUserData(it)
                        }
                    } else {
                        handleException(it.exception, "Login Failed")
                    }
                }
        }

    }

    fun pupulateMessages(chatId: String) {
        inProgressChatsMessages.value = true
        currentChatMessageListener = db.collection(CHATS).document(chatId).collection(MESSAGES)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    handleException(error, "Can't Retrieve Messages")
                }
                if (value != null) {
                    chatMessages.value = value.documents.mapNotNull {
                        it.toObject<Message>()
                    }.sortedBy { it.index } // 👈 Sort messages by newest first
                    inProgressChatsMessages.value = false
                }
            }
    }


    fun dePopulateMessages() {
        chatMessages.value = listOf()
        currentChatMessageListener?.remove()
        currentChatMessageListener = null
    }

    fun pupulateChats() {
        inProgressChats.value = true
        db.collection(CHATS).where(
            Filter.or(
                Filter.equalTo("user1.userId", userData.value?.userId),
                Filter.equalTo("user2.userId", userData.value?.userId)
            )
        ).addSnapshotListener { value, error ->
            if (error != null) {
                handleException(error, "Can't Retrieve Chats")
            }
            if (value != null) {
                chats.value = value.documents.mapNotNull {
                    it.toObject<ChatData>()
                }
                inProgressChats.value = false
            }

        }
    }


    fun onSendReply(chatId: String, message: String, userInChat: Boolean = false) {
        inProgress.value = true
        if (message.isNotEmpty()) {

            val time = Calendar.getInstance().time.toString()

            // Fetch lastMessageIndex asynchronously
            db.collection(CHATS).document(chatId).get().addOnSuccessListener { document ->
                val lastMessageIndex = document.getLong("lastMessageIndex")?.toInt() ?: 0

                val msg = Message(
                    lastMessageIndex + 1,
                    userData.value?.userId,
                    message,
                    time,
                    userInChat
                )

                // Save message to Firestore
                db.collection(CHATS).document(chatId).collection(MESSAGES).document(time).set(msg)

                // Update last message details in CHATS collection
                db.collection(CHATS).document(chatId).update(
                    "lastMessage", message,
                    "lastMessageTime", formatedTime(time),
                    "lastMessageId", time,
                    "lastMessageIndex", msg.index
                )
                inProgress.value = false
            }.addOnFailureListener { error ->
                Log.e("Firestore", "Error fetching lastMessageIndex: ", error)
                inProgress.value = false
            }
        }
    }


    fun formatedTime(time: String): String {
        return try {
            val inputFormatter =
                DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)
            val outputFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH)
            val parsedDate = LocalDateTime.parse(time, inputFormatter)
            parsedDate.format(outputFormatter)
        } catch (e: Exception) {
            "Invalid Time"
        }
    }

    fun signUp(name: String, number: String, email: String, password: String) {
        inProgress.value = true
        if (name.isEmpty() or number.isEmpty() or email.isEmpty() or password.isEmpty()) {
            handleException(customMessage = "Please fill all the fields")
            return
        }
        db.collection(USER_NODE).whereEqualTo("number", number).get().addOnSuccessListener {
            if (it.isEmpty) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            signInSuccess.value = true
                            Log.d("TAG", "Success")
                            createOrUpdateProfile(name, number, email)
                            updateFCMToken()
                        } else {
                            Log.d("TAG", "Failed")
                            handleException(it.exception, "Sign Up Failed")
                        }
                    }
            } else {
                handleException(customMessage = "User already exists")
                inProgress.value = false


            }
        }


    }

    fun updateFCMToken() {
        cloudMessaging.token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("FCM", "Generated token: $token") // Add this for debugging

                userData.value?.userId?.let { userId ->
                    db.collection(USER_NODE)
                        .document(userId)
                        .update("fcmToken", token)
                        .addOnSuccessListener {
                            Log.d("FCM", "Token saved successfully")
                        }
                        .addOnFailureListener { e ->
                            Log.e("FCM", "Failed to save token", e)
                        }
                }
            } else {
                Log.e("FCM", "Token generation failed", task.exception)
            }
        }
    }

    fun uploadProfileImage(uri: Uri) {
        uploadImage(uri)
        {
            val imageUrl = it.toString()
            createOrUpdateProfile(imageUrl = imageUrl)
        }

    }

    fun uploadImage(uri: Uri, onSuccess: (Uri) -> Unit) {
        inProgress.value = true
        val storageRef = storage.reference
        val uuid = UUID.randomUUID()
        val imageRef = storageRef.child("images/$uuid")
        val uploadTask = imageRef.putFile(uri)
        uploadTask.addOnSuccessListener {
            val result = it.metadata?.reference?.downloadUrl
            result?.addOnSuccessListener(onSuccess)
            Log.e("TAG", "uploadImage: Success")
            inProgress.value = false
        }.addOnFailureListener {
            handleException(it, "Can't Upload Image")
        }

    }

    fun createOrUpdateProfile(
        name: String? = null,
        number: String? = null,
        email: String? = null,
        lastSeen: String? = null,
        online: Boolean = true,
        imageUrl: String? = null,
        typing: Boolean = false,
        bio: String? = null
    ) {
        var uid = auth.currentUser?.uid

        val userData = UserData(
            userId = uid,
            name = name ?: userData.value?.name,
            number = number ?: userData.value?.number,
            email = email ?: userData.value?.email,
            online = online,
            lastSeen = lastSeen ?: userData.value?.lastSeen,
            imageUrl = imageUrl ?: userData.value?.imageUrl,
            typing = typing,
            bio = bio ?: userData.value?.bio

        )
        uid?.let {
            inProgress.value = true
            db.collection(USER_NODE).document(uid).get().addOnSuccessListener {
                if (it.exists()) {
                    it.reference.update(userData.toMap())
                    inProgress.value = false
                    getUserData(uid)

                } else {
                    db.collection(USER_NODE).document(uid).set(userData)
                    inProgress.value = false
                    getUserData(uid)
                }
            }
                .addOnFailureListener {
                    handleException(it, "Can't Retrieve user")
                }
        }


    }

    private fun getUserData(uid: String) {
        inProgress.value = true
        db.collection(USER_NODE).document(uid).addSnapshotListener { value, error ->
            if (error != null)
                handleException(error, "Can't Retrieve user")
            if (value != null) {
                var user = value.toObject<UserData>()
                userData.value = user
                inProgress.value = false
                pupulateChats()
                populateStatuses()
            }
        }
    }

    fun handleException(exception: Exception? = null, customMessage: String? = null) {
        Log.e("LiveChatApp", "Exception: $exception")
        exception?.printStackTrace()
        val errorMessage = exception?.localizedMessage ?: "An unknown error occurred"
        val message = if (customMessage.isNullOrEmpty()) errorMessage else customMessage
        eventMutableState.value = Event(message)
        inProgress.value = false

    }

    fun logout() {
        auth.signOut()
        signInSuccess.value = false
        userData.value = null
        dePopulateMessages()
        currentChatMessageListener = null
        eventMutableState.value = Event("Logged Out")
    }

    fun onAddChat(number: String) {

        if (number.isEmpty() or !number.isDigitsOnly()) {
            handleException(customMessage = "Number must be contain digits only")
        } else {
            db.collection(CHATS).where(
                Filter.or(
                    Filter.and(
                        Filter.equalTo("user1.number", number),
                        Filter.equalTo("user2.number", userData.value?.number)
                    ), Filter.and(
                        Filter.equalTo("user1.number", userData.value?.number),
                        Filter.equalTo("user2.number", number)
                    )
                )
            ).get().addOnSuccessListener {
                if (it.isEmpty) {
                    db.collection(USER_NODE).whereEqualTo("number", number).get()
                        .addOnSuccessListener {
                            if (it.isEmpty) {
                                handleException(customMessage = "User not found")
                            } else {
                                val chatPartner = it.toObjects<UserData>()[0]
                                val id = db.collection(CHATS).document().id
                                val chat = ChatData(
                                    chatId = id,
                                    ChatUser(
                                        userId = userData.value?.userId,
                                        name = userData.value?.name,
                                        imageUrl = userData.value?.imageUrl,
                                        lastSeen = userData.value?.lastSeen,
                                        online = userData.value?.online ?: false,
                                        number = userData.value?.number,
                                        typing = userData.value?.typing ?: false
                                    ),
                                    ChatUser(
                                        userId = chatPartner.userId,
                                        name = chatPartner.name,
                                        imageUrl = chatPartner.imageUrl,
                                        lastSeen = chatPartner.lastSeen,
                                        online = chatPartner.online,
                                        number = chatPartner.number,
                                        typing = chatPartner.typing

                                    ),
                                    lastMessage = "",
                                    lastMessageTime = "",
                                    lastMessageId = "",
                                    lastMessageIndex = 0

                                    )
                                db.collection(CHATS).document(id).set(chat)
//                            eventMutableState.value = Event(
//                                "Chat Created"
//                            )
                            }
                        }
                        .addOnFailureListener {
                            handleException(it, "Can't create chat")
                        }
                } else {
                    handleException(customMessage = "Chat already exists")
                }
            }
        }

    }

    fun uploadStatus(uri: Uri) {
        uploadImage(uri) {
            createStatus(it.toString())
        }

    }

    fun createStatus(imageUrl: String) {
        val timestamp = System.currentTimeMillis()
        val newStatus = Status(
            user = ChatUser(
                userId = userData.value?.userId,
                name = userData.value?.name,
                imageUrl = userData.value?.imageUrl,
                number = userData.value?.number
            ),
            imageUrl = imageUrl,
            timestamp = timestamp
        )
        db.collection(STATUS).document(timestamp.toString()).set(newStatus)
    }

    fun populateStatuses() {
        val timeDelta = 24L * 60 * 60 * 1000
        val cutOffTime = System.currentTimeMillis() - timeDelta

        inProgressStatus.value = true
        db.collection(CHATS).where(
            Filter.or(
                Filter.equalTo("user1.userId", userData.value?.userId),
                Filter.equalTo("user2.userId", userData.value?.userId)
            )
        ).addSnapshotListener { value, error ->
            if (error != null) {
                handleException(error, "Can't Retrieve Chats")
            }
            if (value != null) {
                val currentConnections = arrayListOf(userData.value?.userId)
                val chats = value.toObjects<ChatData>()
                chats.forEach { chat ->
                    if (chat.user1.userId == userData.value?.userId)
                        currentConnections.add(chat.user2.userId!!)
                    else
                        currentConnections.add(chat.user1.userId!!)
                }
                db.collection(STATUS).whereGreaterThan("timestamp", cutOffTime)
                    .whereIn("user.userId", currentConnections)
                    .addSnapshotListener { value, error ->
                        if (error != null) {
                            handleException(error, "Can't Retrieve Status")
                        }
                        if (value != null) {
                            status.value = value.toObjects()
                            inProgressStatus.value = false
                        }
                    }
            }
        }
    }

    fun userIsOnline() {
        val uid = auth.currentUser?.uid ?: return
        db.collection(USER_NODE).document(uid).update("online", true)
            .addOnFailureListener { handleException(it, "Failed to update online status") }
    }

    fun updateTypingStatus(typing: Boolean) {
        val uid = auth.currentUser?.uid ?: return
        db.collection(USER_NODE).document(uid).update("typing", typing)
            .addOnFailureListener { handleException(it, "Failed to update online status") }

    }

    fun userIsOffline() {
        val uid = auth.currentUser?.uid ?: return
        val lastSeenTime = System.currentTimeMillis().toString() // Store last seen timestamp
        val updates = mapOf(
            "online" to false,
            "lastSeen" to lastSeenTime
        )

        db.collection(USER_NODE).document(uid).update(updates)
            .addOnFailureListener { handleException(it, "Failed to update offline status") }
    }

    fun updateChatUserStatus(chatId: String) {
        val user1Id = chats.value.first { it.chatId == chatId }.user1.userId
        val user2Id = chats.value.first { it.chatId == chatId }.user2.userId
        val chatRef = db.collection(CHATS).document(chatId)

        inProgress.value = true
        db.collection(USER_NODE).document(user1Id.toString()).addSnapshotListener { value, error ->
            if (error != null)
                handleException(error, "Can't Retrieve user")
            if (value != null) {
                var user = value.toObject<UserData>()
                isUser1Online.value = user?.online ?: false
                user1LastSeen = user?.lastSeen
                isUser1Typing.value = user?.typing ?: false
                chatRef.update(
                    "user1.online",
                    isUser1Online.value,
                    "user1.lastSeen",
                    user1LastSeen,
                    "user1.typing",
                    isUser1Typing.value
                )
                inProgress.value = false
            }
        }
        inProgress.value = true
        db.collection(USER_NODE).document(user2Id.toString()).addSnapshotListener { value, error ->
            if (error != null)
                handleException(error, "Can't Retrieve user")
            if (value != null) {
                var user = value.toObject<UserData>()
                isUser2Online.value = user?.online ?: false
                user2LastSeen = user?.lastSeen
                isUser2Typing.value = user?.typing ?: false
                chatRef.update(
                    "user2.online",
                    isUser2Online.value,
                    "user2.lastSeen",
                    user2LastSeen,
                    "user2.typing",
                    isUser2Typing.value
                )
                inProgress.value = false
            }
        }

    }

    fun updateMessageStatus(chatId: String, messageId: String, status: MessageStatus) {
        db.collection(CHATS).document(chatId).collection(MESSAGES).document(messageId)
            .update("status", status)
            .addOnFailureListener {
                handleException(it, "Failed to update message status")
            }
    }

    fun getLastMessageStatus(chatId: String?, messageId: String?, callback: (String?) -> Unit) {
        if (chatId.isNullOrEmpty() || messageId.isNullOrEmpty()) {
            callback(null) // Prevent crash if chatId or messageId is invalid
            return
        }

        db.collection(CHATS)
            .document(chatId)
            .collection(MESSAGES)
            .document(messageId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val status = document.getString("status") // Fetch 'status' field
                    callback(status)
                } else {
                    callback(null) // No document found
                }
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
                callback(null) // Return null in case of error
            }
    }

    fun deleteMsg(chatId: String, messageId: String) {
        val messageRef = db.collection(CHATS).document(chatId).collection(MESSAGES).document(messageId)

        messageRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val messageText = document.getString("message")
                    if (messageText == "Message Deleted") {
                        // If already marked as deleted, remove it from Firestore
                        messageRef.delete()
                    } else {
                        // Otherwise, update the message content to "Message Deleted"
                        messageRef.update("message", "Message Deleted")
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error deleting message", e)
            }
    }


    fun deleteChat(chatId: String) {
        db.collection(CHATS).document(chatId).delete()
    }

    fun deleteStatus(statusId: String) {
        db.collection(STATUS).document(statusId).delete()

    }

    //Notification Settings Methods Starts:

    fun getCurrentNotificationSettings(context: Context): NotificationSettings {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager
            val channel = manager.getNotificationChannel("chatme_messages")

            return NotificationSettings(
                enabled = channel?.importance != NotificationManager.IMPORTANCE_NONE,
                showPreview = channel?.lockscreenVisibility == Notification.VISIBILITY_PUBLIC,
                soundEnabled = channel?.sound != null,
                vibrationEnabled = channel?.shouldVibrate() == true
            )
        }
        return NotificationSettings() // Default values
    }

    data class NotificationSettings(
        val enabled: Boolean = true,
        val showPreview: Boolean = true,
        val soundEnabled: Boolean = true,
        val vibrationEnabled: Boolean = false
    )

    fun updateNotificationSettings(
        context: Context,
        notificationsEnabled: Boolean,
        showPreview: Boolean,
        soundEnabled: Boolean,
        vibrationEnabled: Boolean
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager

            val channelId = "chatme_messages"
            val channelName = "Chat Messages"
            val importance = if (notificationsEnabled) {
                NotificationManager.IMPORTANCE_HIGH
            } else {
                NotificationManager.IMPORTANCE_HIGH
            }

            val channel = NotificationChannel(
                channelId,
                channelName,
                importance
            ).apply {
                setShowBadge(true)
                lockscreenVisibility = if (showPreview) {
                    Notification.VISIBILITY_PUBLIC  // Correct constant here
                } else {
                    Notification.VISIBILITY_PRIVATE
                }

                if (soundEnabled) {
                    setSound(Settings.System.DEFAULT_NOTIFICATION_URI, null)
                } else {
                    setSound(null, null)
                }

                if (vibrationEnabled) {
                    enableVibration(true)
                    vibrationPattern = longArrayOf(0, 100, 200, 300)
                } else {
                    enableVibration(false)
                }
            }

            notificationManager.createNotificationChannel(channel)
        }
    }


    fun openEmailClient(email: String,context: Context) {
        try {

            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                putExtra(Intent.EXTRA_SUBJECT, "Regarding ChatMe App")
            }
            context.startActivity(Intent.createChooser(intent, "Send email using..."))
        } catch (e: ActivityNotFoundException) {
            // Handle case where no email client is installed
            Toast.makeText(
                context,
                "No email client installed",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    //Here Help and Support Screen Methods :
    private val _faqs = mutableStateListOf<FAQ>()
    val faqs: List<FAQ> = _faqs

    val supportMessages: MutableState<List<SupportMessage>> = mutableStateOf(emptyList())

    fun fetchFAQs() {
        viewModelScope.launch {
            // Call your API or database to get FAQs
            // Then update _faqs
        }
    }

    fun sendSupportMessage(messageText: String, function: () -> Unit) {

    }

    //Forgot Password Method Here
    // In your CMViewModel class
    val passwordResetSuccess = mutableStateOf(false)
    val errorMessage = mutableStateOf("")

    fun sendPasswordResetEmail(email: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->

                        passwordResetSuccess.value = true

                        if (!task.isSuccessful) {
                            errorMessage.value = task.exception?.message ?: "Unknown error occurred"

                        }
                        onComplete()
                    }
            } catch (e: Exception) {
                errorMessage.value = e.message ?: "Unknown error occurred"
                passwordResetSuccess.value = false
                onComplete()
            }
        }
    }


}

