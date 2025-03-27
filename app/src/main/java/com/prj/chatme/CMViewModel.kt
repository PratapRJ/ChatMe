package com.prj.chatme

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
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
import org.json.JSONObject
import java.lang.Exception
import java.lang.reflect.Method
import java.util.Calendar
import java.util.UUID
import javax.inject.Inject

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
    private var user1LastSeen : String? = ""
    private var user2LastSeen : String? = ""
    private var isUser1Typing = mutableStateOf(false)
    private var isUser2Typing = mutableStateOf(false)


    init {
        val currentUser = auth.currentUser
        signInSuccess.value = currentUser != null
        currentUser?.uid?.let {
            getUserData(it)
        }

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
                    }.sortedBy { it.timestamp }
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





    fun onSendReply(chatId: String, message: String,userInChat: Boolean = false) {
        val time = Calendar.getInstance().time.toString()
        val msg = Message(
            userData.value?.userId,
            message,
            time,
            userInChat
        )
        db.collection(CHATS).document(chatId).collection(MESSAGES).document(time).set(msg)
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
        typing: Boolean = false
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
            typing = typing

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

                                    )
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
        val newStatus = Status(
            user = ChatUser(
                userId = userData.value?.userId,
                name = userData.value?.name,
                imageUrl = userData.value?.imageUrl,
                number = userData.value?.number
            ),
            imageUrl = imageUrl,
            timestamp = System.currentTimeMillis()
        )
        db.collection(STATUS).document().set(newStatus)
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
                db.collection(STATUS).whereGreaterThan("timestamp", cutOffTime).whereIn("user.userId", currentConnections)
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

    fun updateTypingStatus(typing: Boolean){
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
                chatRef.update("user1.online", isUser1Online.value, "user1.lastSeen", user1LastSeen,"user1.typing", isUser1Typing.value)
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
                chatRef.update("user2.online", isUser2Online.value, "user2.lastSeen", user2LastSeen,"user2.typing", isUser2Typing.value)
                inProgress.value = false
            }
        }

    }

    fun updateMessageStatus(chatId: String, messageId: String, status: MessageStatus) {
        db.collection(CHATS).document(chatId).collection(MESSAGES).document(messageId).update("status", status)
            .addOnFailureListener {
                handleException(it, "Failed to update message status")
            }
    }















}

