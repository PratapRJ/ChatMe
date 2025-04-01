package com.prj.chatme

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
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
import com.prj.chatme.screens.ChatScreen
import com.prj.chatme.screens.LoginScreen
import com.prj.chatme.screens.SignUpScreen
import com.prj.chatme.screens.UserProfileScreen
import com.prj.chatme.ui.theme.ChatMeTheme
import com.prj.chatme.screens.ChatListScreen
import com.prj.chatme.screens.SplashScreen
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
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NotificationPermissionHelper.checkAndRequestNotificationPermission(this)
        enableEdgeToEdge()
        setContent {

            ChatMeTheme {
                HideSystemBars()
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
    fun HideSystemBars() {
        val systemUiController = rememberSystemUiController()

        LaunchedEffect(Unit) {
            // Hide both the status bar and navigation bar
            systemUiController.isStatusBarVisible = false
            systemUiController.isNavigationBarVisible = false

            // Ensure bars auto-hide if user swipes them up
            systemUiController.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

            while (true) {
                delay(3000) // Wait for 3 seconds
                systemUiController.isStatusBarVisible = false // Hide status bar again
                systemUiController.isNavigationBarVisible = false // Hide navigation bar again
            }
        }
    }





    @Composable
    fun ChatAppNavigation()
    {
        val navController = rememberNavController()
        var vm = hiltViewModel<CMViewModel>()
        vm.updateFCMToken()
        NavHost(navController = navController, startDestination = DestinatinScreen.Splash.route) {
            composable(DestinatinScreen.SignUp.route) {
                SignUpScreen(navController,vm)
            }
            composable(DestinatinScreen.Login.route) {
                LoginScreen(navController,vm)
            }
            composable(DestinatinScreen.ChatList.route) {
                ChatListScreen(navController,vm)
            }
            composable(DestinatinScreen.UserProfile.route) {
                UserProfileScreen(navController,vm)
            }
            composable(DestinatinScreen.Chat.route) {
                val chatId = it.arguments?.getString("chatId")
                chatId?.let {
                    ChatScreen(navController,vm,chatId)
                }
            }
            composable(DestinatinScreen.Splash.route) {
                SplashScreen(navController,vm)
            }

        }


    }

}


