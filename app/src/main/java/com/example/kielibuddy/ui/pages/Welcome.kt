package com.example.kielibuddy.ui.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.kielibuddy.R

@Composable
fun WelcomePage(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val scrollState = rememberScrollState() // Add scroll state

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF8A2BE2)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.verticalScroll(scrollState), // Add scroll modifier
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Add Image
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "KieliBuddy Logo",
                modifier = Modifier
                    .size(300.dp)
                    .padding(vertical = 32.dp) // Add vertical padding
            )

            // Add Text Elements
            Text(
                text = "Let's start",
                color = Color.White,
                fontSize = 30.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "your Finnish",
                color = Color.White,
                fontSize = 30.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Text(
                text = "learning journey",
                color = Color.White,
                fontSize = 30.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Text(
                text = "together",
                color = Color.White,
                fontSize = 30.sp,
                modifier = Modifier.padding(bottom = 32.dp) // Increased bottom padding
            )

            // Get Started Button
            Button(
                onClick = { navController.navigate("login") },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                modifier = Modifier
                    .padding(top = 24.dp)
                    .padding(bottom = 32.dp) // Add bottom padding for scroll
            ) {
                Text(
                    text = "Get Started",
                    color = Color.White,
                    fontSize = 18.sp
                )
            }
        }
    }
}