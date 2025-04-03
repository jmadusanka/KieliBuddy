package com.example.kielibuddy.ui.pages

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
fun LoginPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    activity: Activity
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    val authState = authViewModel.authState.observeAsState()
    val scrollState = rememberScrollState()

    // Handle Firebase responses
    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Authenticated -> navController.navigate("home")
            is AuthState.Error -> {
                val error = (authState.value as AuthState.Error).message
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            }
            else -> Unit
        }
    }

    //  validation
    fun validateInputs(): Boolean {
        emailError = email.isBlank()
        passwordError = password.isBlank()
        return !emailError && !passwordError
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF8A2BE2)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo and input fields
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .size(280.dp)
                        .padding(top = 8.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
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
                            .padding(bottom = 8.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
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
                            .padding(bottom = 4.dp),
                        shape = RoundedCornerShape(20.dp),
                        visualTransformation = PasswordVisualTransformation(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
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
                }
            }

            // Bottom section buttons
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .padding(24.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Login button
                    Button(
                        onClick = {
                            if (validateInputs()) {
                                authViewModel.login(email, password, navController)
                            }
                        },
                        enabled = authState.value != AuthState.Loading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF9370DB),
                            contentColor = Color.White,
                            disabledContainerColor = Color(0xFF9370DB).copy(alpha = 0.5f),
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer {
                                shadowElevation = 4.dp.toPx()
                                shape = RoundedCornerShape(20.dp)
                            },
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        Text("Login", fontSize = 18.sp)
                    }

                    Text("or continue with", color = Color.Black, modifier = Modifier.padding(top = 16.dp))

// Social login icons
                    Row(
                        modifier = Modifier.padding(top = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Google icon
                        IconButton(
                            onClick = { authViewModel.handleGoogleSignIn(context, navController) },
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color(0xFFF5F5F5), CircleShape)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.google_icon),
                                contentDescription = "Google login",
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        // Facebook icon
                        IconButton(
                            onClick = { /* Handle Facebook login */ },
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color(0xFFF5F5F5), CircleShape)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.facebook_icon),
                                contentDescription = "Facebook login",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    // Forgot password
                    Text(
                        text = "Forgot Password?",
                        color = Color.Black,
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .clickable { navController.navigate("forgotPassword") },
                        textDecoration = TextDecoration.Underline
                    )

                    Text("or", color = Color.Black, modifier = Modifier.padding(top = 16.dp))

                    // Sign-up button
                    Button(
                        onClick = { navController.navigate("signup") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF9370DB),
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer {
                                shadowElevation = 2.dp.toPx()
                                shape = RoundedCornerShape(20.dp)
                            },
                    ) {
                        Text("Create an account", fontSize = 18.sp)
                    }
                }
            }
        }
    }
}