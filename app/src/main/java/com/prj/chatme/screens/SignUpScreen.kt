package com.prj.chatme.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.prj.chatme.CMViewModel
import com.prj.chatme.CheckSignedIn
import com.prj.chatme.CommonProgressBar
import com.prj.chatme.DestinatinScreen
import com.prj.chatme.R
import com.prj.chatme.navigateTo
import com.prj.chatme.ui.theme.ChatMeColors
import com.prj.chatme.ui.theme.ChatMeTheme
import com.prj.chatme.ui.theme.ChatMeTypography
import com.prj.chatme.ui.theme.DarkOrange
import com.prj.chatme.ui.theme.SuccessColor
import com.prj.chatme.ui.theme.Orange
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navController: NavController, vm: CMViewModel) {
    ChatMeTheme {
        val inProgress = vm.inProgress.value
        val focusManager = LocalFocusManager.current

        CheckSignedIn(navController = navController, vm = vm)

        if (inProgress) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = ChatMeColors.lightPrimary)
            }
            return@ChatMeTheme
        }

        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .clickable { focusManager.clearFocus() }
                .background(ChatMeColors.lightBackground)
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Logo and Title
                Image(
                    painter = painterResource(id = R.drawable.chat),
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .size(120.dp)
                        .padding(bottom = 24.dp)
                )

                Text(
                    text = "Create Your Account",
                    style = ChatMeTypography.headlineSmall,
                    color = ChatMeColors.lightPrimary,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                // Form Fields
                val nameState = remember { mutableStateOf(TextFieldValue()) }
                val numberState = remember { mutableStateOf(TextFieldValue()) }
                val emailState = remember { mutableStateOf(TextFieldValue()) }
                val passwordState = remember { mutableStateOf(TextFieldValue()) }

                OutlinedTextField(
                    value = nameState.value,
                    onValueChange = { nameState.value = it },
                    label = { Text("Full Name") },
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = ChatMeColors.lightPrimary,
                        unfocusedBorderColor = ChatMeColors.divider,
                        focusedTextColor = ChatMeColors.lightOnSurface,
                        unfocusedTextColor = ChatMeColors.lightOnSurface,
                        focusedLabelColor = ChatMeColors.lightPrimary,
                        unfocusedLabelColor = ChatMeColors.lightOnSurface.copy(alpha = 0.6f),
                        containerColor = ChatMeColors.lightSurface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = numberState.value,
                    onValueChange = { numberState.value = it },
                    label = { Text("Phone Number") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = ChatMeColors.lightPrimary,
                        unfocusedBorderColor = ChatMeColors.divider,
                        focusedTextColor = ChatMeColors.lightOnSurface,
                        unfocusedTextColor = ChatMeColors.lightOnSurface,
                        focusedLabelColor = ChatMeColors.lightPrimary,
                        unfocusedLabelColor = ChatMeColors.lightOnSurface.copy(alpha = 0.6f),
                        containerColor = ChatMeColors.lightSurface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = emailState.value,
                    onValueChange = { emailState.value = it },
                    label = { Text("Email") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = ChatMeColors.lightPrimary,
                        unfocusedBorderColor = ChatMeColors.divider,
                        focusedTextColor = ChatMeColors.lightOnSurface,
                        unfocusedTextColor = ChatMeColors.lightOnSurface,
                        focusedLabelColor = ChatMeColors.lightPrimary,
                        unfocusedLabelColor = ChatMeColors.lightOnSurface.copy(alpha = 0.6f),
                        containerColor = ChatMeColors.lightSurface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = passwordState.value,
                    onValueChange = { passwordState.value = it },
                    label = { Text("Password") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = ChatMeColors.lightPrimary,
                        unfocusedBorderColor = ChatMeColors.divider,
                        focusedTextColor = ChatMeColors.lightOnSurface,
                        unfocusedTextColor = ChatMeColors.lightOnSurface,
                        focusedLabelColor = ChatMeColors.lightPrimary,
                        unfocusedLabelColor = ChatMeColors.lightOnSurface.copy(alpha = 0.6f),
                        containerColor = ChatMeColors.lightSurface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                )

                // Sign Up Button
                Button(
                    onClick = {
                        vm.signUp(
                            nameState.value.text,
                            numberState.value.text,
                            emailState.value.text,
                            passwordState.value.text
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ChatMeColors.lightPrimary,
                        contentColor = ChatMeColors.lightOnPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .height(48.dp)
                ) {
                    Text(
                        text = "Sign Up",
                        style = ChatMeTypography.labelLarge
                    )
                }

                // Login Link
                Text(
                    text = "Already have an account? Login",
                    style = ChatMeTypography.bodyMedium,
                    color = ChatMeColors.lightPrimary,
                    modifier = Modifier
                        .clickable {
                            navigateTo(navController, DestinatinScreen.Login.route)
                        }
                        .padding(8.dp)
                )
            }
        }
    }
}