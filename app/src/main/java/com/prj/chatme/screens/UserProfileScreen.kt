package com.prj.chatme.screens

import android.util.Log
import com.prj.chatme.ui.theme.DarkGreen
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.prj.chatme.CommonDivider
import com.prj.chatme.CommonImage
import com.prj.chatme.CommonProgressBar
import com.prj.chatme.DestinatinScreen
import com.prj.chatme.CMViewModel
import com.prj.chatme.TextFieldWithIcons
import com.prj.chatme.navigateTo
import com.prj.chatme.ui.theme.DarkOrange
import com.prj.chatme.ui.theme.DarkRed

@Composable
fun UserProfileScreen(navController: NavController, vm: CMViewModel) {
    val inProgress = vm.inProgress.value
    if (inProgress)
        CommonProgressBar()
    else {
        val userData = vm.userData
        var name by rememberSaveable {
            mutableStateOf(userData.value?.name ?: "")
        }
        var number by rememberSaveable {
            mutableStateOf(userData.value?.number ?: "")
        }
        var email by rememberSaveable {
            mutableStateOf(userData.value?.email ?: "")
        }
        var showDialog = rememberSaveable {
            mutableStateOf(false)
        }
        val focusManager = LocalFocusManager.current

        Box(modifier = Modifier
            .fillMaxSize()
            .clickable {
                focusManager.clearFocus()
            }) {
            Column(
                modifier = Modifier
                    .wrapContentHeight()
                    .verticalScroll(rememberScrollState())
            ) {
                ProfileContent(modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
                    .verticalScroll(
                        rememberScrollState()
                    ),
                    name = name,
                    number = number,
                    email = email,
                    onNameChanged = { name = it },
                    onNumberChanged = { number = it },
                    onEmailChanged = { email = it },
                    vm = vm,
                    onBack = {
                        navController.popBackStack()
                    },
                    onSave = {
                        vm.createOrUpdateProfile(name = name, number = number)
                    },
                    showDialog = showDialog,
                    onDismiss = { showDialog.value = false },
                    onLogout = {
                        vm.userIsOffline()
                        vm.logout()
                        navigateTo(navController, DestinatinScreen.Login.route)
                    })
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) {
                BottomNavigationMenu(
                    selectedItem = BottomNavigationItem.PROFILE,
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun ProfileContent(
    modifier: Modifier = Modifier,
    name: String,
    number: String,
    email: String,
    onNameChanged: (String) -> Unit,
    onNumberChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    showDialog: MutableState<Boolean>,
    onDismiss: () -> Unit,
    vm: CMViewModel,
    onBack: () -> Unit,
    onSave: () -> Unit,
    onLogout: () -> Unit
) {
    // var showDialog by remember { mutableStateOf(false) } // Manage dialog visibility state

    val imageUrl = vm.userData.value?.imageUrl

    Column(

        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(DarkOrange),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Back", modifier = Modifier.clickable { onBack.invoke() }.padding(8.dp))
            Text(text = "Save", modifier = Modifier.clickable { onSave.invoke() }.padding(8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))
        ProfileImage(imageUrl = imageUrl, vm = vm)
        Spacer(modifier = Modifier.height(32.dp))

        TextFieldWithIcons(
            icon = Icons.Default.Face,
            label = "Name",
            value = name,
            placeholder = "Enter your name",
            contentDescription = "Name",
            onValueChange = { onNameChanged(it) }
        )
        Spacer(modifier = Modifier.height(16.dp))

        TextFieldWithIcons(
            icon = Icons.Default.Call,
            label = "Phone Number",
            value = number,
            placeholder = "Enter your phone number",
            contentDescription = "Phone Number",
            onValueChange = { onNumberChanged(it) }
        )
        Spacer(modifier = Modifier.height(16.dp))

        TextFieldWithIcons(
            icon = Icons.Default.Email,
            label = "Email",
            value = email,
            placeholder = "Enter your email",
            contentDescription = "Email",
            onValueChange = { onEmailChanged(it) }
        )
        Spacer(modifier = Modifier.height(32.dp))

        // Logout Button
        Button(
            onClick = { showDialog.value = true }, // Show the dialog
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Red,
                contentColor = Color.White,
                disabledContainerColor = Color.Gray,
                disabledContentColor = Color.White
            )
        ) {
            Text(text = "Logout", modifier = Modifier.padding(8.dp), color = Color.White)
        }
    }

    // Show the Logout Confirmation Dialog
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false }, // Close dialog on dismiss
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
                        text = "Do You Still Want to Logout?",
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
                        showDialog.value = false // Close the dialog
                        onLogout.invoke() // Perform logout
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkRed,
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
                    onClick = { showDialog.value = false },
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
}


@Composable
fun ProfileImage(imageUrl: String?, vm: CMViewModel) {
    Log.d("ProfileImage", "Image URL: $imageUrl") // Log the image URL

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri ->
        uri?.let {
            vm.uploadProfileImage(it)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally) // Centers Box content
            .height(IntrinsicSize.Min),
        contentAlignment = Alignment.Center // Ensures content inside Box is centered
    ) {
        Column(
            modifier = Modifier
                .clickable {
                    launcher.launch("image/*")
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                shape = CircleShape,
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.CenterHorizontally) // Ensures Card is centered
            ) {
                CommonImage(
                    data = imageUrl,
                    modifier = Modifier.fillMaxSize(), // Ensure Image takes full space in Card
                    contentScale = ContentScale.Crop
                )
            }
            Text(text = "Change profile picture", modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        if (vm.inProgress.value) {
            CommonProgressBar()
        }
    }
}
