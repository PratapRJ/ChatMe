package com.prj.chatme.screens

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.prj.chatme.CommonDivider
import com.prj.chatme.CommonImage
import com.prj.chatme.CommonProgressBar
import com.prj.chatme.DestinatinScreen
import com.prj.chatme.CMViewModel
import com.prj.chatme.navigateTo

@Composable
fun UserProfileScreen(navController: NavController, vm: CMViewModel) {
    val inProgress = vm.inProgress.value
    if (inProgress)
        CommonProgressBar()
    else {
        val userData = vm.userData
        var name by rememberSaveable {
            mutableStateOf(userData.value?.name?:"")
        }
        var number by rememberSaveable {
            mutableStateOf(userData.value?.number?:"")
        }

        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.wrapContentHeight().verticalScroll(rememberScrollState())) {
                ProfileContent(modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
                    .verticalScroll(
                        rememberScrollState()
                    ),
                    name = name,
                    number = number,
                    onNameChanged = {name = it},
                    onNumberChanged = {number = it },
                    vm = vm,
                    onBack = {
                        navController.popBackStack()
                    },
                    onSave = {
                        vm.createOrUpdateProfile(name=name,number=number)
                    },
                    onLogout = {
                        vm.userIsOffline()
                        vm.logout()
                        navigateTo(navController, DestinatinScreen.Login.route)
                    })
            }

            Column (modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter)){
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
    modifier: Modifier,
    name: String,
    number: String,
    onNameChanged: (String) -> Unit,
    onNumberChanged: (String) -> Unit,
    vm: CMViewModel,
    onBack: () -> Unit,
    onSave: () -> Unit,
    onLogout: () -> Unit
) {
    val imageUrl = vm.userData.value?.imageUrl
    Column(modifier = Modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Back", modifier = Modifier.clickable { onBack.invoke() })
            Text(text = "Save", modifier = Modifier.clickable { onSave.invoke() })
        }
        CommonDivider()
        ProfileImage(imageUrl = imageUrl, vm = vm)
        CommonDivider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Name: ",modifier = Modifier.width(100.dp))
            TextField(
                value = name, onValueChange = onNameChanged,
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    unfocusedTextColor = Color.Black

                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
        CommonDivider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Number: ",modifier = Modifier.width(100.dp))
            TextField(
                value = number, onValueChange = onNumberChanged,
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    unfocusedTextColor = Color.Black
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
        CommonDivider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "Logout", modifier = Modifier.clickable { onLogout.invoke() })
        }

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
    Box(modifier = Modifier.height(IntrinsicSize.Min)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable {
                    launcher.launch("image/*")
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                shape = CircleShape, modifier = Modifier
                    .padding(8.dp)
                    .size(100.dp)
            ) {
                CommonImage(data = imageUrl)

            }
            Text(text = "Change profile picture")

        }
        if (vm.inProgress.value) {
            CommonProgressBar()
        }

    }
}
