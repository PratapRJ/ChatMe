package com.prj.chatme.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColor
import androidx.core.graphics.toColorLong
import androidx.navigation.NavController
import com.prj.chatme.DestinatinScreen
import com.prj.chatme.R
import com.prj.chatme.navigateTo
import kotlinx.coroutines.launch

enum class BottomNavigationItem(val icon: Int, val navDestinationScreen: DestinatinScreen) {
    CHATLIST(R.drawable.chat_icon, DestinatinScreen.ChatList),
    PROFILE(R.drawable.profile_icon, DestinatinScreen.UserProfile)
}

@Composable
fun BottomNavigationMenu(
    selectedItem: BottomNavigationItem,
    navController: NavController
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 4.dp)
            .background(Color.White)
    ) {
        for (item in BottomNavigationItem.values()) {
            var scale = remember{ Animatable(if (item == selectedItem) 1.4f else 1f) }
            val coroutineScope = rememberCoroutineScope()
            Image(
                painter = painterResource(id = item.icon),
                contentDescription = "BottomNavigationIcon",
                modifier = Modifier
                    .scale(scale = scale.value)
                    .size(40.dp)
                    .padding(4.dp)
                    .weight(1f)
                    .clickable {

                        navigateTo(navController, item.navDestinationScreen.route)
                    },
                colorFilter = if (item == selectedItem) null
                else ColorFilter.tint(Color.Gray)
            )
        }


    }
}