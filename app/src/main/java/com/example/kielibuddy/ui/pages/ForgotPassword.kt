package com.example.kielibuddy.ui.pages


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun ForgotPasswordPage(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF8A2BE2)), // Purple background
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title: "Forgot Password?"
            Text(
                text = "Forgot Password?",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Subtitle: "No worries, we'll send you reset instructions"
            Text(
                text = "No worries, we'll send you reset instructions",
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Email Input Field
            TextField(
                value = "", // Placeholder for email
                onValueChange = { /* No action needed for UI preview */ },
                label = { Text("Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            // Reset Password Button
            Button(
                onClick = { navController.popBackStack() }, // Navigate back to Login
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF9370DB), // Purple background
                    contentColor = Color.White // White text
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                Text(
                    text = "Reset Password",
                    fontSize = 18.sp
                )
            }

            // Back to Login Link
            Text(
                text = "Back to Login",
                color = Color.White,
                fontSize = 14.sp,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier
                    .clickable { navController.popBackStack() } // Navigate back to Login
                    .padding(top = 16.dp)
            )
        }
    }
}