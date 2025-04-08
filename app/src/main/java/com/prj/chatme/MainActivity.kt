package com.prj.chatme

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.messaging.FirebaseMessaging
import com.prj.chatme.help_and_support_screens.FAQsScreen
import com.prj.chatme.help_and_support_screens.SupportChatScreen
import com.prj.chatme.screens.ChatScreen
import com.prj.chatme.screens.LoginScreen
import com.prj.chatme.screens.SignUpScreen
import com.prj.chatme.screens.UserProfileScreen
import com.prj.chatme.ui.theme.ChatMeTheme
import com.prj.chatme.screens.ChatListScreen
import com.prj.chatme.screens.ForgotPasswordScreen
import com.prj.chatme.screens.SettingsScreen
import com.prj.chatme.screens.SingleStatusScreen
import com.prj.chatme.screens.SplashScreen
import com.prj.chatme.screens.StatusScreen
import com.prj.chatme.settings_screens.AboutScreen
import com.prj.chatme.settings_screens.EditProfileScreen
import com.prj.chatme.settings_screens.HelpAndSupportScreen
import com.prj.chatme.settings_screens.NotificationScreen
import com.prj.chatme.settings_screens.PrivacyScreen
import com.prj.chatme.ui.theme.ChatMeColors
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

sealed class DestinatinScreen(var route: String) {
    object ChatList : DestinatinScreen("chatList")
    object Chat : DestinatinScreen("chat/{chatId}") {
        fun createRoute(chatId: String) = "Chat/$chatId"
    }
    object Login : DestinatinScreen("login")
    object SignUp : DestinatinScreen("signUp")
    object UserProfile : DestinatinScreen("userProfile")
    object Splash : DestinatinScreen("splashScreen")
    object Status : DestinatinScreen("status")
    object SingleStatus : DestinatinScreen("singleStatus/{userId}"){
        fun createRoute(userId: String) = "SingleStatus/$userId"
    }
    object Settings : DestinatinScreen("settings")
    object About : DestinatinScreen("about")
    object Privacy : DestinatinScreen("privacy")
    object Notification : DestinatinScreen("notification"){}
    object EditProfile : DestinatinScreen("editProfile")
    object HelpAndSupport : DestinatinScreen("helpAndSupport")
    object SupportChat : DestinatinScreen("supportChat")
    object FAQs : DestinatinScreen("faqs")
    object ForgotPassword : DestinatinScreen("forgotPassword")

}
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NotificationPermissionHelper.checkAndRequestNotificationPermission(this)
        enableEdgeToEdge()

        setContent {
            ChatMeTheme {
                // Set status bar color to match theme
                val systemUiController = rememberSystemUiController()
                SideEffect {
                    systemUiController.setSystemBarsColor(
                        color = ChatMeColors.lightPrimaryContainer,
                        darkIcons = false
                    )
                }

                ChatAppNavigation()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        ViewModelProvider(this).get(CMViewModel::class.java).userIsOnline()
    }

    override fun onStop() {
        super.onStop()
        ViewModelProvider(this).get(CMViewModel::class.java).userIsOffline()
    }

    override fun onDestroy() {
        super.onDestroy()
        ViewModelProvider(this).get(CMViewModel::class.java).userIsOffline()
    }

    override fun onPause() {
        super.onPause()
        ViewModelProvider(this).get(CMViewModel::class.java).userIsOffline()
    }

    override fun onResume() {
        super.onResume()
        ViewModelProvider(this).get(CMViewModel::class.java).userIsOnline()
    }

    override fun onRestart() {
        super.onRestart()
        ViewModelProvider(this).get(CMViewModel::class.java).userIsOnline()
    }

    @Composable
    fun ChatAppNavigation() {
        val navController = rememberNavController()
        val vm = hiltViewModel<CMViewModel>()

        val startDestination = DestinatinScreen.Splash.route


        // Update FCM token on launch
        LaunchedEffect(Unit) {
            vm.updateFCMToken()

            intent?.getStringExtra("chatId")?.let {
                vm.chatIdToNavigate = it
            }
        }

        NavHost(
            navController = navController,
            startDestination = DestinatinScreen.Splash.route,
            modifier = Modifier.background(ChatMeColors.lightBackground)
        ) {
            composable(DestinatinScreen.Splash.route) {
                SplashScreen(navController, vm)
            }
            composable(DestinatinScreen.Login.route) {
                LoginScreen(navController, vm)
            }
            composable(DestinatinScreen.SignUp.route) {
                SignUpScreen(navController, vm)
            }
            composable(DestinatinScreen.ChatList.route) {
                ChatListScreen(navController, vm)
            }
            composable(DestinatinScreen.Chat.route) {
                val chatId = it.arguments?.getString("chatId")
                requireNotNull(chatId) { "chatId parameter must not be null" }
                ChatScreen(navController, vm, chatId)
            }
            composable(DestinatinScreen.UserProfile.route) {
                UserProfileScreen(navController, vm)
            }
            composable(DestinatinScreen.Status.route) {
                StatusScreen(navController, vm)
            }
            composable(DestinatinScreen.SingleStatus.route) {
                val userId = it.arguments?.getString("userId")
                requireNotNull(userId) { "userId parameter must not be null" }
                SingleStatusScreen(navController, vm, userId)
            }
            composable(DestinatinScreen.Settings.route) {
                SettingsScreen(navController, vm)
            }
            composable(DestinatinScreen.About.route) {
                AboutScreen(navController, vm)
            }
            composable(DestinatinScreen.Privacy.route) {
                PrivacyScreen(navController, vm)
            }
            composable(DestinatinScreen.Notification.route) {
                NotificationScreen(navController, vm)
            }
            composable(DestinatinScreen.EditProfile.route) {
                EditProfileScreen(navController, vm)
            }
            composable(DestinatinScreen.HelpAndSupport.route) {
                HelpAndSupportScreen(navController, vm)
            }
            composable(DestinatinScreen.SupportChat.route) {
                SupportChatScreen(navController, vm)
            }
            composable(DestinatinScreen.FAQs.route) {
                FAQsScreen(navController, vm)
            }
            composable(DestinatinScreen.ForgotPassword.route) {
                ForgotPasswordScreen(navController, vm)
            }
        }
    }
}