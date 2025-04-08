package com.prj.chatme.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.prj.chatme.CheckSignedIn
import com.prj.chatme.CommonProgressBar
import com.prj.chatme.DestinatinScreen
import com.prj.chatme.CMViewModel
import com.prj.chatme.R
import com.prj.chatme.navigateTo
import com.prj.chatme.ui.theme.ChatMeColors
import com.prj.chatme.ui.theme.ChatMeShapes
import com.prj.chatme.ui.theme.ChatMeTheme
import com.prj.chatme.ui.theme.ChatMeTypography
import com.prj.chatme.ui.theme.DarkOrange
import com.prj.chatme.ui.theme.Orange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController, vm: CMViewModel) {
    ChatMeTheme {
        val focusManager = LocalFocusManager.current
        val inProgress = vm.inProgress.value
        var emailState by remember { mutableStateOf(TextFieldValue()) }
        var passwordState by remember { mutableStateOf(TextFieldValue()) }

        CheckSignedIn(navController = navController, vm = vm)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ChatMeColors.lightBackground)
                .clickable { focusManager.clearFocus() }
        ) {
            if (inProgress) {
                CommonProgressBar()
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Logo/Header Section
                Image(
                    painter = painterResource(id = R.drawable.login_icon),
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .width(200.dp)
                        .padding(bottom = 32.dp)
                )

                // Title
                Text(
                    text = "Welcome Back",
                    style = ChatMeTypography.headlineSmall,
                    color = ChatMeColors.lightOnSurface,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Login to continue",
                    style = ChatMeTypography.bodyMedium,
                    color = ChatMeColors.lightOnSurface.copy(alpha = 0.7f),
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                // Email Field
                OutlinedTextField(
                    value = emailState,
                    onValueChange = { emailState = it },
                    label = { Text("Email") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = ChatMeColors.lightPrimary,
                        unfocusedBorderColor = ChatMeColors.divider,
                        focusedLabelColor = ChatMeColors.lightPrimary,
                        unfocusedLabelColor = ChatMeColors.lightOnSurface.copy(alpha = 0.6f),
                        cursorColor = ChatMeColors.lightPrimary,
                        containerColor = ChatMeColors.lightSurface
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                // Password Field
                OutlinedTextField(
                    value = passwordState,
                    onValueChange = { passwordState = it },
                    label = { Text("Password") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = ChatMeColors.lightPrimary,
                        unfocusedBorderColor = ChatMeColors.divider,
                        focusedLabelColor = ChatMeColors.lightPrimary,
                        unfocusedLabelColor = ChatMeColors.lightOnSurface.copy(alpha = 0.6f),
                        cursorColor = ChatMeColors.lightPrimary,
                        containerColor = ChatMeColors.lightSurface
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )

                // Login Button
                Button(
                    onClick = {
                        vm.login(emailState.text, passwordState.text)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ChatMeColors.lightPrimary,
                        contentColor = ChatMeColors.lightOnPrimary
                    ),
                    shape = ChatMeShapes.medium
                ) {
                    Text(
                        text = "Login",
                        style = ChatMeTypography.labelLarge
                    )
                }

                // Forgot Password
                TextButton(
                    onClick = {
                        navigateTo(navController, DestinatinScreen.ForgotPassword.route)
                    },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text(
                        text = "Forgot Password?",
                        style = ChatMeTypography.bodySmall,
                        color = ChatMeColors.lightPrimary
                    )
                }

                // Sign Up Prompt
                Row(
                    modifier = Modifier.padding(top = 32.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Don't have an account?",
                        style = ChatMeTypography.bodyMedium,
                        color = ChatMeColors.lightOnSurface.copy(alpha = 0.7f)
                    )
                    TextButton(
                        onClick = {
                            navigateTo(navController, DestinatinScreen.SignUp.route)
                        }
                    ) {
                        Text(
                            text = "Sign Up",
                            style = ChatMeTypography.bodyMedium,
                            color = ChatMeColors.lightPrimary
                        )
                    }
                }
            }
        }
    }
}

// State variables should be moved inside the composable
@Composable
private fun LoginScreenContent() {

    // ... rest of the content
}