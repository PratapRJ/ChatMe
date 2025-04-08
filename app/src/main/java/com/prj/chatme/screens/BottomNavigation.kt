package com.prj.chatme.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.prj.chatme.DestinatinScreen
import com.prj.chatme.R
import com.prj.chatme.navigateTo
import com.prj.chatme.ui.theme.ChatMeColors
import com.prj.chatme.ui.theme.ChatMeTheme
import kotlinx.coroutines.launch

enum class BottomNavigationItem(val icon: Int, val navDestinationScreen: DestinatinScreen) {
    CHATLIST(R.drawable.chat_icon, DestinatinScreen.ChatList),
    STATUS(R.drawable.status_icon, DestinatinScreen.Status),
    PROFILE(R.drawable.profile_icon, DestinatinScreen.UserProfile)
}

@Composable
fun BottomNavigationMenu(
    selectedItem: BottomNavigationItem,
    navController: NavController
) {
    ChatMeTheme {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(top = 4.dp)
                .background(ChatMeColors.lightSurface)
        ) {
            for (item in BottomNavigationItem.values()) {

                Image(
                    painter = painterResource(id = item.icon),
                    contentDescription = "BottomNavigationIcon",
                    modifier = Modifier
                        .size(40.dp)
                        .padding(4.dp)
                        .weight(1f)
                        .clickable {
                            navigateTo(navController, item.navDestinationScreen.route)
                        },
                    colorFilter = if (item == selectedItem) {
                        ColorFilter.tint(ChatMeColors.lightPrimary)
                    } else {
                        ColorFilter.tint(ChatMeColors.lightOnSurface.copy(alpha = 0.6f))
                    }
                )
            }
        }
    }
}