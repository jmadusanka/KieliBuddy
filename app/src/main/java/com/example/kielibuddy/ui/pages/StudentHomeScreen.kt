package com.example.kielibuddy.ui.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun StudentHomeScreen(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 🟢 Profile Section
        ProfileSection()

        Spacer(modifier = Modifier.height(16.dp))

        // 🟣 Progress & Goals Section
        ProgressSection()

        Spacer(modifier = Modifier.height(16.dp))

        // 🟠 Schedule Section
        ScheduleSection(navController)

        Spacer(modifier = Modifier.height(16.dp))

        // 🔵 Recommended Tutors Section
        Text("Recommended Tutors", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        TutorListScreen(modifier = Modifier, navController = navController)
    }
}

@Composable
fun ProfileSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF6A3DE2), shape = RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = android.R.drawable.ic_menu_gallery),
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color.White)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text("Mahesh Idangodage", fontSize = 20.sp, color = Color.White)
            Text("Student", fontSize = 14.sp, color = Color.LightGray)
        }
    }
}

@Composable
fun ProgressSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Weekly Learning Goal", fontSize = 16.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(8.dp))

            var progress by remember { mutableStateOf(0.7f) }

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp),
                color = Color(0xFF6A3DE2),
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text("70% Completed", fontSize = 14.sp, color = Color.Gray)
        }
    }
}

@Composable
fun ScheduleSection(navController: NavController) {
    Text("Upcoming Lesson", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F3F3)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Finnish Lesson with John Doe", fontSize = 16.sp, color = Color.Black)
            Text("Friday, 3 PM - 4 PM", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { navController.navigate("join_session") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A3DE2))
            ) {
                Text("Join Now", color = Color.White)
            }
        }
    }
}
