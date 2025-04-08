package com.prj.chatme.settings_screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.prj.chatme.CMViewModel
import com.prj.chatme.CommonImage
import com.prj.chatme.R
import com.prj.chatme.ui.theme.ChatMeColors
import com.prj.chatme.ui.theme.ChatMeShapes
import com.prj.chatme.ui.theme.ChatMeTheme
import com.prj.chatme.ui.theme.ChatMeTypography

@Composable
fun EditProfileScreen(navController: NavController, vm: CMViewModel) {
    ChatMeTheme {
        val inProgress = vm.inProgress.value
        val userData by vm.userData
        var name by rememberSaveable { mutableStateOf(userData?.name ?: "") }
        var bio by rememberSaveable { mutableStateOf(userData?.bio ?: "") }
        var phone by rememberSaveable { mutableStateOf(userData?.number ?: "") }
        val focusManager = LocalFocusManager.current

        LaunchedEffect(userData) {
            userData?.let {
                name = it.name ?: ""
                bio = it.bio ?: ""
                phone = it.number ?: ""
            }
        }

        Scaffold(
            topBar = {
                EditProfileTopBar(
                    onBack = { navController.popBackStack() },
                    onSave = { vm.createOrUpdateProfile(name = name, bio = bio, number = phone) }
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(ChatMeColors.lightBackground)
                    .clickable { focusManager.clearFocus() }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Profile Picture Section
                    ProfilePictureSection(
                        imageUrl = userData?.imageUrl,
                        vm = vm,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    // Profile Form Section
                    ProfileFormSection(
                        name = name,
                        bio = bio,
                        phone = phone,
                        onNameChange = { name = it },
                        onBioChange = { bio = it },
                        onPhoneChange = { phone = it },
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    // Save Button
                    Button(
                        onClick = {
                            vm.createOrUpdateProfile(name = name, bio = bio, number = phone)
                            navController.popBackStack()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ChatMeColors.lightPrimary,
                            contentColor = ChatMeColors.lightOnPrimary
                        ),
                        shape = ChatMeShapes.medium
                    ) {
                        Text(
                            text = "Save Changes",
                            style = ChatMeTypography.labelLarge,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }

                if (inProgress) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(ChatMeColors.darkOverlay),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = ChatMeColors.lightPrimary)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditProfileTopBar(onBack: () -> Unit, onSave: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Edit Profile",
                style = ChatMeTypography.titleLarge,
                color = ChatMeColors.darkPrimaryContainer
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = ChatMeColors.darkPrimaryContainer
                )
            }
        },
        actions = {
            TextButton(onClick = onSave) {
                Text(
                    text = "Save",
                    style = ChatMeTypography.bodyLarge,
                    color = ChatMeColors.darkPrimaryContainer
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = ChatMeColors.lightPrimaryContainer,
            titleContentColor = ChatMeColors.darkPrimaryContainer,
            actionIconContentColor = ChatMeColors.darkPrimaryContainer
        )
    )
}

@Composable
private fun ProfilePictureSection(
    imageUrl: String?,
    vm: CMViewModel,
    modifier: Modifier = Modifier
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { vm.uploadProfileImage(it) }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            Card(
                shape = CircleShape,
                modifier = Modifier.size(120.dp)
            ) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Profile picture",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.user),
                    error = painterResource(R.drawable.user)
                )
            }

            IconButton(
                onClick = { launcher.launch("image/*") },
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = ChatMeColors.lightPrimary,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit profile picture",
                    tint = ChatMeColors.lightOnPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Change Photo",
            style = ChatMeTypography.labelMedium,
            color = ChatMeColors.lightPrimary
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileFormSection(
    name: String,
    bio: String,
    phone: String,
    onNameChange: (String) -> Unit,
    onBioChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = ChatMeColors.lightPrimary,
                unfocusedBorderColor = ChatMeColors.divider,
                focusedTextColor = ChatMeColors.lightOnSurface,
                unfocusedTextColor = ChatMeColors.lightOnSurface,
                focusedLabelColor = ChatMeColors.lightPrimary,
                unfocusedLabelColor = ChatMeColors.lightOnSurface.copy(alpha = 0.6f),
                containerColor = ChatMeColors.lightSurface
            ),
            shape = ChatMeShapes.medium
        )

        OutlinedTextField(
            value = bio,
            onValueChange = onBioChange,
            label = { Text("Bio") },
            maxLines = 3,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = ChatMeColors.lightPrimary,
                unfocusedBorderColor = ChatMeColors.divider,
                focusedTextColor = ChatMeColors.lightOnSurface,
                unfocusedTextColor = ChatMeColors.lightOnSurface,
                focusedLabelColor = ChatMeColors.lightPrimary,
                unfocusedLabelColor = ChatMeColors.lightOnSurface.copy(alpha = 0.6f),
                containerColor = ChatMeColors.lightSurface
            ),
            shape = ChatMeShapes.medium
        )

        OutlinedTextField(
            value = phone,
            onValueChange = onPhoneChange,
            label = { Text("Phone Number") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = ChatMeColors.lightPrimary,
                unfocusedBorderColor = ChatMeColors.divider,
                focusedTextColor = ChatMeColors.lightOnSurface,
                unfocusedTextColor = ChatMeColors.lightOnSurface,
                focusedLabelColor = ChatMeColors.lightPrimary,
                unfocusedLabelColor = ChatMeColors.lightOnSurface.copy(alpha = 0.6f),
                containerColor = ChatMeColors.lightSurface
            ),
            shape = ChatMeShapes.medium
        )
    }
}