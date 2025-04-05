package com.example.kielibuddy.ui.screens.tutor.TutorHomeScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.kielibuddy.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import coil.compose.rememberAsyncImagePainter
import com.example.kielibuddy.model.UserModel

@Composable
fun TutorDashboard(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {

    val userData by authViewModel.userData.observeAsState()

    LaunchedEffect(Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null && userData == null) {
            authViewModel.loadUserData(uid)
        }
    }

    if (userData == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Tutor Profile
        TextButton(onClick = {
            authViewModel.signout()
        }) {
            Text(text = "Sign out")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate("gallery") },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("View Sample Pages")
        }

        TutorProfile(
            userData,
            languages = listOf("Finnish", "English"),
            rating = "4.5/5 ⭐"
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            InfoCard(title = "Earnings", value = "€450", textColor = Color(0xFF6A3DE2))
            InfoCard(title = "Students", value = "15 Students", textColor = Color(0xFF6A3DE2))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Upcoming Lessons
        UpcomingLessonsCard(
            lessons = listOf("Beginner Finnish - 10:00 AM", "Intermediate Finnish - 2:00 PM")
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Notifications & Requests
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            NotificationCard(title = "New Messages", count = 5, cardColor = Color(0xFFD1B3FF), textColor = Color(0xFF4E2AB3))
            NotificationCard(title = "Pending Requests", count = 3, cardColor = Color(0xFFD1B3FF), textColor = Color(0xFF4E2AB3))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action Buttons
        Button(
            onClick = { /* View Earnings */ },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A3DE2))
        ) {
            Text(text = "View Earnings", color = Color.White)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { /* Approve Trial Lessons */ },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4E2AB3))
        ) {
            Text(text = "Approve Trial Lessons", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Reviews Section
        ReviewsCard(
            reviews = listOf(
                "Great tutor! Helped me improve my Finnish quickly. ⭐⭐⭐⭐⭐",
                "Very patient and knowledgeable. Highly recommend! ⭐⭐⭐⭐⭐",
                "Fun lessons and great explanations. ⭐⭐⭐⭐"
            )
        )
    }
}

@Composable
fun TutorProfile(user: UserModel?, languages: List<String>, rating: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = rememberAsyncImagePainter(user?.profileImg),
            contentDescription = "Tutor Profile",
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = (user?.firstName ?: "jas") + " " + (user?.lastName ?: ""), fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6A3DE2))
        Text(text = "Languages: ${languages.joinToString(", ")}", fontSize = 14.sp, color = Color.Gray)
        Text(text = rating, fontSize = 16.sp, color = Color(0xFF4E2AB3))
    }
}

@Composable
fun UpcomingLessonsCard(lessons: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFD1B3FF))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Upcoming Lessons", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            lessons.forEach { lesson ->
                Text(text = lesson, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun ReviewsCard(reviews: List<String>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp), // Set a fixed height to enable scrolling
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Student Reviews", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6A3DE2))

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(reviews.size) { index ->
                    Text(
                        text = reviews[index],
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun InfoCard(title: String, value: String, textColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = title, fontSize = 12.sp, color = Color.Gray)
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = textColor)
    }
}

@Composable
fun NotificationCard(title: String, count: Int, cardColor: Color, textColor: Color) {
    Card(
        modifier = Modifier.width(140.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textColor)
            Text(text = count.toString(), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = textColor)
        }
    }
}
