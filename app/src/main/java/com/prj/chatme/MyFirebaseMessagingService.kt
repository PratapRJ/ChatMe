package com.prj.chatme

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.prj.chatme.data.USER_NODE

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private fun getCurrentUserId(): String? {
        return try {
            Firebase.auth.currentUser?.uid
        } catch (e: Exception) {
            Log.e("FCM", "Auth not initialized", e)
            null
        }
    }
    override fun onNewToken(token: String) {
        getCurrentUserId()?.let { userId ->
            Firebase.firestore.collection(USER_NODE).document(userId)
                .update("fcmToken", token)
                .addOnFailureListener { e ->
                    Log.e("FCM", "Failed to update FCM token", e)
                }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        // 1. Handle notification when app is in foreground
        if (message.notification != null) {
            showNotification(
                title = message.notification?.title ?: "New message",
                body = message.notification?.body ?: ""
            )
        }

        // 2. Handle data payload (for when app is in background)
        message.data.let { data ->
            val chatId = data["chatId"]
            // Navigate to chat if needed
        }
    }

    private fun showNotification(title: String, body: String) {
        val channelId = "chatme_notifications"
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.chat_icon) // Create this icon
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Create channel (required for Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Chat Messages",
                NotificationManager.IMPORTANCE_HIGH
            )
            manager.createNotificationChannel(channel)
        }

        manager.notify(0, notificationBuilder.build())
    }
}