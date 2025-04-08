package com.prj.chatme.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.prj.chatme.CMViewModel
import com.prj.chatme.R
import com.prj.chatme.showToast
import com.prj.chatme.ui.theme.ChatMeColors
import com.prj.chatme.ui.theme.ChatMeTheme
import com.prj.chatme.ui.theme.ChatMeTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(navController: NavController, vm: CMViewModel) {
    ChatMeTheme {
        var email by remember { mutableStateOf("") }
        var isLoading by remember { mutableStateOf(false) }
        val context = LocalContext.current

        LaunchedEffect(vm.passwordResetSuccess.value) {
            if (vm.passwordResetSuccess.value) {
                showToast(context, "Password reset email sent successfully")
                vm.passwordResetSuccess.value = false
                navController.popBackStack()
            }
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = ChatMeColors.lightBackground
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Forgot Password",
                    style = ChatMeTypography.headlineSmall,
                    color = ChatMeColors.lightPrimary
                )

                Spacer(modifier = Modifier.padding(16.dp))

                Text(
                    text = "Enter your email to receive password reset instructions",
                    style = ChatMeTypography.bodyMedium,
                    color = ChatMeColors.lightOnSurface.copy(alpha = 0.8f),
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Email,
                            contentDescription = "Email",
                            tint = ChatMeColors.lightPrimary
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = ChatMeColors.lightPrimary,
                        unfocusedBorderColor = ChatMeColors.divider,
                        focusedTextColor = ChatMeColors.lightOnSurface,
                        unfocusedTextColor = ChatMeColors.lightOnSurface,
                        focusedLabelColor = ChatMeColors.lightPrimary,
                        unfocusedLabelColor = ChatMeColors.lightOnSurface.copy(alpha = 0.6f),
                        focusedLeadingIconColor = ChatMeColors.lightPrimary,
                        unfocusedLeadingIconColor = ChatMeColors.lightPrimary,
                        containerColor = ChatMeColors.lightSurface
                    )
                )

                Spacer(modifier = Modifier.padding(16.dp))

                Button(
                    onClick = {
                        if (email.isNotBlank()) {
                            isLoading = true
                            vm.sendPasswordResetEmail(email) {
                                isLoading = false
                                if (!vm.passwordResetSuccess.value) {
                                    showToast(context, "Error: ${vm.errorMessage.value}")
                                }
                            }
                        } else {
                            showToast(context, "Please enter your email")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ChatMeColors.lightPrimary,
                        contentColor = ChatMeColors.lightOnPrimary
                    )
                ) {
                    Text(
                        text = if (isLoading) "Sending..." else "Send Reset Link",
                        style = ChatMeTypography.labelLarge
                    )
                }

                Spacer(modifier = Modifier.padding(8.dp))

                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ChatMeColors.lightSurface,
                        contentColor = ChatMeColors.lightPrimary
                    )
                ) {
                    Text(
                        text = "Back to Login",
                        style = ChatMeTypography.labelLarge
                    )
                }
            }
        }
    }
}