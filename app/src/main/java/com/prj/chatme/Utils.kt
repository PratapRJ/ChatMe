package com.prj.chatme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.prj.chatme.ui.theme.ChatMeColors
import com.prj.chatme.ui.theme.DarkGreen
import com.prj.chatme.ui.theme.DarkOrange
import java.text.SimpleDateFormat
import java.util.*

import android.content.Context
import android.widget.Toast


fun navigateTo(navController: NavController, route: String) {

    navController.navigate(route) {
        popUpTo(route) {
            inclusive = true
        }
        launchSingleTop = true
    }
}

@Composable
fun CommonProgressBar() {
    Row(
        modifier = Modifier
            .alpha(0.5f)
            .background(Color.LightGray)
            .clickable(enabled = false) {}
            .fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun CommonDivider() {
    Divider(
        color = Color.LightGray,
        thickness = 1.dp,
        modifier = Modifier
            .alpha(3.3f)
            .padding(top = 8.dp, bottom = 8.dp)
    )
}

@Composable
fun CommonImage(
    data: String?,
    modifier: Modifier = Modifier.wrapContentSize(),
    contentScale: ContentScale = ContentScale.Crop
) {
    val painter = if (data.isNullOrEmpty()) {
        painterResource(R.drawable.user) // Use painterResource for local drawables
    } else {
        rememberImagePainter(data) // Load from a URL
    }

    Image(
        painter = painter,
        contentDescription = null,
        modifier = modifier,
        contentScale = contentScale
    )
}


@Composable
fun CheckSignedIn(navController: NavController, vm: CMViewModel) {
    val alreadySignIn = remember { mutableStateOf(false) }
    val signIn = vm.signInSuccess.value
    if (signIn && !alreadySignIn.value) {
        alreadySignIn.value = true
        navController.navigate(DestinatinScreen.ChatList.route)
        {
            popUpTo(0)
        }

    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextFieldWithIcons(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    value: String,
    placeholder: String,
    contentDescription: String,
    containerColor: Color = Color.White,
    focusedBorderColor: Color = DarkOrange,
    unfocusedBorderColor: Color = Color.Gray,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = { onValueChange(it) },
        leadingIcon = {
            Icon(imageVector = icon, contentDescription = contentDescription, tint = Color.Black)
        },
        trailingIcon = {
            if (value.isNotEmpty()) {
                IconButton(onClick = { onValueChange("") }) {
                    Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear text")
                }
            }
        },
        label = if(label.isNullOrEmpty()) null else { { Text(text = label) } },
        placeholder = { Text(text = placeholder) },
        singleLine = true,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            containerColor = containerColor,
            focusedBorderColor = focusedBorderColor,
            unfocusedBorderColor = unfocusedBorderColor
    ))
}


@Composable
fun TitleText(text: String,color:Color) {
    Text(
        text = text,
        fontWeight = FontWeight.Bold,
        fontSize = 35.sp,
        color = color,
        modifier = Modifier.padding(start = 16.dp,top = 8.dp,bottom = 8.dp,end = 16.dp)
    )
}

@Composable
fun CommonRow(
    imageUrl: String,
    name: String,
    onItemClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(75.dp)
            .clickable { onItemClick.invoke() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        CommonImage(
            data = imageUrl, modifier = Modifier
                .padding(8.dp)
                .size(50.dp)
                .clip(CircleShape)
                .background(Color.Red)
        )
        Text(
            text = name?: "No Name",
            fontSize = 18.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start =4.dp)
        )
    }

}

@Composable
fun UserInfoRow(
    imageUrl: String?,
    name: String,
    lastMessage : String,
    lastMessageTime: String,
    onLongPress: () -> Unit,
    onItemClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(75.dp)
            .pointerInput(Unit){
                detectTapGestures(onLongPress = {
                    onLongPress.invoke()

                },
                    onTap = {
                        onItemClick.invoke()
                    })
            },
            //.clickable { onItemClick.invoke() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row {
            CommonImage(
                data = imageUrl, modifier = Modifier
                    .padding(start = 8.dp,end = 8.dp)
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Color.Red)
                    .align(Alignment.CenterVertically)
            )
            Column {
                Text(
                    text = name ?: "No Name",
                    fontSize = 18.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp)
                )
                Text(
                    text = lastMessage ?: "No Message Found",
                    fontSize = 15.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(start = 4.dp)
                )

            }
        }
        Text(
            text = lastMessageTime ?: "",
            fontSize = 12.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.padding(top = 4.dp, end = 12.dp)
                .align( Alignment.Top)
        )
    }


}

@Composable
fun CommonAlertDialog(
    message:String,
    onSuccess: () -> Unit,
    onDismiss: () -> Unit
){
    AlertDialog(
        onDismissRequest = { onDismiss.invoke() }, // Close dialog on dismiss
        title = {
            Column {
                Text(
                    text = "Think Again...",
                    modifier = Modifier.padding(8.dp),
                    fontWeight = FontWeight.Bold,
                    color = Color.Red,
                    fontSize = 27.sp
                )
                Text(
                    text = message,
                    modifier = Modifier.padding(8.dp),
                    fontWeight = FontWeight.Light,
                    color = Color.Red,
                    fontSize = 18.sp
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSuccess.invoke()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = ChatMeColors.darkRed,
                    contentColor = Color.White,
                    disabledContainerColor = Color.Gray,
                    disabledContentColor = Color.White
                )
            ) {
                Text(text = "Just, Do it!")
            }
        },
        dismissButton = {
            Button(
                onClick = { onDismiss.invoke() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = DarkGreen,
                    contentColor = Color.White,
                    disabledContainerColor = Color.Gray,
                    disabledContentColor = Color.White
                )
            ) {
                Text(text = "No, Leave it!")
            }
        }
    )
}


fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}