package com.prj.chatme

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
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
        // Handle foreground notification
        if (message.notification != null) {
            val title = message.notification?.title ?: "New message"
            val body = message.notification?.body ?: ""
            val chatId = message.data["chatId"] ?: ""

            showNotification(
                title = title,
                body = body,
                chatId = chatId
            )
        }
    }

    private fun showNotification(title: String, body: String, chatId: String) {
        val channelId = "chatme_notifications"

        // Create intent to open MainActivity and pass chatId
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("chatId", chatId)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_round) // Replace with your actual icon
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel for Android 8.0+
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
