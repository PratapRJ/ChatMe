package com.prj.chatme.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import com.prj.chatme.ui.theme.DarkOrange
import com.prj.chatme.ui.theme.Orange

@Composable
fun LoginScreen(navController: NavController, vm: CMViewModel) {
    val focusManager = LocalFocusManager.current
    val inProgress = vm.inProgress.value
    if (inProgress)
        CommonProgressBar()
    CheckSignedIn(navController = navController, vm = vm)


    Box(modifier = Modifier.fillMaxSize()
        .clickable{focusManager.clearFocus()})
    {
        var emailState = remember {
            mutableStateOf(TextFieldValue())
        }
        var passwordState = remember {
            mutableStateOf(TextFieldValue())
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentHeight()
                .verticalScroll(
                    rememberScrollState()
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(id = R.drawable.login_icon), contentDescription = null,
                modifier = Modifier
                    .width(160.dp)
                    .padding(top = 16.dp)
                    .padding(8.dp)
            )
            Text(
                text = "Login",
                fontSize = 30.sp,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(8.dp)
            )

            OutlinedTextField(
                value = emailState.value, onValueChange = {
                    emailState.value = it
                }, colors = OutlinedTextFieldDefaults.colors(
                    focusedLabelColor = Orange,
                    unfocusedLabelColor = Orange
                ),
                label = { Text(text = "Email") },
                modifier = Modifier.padding(8.dp)
            )
            OutlinedTextField(
                value = passwordState.value, onValueChange = {
                    passwordState.value = it
                }, colors = OutlinedTextFieldDefaults.colors(
                    focusedLabelColor = Orange,
                    unfocusedLabelColor = Orange
                ),
                label = { Text(text = "Password") },
                modifier = Modifier.padding(8.dp)
            )
            Button(
                onClick = {
                    vm.login(emailState.value.text, passwordState.value.text)
                }, colors = ButtonDefaults.buttonColors(
                    containerColor = DarkOrange,  // Background color
                    contentColor = Color.White   // Text/icon color
                ), modifier = Modifier.padding(8.dp)
            ) {
                Text(text = "Login")
            }
            Text(
                text = "Forgot Password?",
                color = Color.Red,
                modifier = Modifier.padding(top = 16.dp)
            )
            Text(
                text = "Don't have an account? Sign Up",
                color = DarkOrange,
                modifier = Modifier.clickable {
                    navigateTo(navController, DestinatinScreen.SignUp.route)
                }.padding(8.dp)
            )


        }
    }

}