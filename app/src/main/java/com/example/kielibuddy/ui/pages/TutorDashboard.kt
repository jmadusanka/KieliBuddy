package com.example.kielibuddy.ui.screens.tutor.TutorHomeScreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.kielibuddy.model.UserModel
import com.example.kielibuddy.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TutorDashboard(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {

    val userData by authViewModel.userData.observeAsState()
    var showMenu by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tutor Dashboard") },
                navigationIcon = {
                    IconButton(onClick = { /* Handle back navigation */ }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(Icons.Filled.MoreVert, "Menu")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Sign out") },
                            onClick = {
                                showMenu = false
                                authViewModel.signout()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Edit Profile") },
                            onClick = {
                                showMenu = false
                                // TODO: Navigate to edit profile screen
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("View Sample Pages") },
                            onClick = {
                                showMenu = false
                                navController.navigate("gallery")
                            }
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Tutor Profile Section
            TutorProfile(
                user = userData,
                languages = userData?.languagesSpoken ?: emptyList(),
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

            // Notification Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp), // Reduced outer padding
                horizontalArrangement = Arrangement.spacedBy(8.dp) // Less spacing between items
            ) {
                Box(modifier = Modifier.weight(1f).height(100.dp)) {
                    NotificationButton(
                        title = "New Messages",
                        count = 5,
                        onClick = { /* TODO */ }
                    )
                }
                Box(modifier = Modifier.weight(1f).height(100.dp)) {
                    NotificationButton(
                        title = "Pending Requests",
                        count = 3,
                        onClick = { /* TODO */ }
                    )
                }
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
}

@Composable
fun NotificationButton(
    title: String,
    count: Int,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD1B3FF)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxSize() // Make the button fill the Box
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                color = Color(0xFF4E2AB3),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = count.toString(),
                color = Color(0xFF4E2AB3),
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )
        }
    }
}

@Composable
fun TutorProfile(user: UserModel?, languages: List<String>, rating: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Card(
            modifier = Modifier.size(125.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            border = BorderStroke(1.dp, Color.LightGray)
        ) {
            Image(
                painter = rememberAsyncImagePainter(user?.profileImg),
                contentDescription = "Tutor Profile",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.FillBounds
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "${user?.firstName ?: "First"} ${user?.lastName ?: "Last"}",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6A3DE2)
        )
        Text(
            text = "Languages: ${languages.joinToString(", ")}",
            fontSize = 16.sp,
            color = Color.Gray
        )
        Text(rating, fontSize = 18.sp, color = Color(0xFF4E2AB3))
    }
}

@Composable
fun UpcomingLessonsCard(lessons: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
            contentColor = Color.Black
        ),
        border = BorderStroke(1.dp, Color(0xFF6A3DE2))
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
            .height(200.dp),
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