package com.example.kielibuddy.ui.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.kielibuddy.model.UserModel
import com.example.kielibuddy.model.UserRole
import com.example.kielibuddy.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@Composable
fun TutorListScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val tutors = rememberTutorList()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Find Your Tutor",
            fontSize = 22.sp,
            color = Color(0xFF6A3DE2)
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(tutors) { tutor ->
                TutorCard(tutor = tutor, navController = navController)
            }
        }
    }
}

@Composable
fun TutorCard(tutor: UserModel, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = tutor.profileImg,
                contentDescription = "Tutor Image",
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = tutor.firstName + " " + tutor.lastName, fontSize = 18.sp)
                Text(text = "€${tutor.price50Min}/lesson", fontSize = 14.sp, color = Color.Black)
                Text(text = tutor.aboutMe ?: "", fontSize = 12.sp, color = Color.Gray)
                //Text(text = "${tutor.students} students • ${tutor.lessons} lessons", fontSize = 12.sp, color = Color.Gray)
                //Text(text = "⭐ ${tutor.rating} (${tutor.reviews} reviews)", fontSize = 12.sp, color = Color.Gray)

                Spacer(modifier = Modifier.height(8.dp))

                Button(onClick = {
                    navController.navigate("chat/${tutor.id}/${tutor.firstName}")
                }) {
                    Text("Chat Now")
                }
            }
        }
    }
}

@Composable
fun rememberTutorList(): List<UserModel> {
    val tutors = remember { mutableStateOf(emptyList<UserModel>()) }
    LaunchedEffect(Unit) {
        val db = FirebaseFirestore.getInstance()
        val snapshot = db.collection("users").whereEqualTo("role", UserRole.TEACHER.name).get().await()
        val result = snapshot.documents.mapNotNull { it.toObject(UserModel::class.java) }
        tutors.value = result
    }
    return tutors.value
}
