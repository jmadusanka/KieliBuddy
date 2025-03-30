package com.example.kielibuddy.ui.pages


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.kielibuddy.viewmodel.AuthViewModel

@Composable
fun ForgotPasswordPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    var email by remember { mutableStateOf("") }
    var showSuccess by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }

    fun validateEmail(): Boolean {
        emailError = email.isBlank()
        return !emailError
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF8A2BE2)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (showSuccess) {
                // Success message
                Text(
                    text = "Reset email sent!",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Check your email for instructions",
                    color = Color.White,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            } else {
                // Form state
                Text(
                    text = "Forgot Password?",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "We'll send you reset instructions",
                    color = Color.White,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Email field - No underline in any state
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
                        errorIndicatorColor = Color.Transparent, // No red underline
                        errorContainerColor = Color(0xFFF5F5F5)
                    ),
                    isError = emailError
                )

                // Error text (without underline)
                if (emailError) {
                    Text(
                        text = "Please enter your email",
                        color = Color(0xFFF44336).copy(alpha = 0.8f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, bottom = 8.dp)
                    )
                }

                // Reset button with shadow
                Button(
                    onClick = {
                        if (validateEmail()) {
                            authViewModel.sendPasswordResetEmail(email) {
                                showSuccess = true
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF9370DB),
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(20.dp),
                            clip = true
                        ),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Text("Reset Password", fontSize = 18.sp)
                }
            }

            // Back link
            Text(
                text = if (showSuccess) "Return to Login" else "Back to Login",
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