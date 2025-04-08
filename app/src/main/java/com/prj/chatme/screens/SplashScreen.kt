package com.prj.chatme.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.prj.chatme.CMViewModel
import com.prj.chatme.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(navController: NavController, vm: CMViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        val scale = remember { Animatable(0f) }

        LaunchedEffect(Unit) {
            scale.animateTo(1f, animationSpec = tween(2000))
            delay(2100)

            // Clear the entire back stack including splash screen
            navController.navigate(
                if (vm.signInSuccess.value) {
                    if (vm.chatIdToNavigate != null) {
                        "Chat/${vm.chatIdToNavigate}".also { vm.chatIdToNavigate = null }
                    } else {
                        "chatList"
                    }
                } else {
                    "login"
                }
            ) {
                // This clears the entire back stack up to and including the splash screen
                popUpTo(0) { inclusive = true }
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(R.drawable.ic_launcher_round),
                contentDescription = "Splash Screen Image",
                modifier = Modifier.scale(scale.value)
            )
            Text(
                text = "ChatMe",
                fontSize = 30.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.scale(scale.value)
            )
        }
    }
}