package com.example.kielibuddy.ui.pages

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.kielibuddy.R
import com.example.kielibuddy.viewmodel.AuthState
import com.example.kielibuddy.viewmodel.AuthViewModel

@Composable
fun SignupPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var userType by remember { mutableStateOf("") } // "student" or "tutor"
    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Authenticated -> navController.navigate("home")
            is AuthState.Error -> Toast.makeText(
                context,
                (authState.value as AuthState.Error).message,
                Toast.LENGTH_SHORT
            ).show()
            else -> Unit
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF8A2BE2))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Let's\ncreate account",
                color = Color(0xFF8A2BE2),
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                lineHeight = 30.sp
            )
        }

        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "KieliBuddy Logo",
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.CenterHorizontally)
        )

        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Moved the user type selection to the top
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedButton(
                    onClick = { userType = "student" },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (userType == "student") Color(0xFF9370DB) else Color.White,
                        contentColor = if (userType == "student") Color.White else Color.Black
                    )
                ) {
                    Text("I am Student")
                }

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedButton(
                    onClick = { userType = "tutor" },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (userType == "tutor") Color(0xFF9370DB) else Color.White,
                        contentColor = if (userType == "tutor") Color.White else Color.Black
                    )
                ) {
                    Text("I am Tutor")
                }
            }

            // Full Name field now comes after the user type selection
            TextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Full Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                shape = RoundedCornerShape(20.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                shape = RoundedCornerShape(20.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                shape = RoundedCornerShape(20.dp),
                visualTransformation = PasswordVisualTransformation(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            Button(
                onClick = { authViewModel.signup(email, password, fullName, userType) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF9370DB),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                Text("Sign Up", fontSize = 18.sp)
            }

            Text(
                text = "Already have an account? Login",
                color = Color.White,
                fontSize = 14.sp,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier
                    .clickable { navController.popBackStack() }
                    .padding(top = 16.dp)
            )
        }
    }
}