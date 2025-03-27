package com.prj.chatme.data

data class UserData(
    var userId: String?="",
    var name: String?="",
    var number: String?="",
    var email: String?="",
    var online: Boolean=false,
    var lastSeen: String?=null,
    var imageUrl: String?="",
    var typing: Boolean = false,
    var fcmToken: String? = ""
){
    fun toMap() = mapOf(
        "userId" to userId,
        "name" to name,
        "number" to number,
        "email" to email,
        "online" to online,
        "lastSeen" to lastSeen,
        "imageUrl" to imageUrl,
        "typing" to typing
    )
}

data class ChatData(
    val chatId: String?="",
    val user1: ChatUser = ChatUser(),
    val user2: ChatUser = ChatUser()
)

data class ChatUser(
    val userId: String?="",
    val name: String?="",
    val imageUrl: String?="",
    val lastSeen: String?="",
    val online: Boolean=true,
    val number: String?="",
    val typing: Boolean=false
)

enum class MessageStatus { SENT, DELIVERED, READ }

data class Message(
    var sendBy: String? = "",
    val message: String? = "",
    val timestamp: String? = "",
    val userInChat: Boolean = false,
    val status: MessageStatus = MessageStatus.SENT // Default to SENT
)


data class Status(
    val user: ChatUser = ChatUser(),
    val imageUrl: String?="",
    val timestamp: Long ?=null

)