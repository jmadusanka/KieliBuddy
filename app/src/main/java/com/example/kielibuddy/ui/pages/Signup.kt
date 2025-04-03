package com.example.kielibuddy.ui.pages

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var userType by remember { mutableStateOf("") }

    // Error states
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var firstNameError by remember { mutableStateOf(false) }
    var lastNameError by remember { mutableStateOf(false) }
    var userTypeError by remember { mutableStateOf(false) }

    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

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

    fun validateInputs(): Boolean {
        firstNameError = firstName.isBlank()
        lastNameError = lastName.isBlank()
        emailError = email.isBlank()
        passwordError = password.isBlank()
        userTypeError = userType.isBlank()
        return !firstNameError && !lastNameError && !emailError && !passwordError && !userTypeError
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF8A2BE2))
            .verticalScroll(scrollState)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Let's create account",
                color = Color(0xFF8A2BE2),
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )
        }

        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "KieliBuddy Logo",
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 12.dp)
        )

        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // User type selection
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedButton(
                    onClick = {
                        userType = "student"
                        userTypeError = false
                    },
                    modifier = Modifier
                        .weight(1f)
                        .graphicsLayer {
                            shadowElevation = 2.dp.toPx()
                            shape = RoundedCornerShape(20.dp)
                        },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (userType == "student") Color(0xFF9370DB) else Color.White,
                        contentColor = if (userType == "student") Color.White else Color.Black
                    )
                ) {
                    Text("I am Student")
                }

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedButton(
                    onClick = {
                        userType = "TEACHER"
                        userTypeError = false
                    },
                    modifier = Modifier
                        .weight(1f)
                        .graphicsLayer {
                            shadowElevation = 2.dp.toPx()
                            shape = RoundedCornerShape(20.dp)
                        },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (userType == "TEACHER") Color(0xFF9370DB) else Color.White,
                        contentColor = if (userType == "TEACHER") Color.White else Color.Black
                    )
                ) {
                    Text("I am Tutor")
                }
            }
            if (userTypeError) {
                Text(
                    text = "Please select user role",
                    color = Color.Red.copy(alpha = 0.8f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, bottom = 8.dp)
                )
            }

            // First Name field
            TextField(
                value = firstName,
                onValueChange = {
                    firstName = it
                    firstNameError = false
                },
                label = { Text("First Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                shape = RoundedCornerShape(20.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = if (firstNameError) Color(0xFFF5F5F5) else Color.White,
                    unfocusedContainerColor = if (firstNameError) Color(0xFFF5F5F5) else Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                    errorContainerColor = Color(0xFFF5F5F5)
                ),
                isError = firstNameError
            )
            if (firstNameError) {
                Text(
                    text = "First name is required",
                    color = Color.Red.copy(alpha = 0.8f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, bottom = 8.dp)
                )
            }

            // Last Name field
            TextField(
                value = lastName,
                onValueChange = {
                    lastName = it
                    lastNameError = false
                },
                label = { Text("Last Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                shape = RoundedCornerShape(20.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = if (lastNameError) Color(0xFFF5F5F5) else Color.White,
                    unfocusedContainerColor = if (lastNameError) Color(0xFFF5F5F5) else Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                    errorContainerColor = Color(0xFFF5F5F5)
                ),
                isError = lastNameError
            )
            if (lastNameError) {
                Text(
                    text = "Last name is required",
                    color = Color.Red.copy(alpha = 0.8f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, bottom = 8.dp)
                )
            }

            // Email field
            TextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = false
                },
                label = { Text("Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                shape = RoundedCornerShape(20.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = if (emailError) Color(0xFFF5F5F5) else Color.White,
                    unfocusedContainerColor = if (emailError) Color(0xFFF5F5F5) else Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                    errorContainerColor = Color(0xFFF5F5F5)
                ),
                isError = emailError
            )
            if (emailError) {
                Text(
                    text = "Email is required",
                    color = Color.Red.copy(alpha = 0.8f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, bottom = 8.dp)
                )
            }

            // Password field
            TextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = false
                },
                label = { Text("Password") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                shape = RoundedCornerShape(20.dp),
                visualTransformation = PasswordVisualTransformation(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = if (passwordError) Color(0xFFF5F5F5) else Color.White,
                    unfocusedContainerColor = if (passwordError) Color(0xFFF5F5F5) else Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                    errorContainerColor = Color(0xFFF5F5F5)
                ),
                isError = passwordError
            )
            if (passwordError) {
                Text(
                    text = "Password is required",
                    color = Color.Red.copy(alpha = 0.8f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, bottom = 8.dp)
                )
            }

            // Sign Up button
            Button(
                onClick = {
                    if (validateInputs()) {
                        authViewModel.signup(firstName, lastName, email, password, userType)
                    }
                },
                enabled = authState.value != AuthState.Loading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF9370DB),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .graphicsLayer {
                        shadowElevation = 4.dp.toPx()
                        shape = RoundedCornerShape(20.dp)
                    },
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
                    .padding(top = 16.dp, bottom = 32.dp)
            )
        }
    }
}