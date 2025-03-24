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
import androidx.compose.ui.text.style.TextAlign
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
fun SignupPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF8A2BE2))
    ) {

        // White box with lets create account text
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
                lineHeight = 30.sp,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
// Add Image (Logo)
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "KieliBuddy Logo",
            modifier = Modifier
                .size(300.dp)
                .padding(bottom = 0.dp)
                .align(Alignment.CenterHorizontally)
        )

        Box(
            modifier = Modifier
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(

                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                // Email Input Field
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

                // Password Input Field
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

                // Sign Up Button
                Button(
                    onClick = {
                        authViewModel.signup(email, password)
                        navController.navigate("home")
                    },
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

                // Login link
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
}