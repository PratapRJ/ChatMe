package com.prj.chatme

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

object NotificationPermissionHelper {
    const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1001
    private const val PREFS_NAME = "notification_prefs"
    private const val KEY_FIRST_LAUNCH = "first_launch"

    fun checkAndRequestNotificationPermission(activity: Activity) {
        val sharedPrefs = activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // Check if it's first launch
        if (sharedPrefs.getBoolean(KEY_FIRST_LAUNCH, true)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val permission = Manifest.permission.POST_NOTIFICATIONS

                if (activity.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    // Show explanation dialog first
                    showPermissionExplanationDialog(activity) {
                        // Request permission after explanation
                        activity.requestPermissions(
                            arrayOf(permission),
                            NOTIFICATION_PERMISSION_REQUEST_CODE
                        )
                    }
                }

                // Mark as not first launch anymore
                sharedPrefs.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply()
            }
        }
    }

    private fun showPermissionExplanationDialog(
        activity: Activity,
        onContinue: () -> Unit
    ) {
        AlertDialog.Builder(activity)
            .setTitle("Enable Notifications")
            .setMessage("Allow notifications to receive new messages and updates")
            .setPositiveButton("Continue") { _, _ -> onContinue() }
            .setNegativeButton("Later", null)
            .show()
    }
}