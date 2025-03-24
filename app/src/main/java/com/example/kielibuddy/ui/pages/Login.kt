package com.example.kielibuddy.ui.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.kielibuddy.R
import com.example.kielibuddy.viewmodel.AuthViewModel
import com.example.kielibuddy.viewmodel.FakeAuthViewModel

@Composable
fun LoginPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel // Add authViewModel parameter
) {
    var email by remember { mutableStateOf("") } // Correctly initialize email state
    var password by remember { mutableStateOf("") } // Correctly initialize password state

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF8A2BE2)), // Correctly define color
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Add Image (Logo)
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "KieliBuddy Logo",
                modifier = Modifier
                    .size(380.dp)
                    .padding(top = 8.dp) // Add some padding at the top
            )

            // Add Email and Password Input Fields
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp), // Add padding
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Email Input Field
                TextField(
                    value = email, // Bind email state
                    onValueChange = { email = it }, // Update email state
                    label = { Text("Email") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp), // Add spacing below
                    shape = RoundedCornerShape(20.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                // Password Input Field
                TextField(
                    value = password, // Bind password state
                    onValueChange = { password = it }, // Update password state
                    label = { Text("Password") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp), // Add spacing below
                    shape = RoundedCornerShape(20.dp),
                    visualTransformation = PasswordVisualTransformation(), // Hide password text
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }

            // White Box covering from Login to Create an account
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false) // Take up remaining space but don't fill entirely
                    .background(Color.White, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .padding(24.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Solid Purple Box for Login Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color(0xFF9370DB), // Solid purple color
                                shape = RoundedCornerShape(20.dp)
                            )
                    ) {
                        Button(
                            onClick = {
                                authViewModel.login(email, password) // Call login function
                                navController.navigate("home") // Navigate to Home after login
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF9370DB), // Set containerColor to purple
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(0.dp),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            Text(
                                text = "Login",
                                fontSize = 18.sp
                            )
                        }
                    }

                    Text(
                        text = "or continue with",
                        color = Color.Black,
                        modifier = Modifier.padding(top = 16.dp)
                    )

                    Row(modifier = Modifier.padding(top = 16.dp)) {
                        // Google Button with Red Background
                        Button(
                            onClick = { /* Handle Google Login */ },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFDB4437), // Google Red
                                contentColor = Color.White // White text
                            ),
                            modifier = Modifier.padding(end = 8.dp),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(text = "Google", color = Color.White)
                        }

                        // Facebook Button with Blue Background
                        Button(
                            onClick = { /* Handle Facebook Login */ },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1877F2), // Facebook Blue
                                contentColor = Color.White // White text
                            ),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(text = "Facebook", color = Color.White)
                        }
                    }

                    Text(
                        text = "Forgot Password?",
                        color = Color.Black,
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .clickable { navController.navigate("ForgotPassword") },
                        textDecoration = TextDecoration.Underline
                    )

                    Text(
                        text = "or",
                        color = Color.Black,
                        modifier = Modifier.padding(top = 16.dp)
                    )

                    // Solid Purple Box for Create Account Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color(0xFF892BE1), // Solid purple color
                                shape = RoundedCornerShape(20.dp)
                            )
                    ) {
                        Button(
                            onClick = { navController.navigate("signup") }, // Navigate to Signup
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF9370DB), // Set containerColor to purple
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(0.dp),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            Text(
                                text = "Create an account",
                                fontSize = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPagePreview() {
    LoginPage(
        navController = rememberNavController(),
        authViewModel = FakeAuthViewModel() // Use FakeAuthViewModel for preview
    )
}