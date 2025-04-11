package com.example.kielibuddy.ui.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.kielibuddy.R
import com.example.kielibuddy.model.UserModel
import com.example.kielibuddy.ui.components.BackButton
import com.example.kielibuddy.viewmodel.AuthViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlin.random.Random

@Composable
fun TutorListScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    var selectedFilter by remember { mutableStateOf("Default") }
    var tutors by remember { mutableStateOf<List<UserModel>>(emptyList()) }
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val snapshot = FirebaseFirestore.getInstance().collection("users").whereEqualTo("role", "TEACHER").get().await()
        tutors = snapshot.documents.mapNotNull { doc -> doc.toObject(UserModel::class.java) }
    }

    val filteredTutors = remember(selectedFilter, tutors) {
        when (selectedFilter) {
            "Lowest Price" -> tutors.sortedBy { it.price50Min }
            "Highest Price" -> tutors.sortedByDescending { it.price50Min }
            "Rating" -> tutors.sortedByDescending { Random.nextDouble(4.0, 5.0) }
            "Reviews" -> tutors.sortedByDescending { Random.nextInt(20, 150) }
            else -> tutors
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF9F7FF))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            BackButton(navController = navController)
            Text("Finnish", fontSize = 36.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(48.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("${filteredTutors.size} tutors", color = Color.Gray, fontSize = 16.sp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Sort by relevance", fontSize = 14.sp)
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Color.Black)
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    offset = DpOffset(x = 50.dp, y = 0.dp),
                    modifier = Modifier
                        .wrapContentWidth()
                        .background(Color.White, RoundedCornerShape(8.dp))
                ) {
                    listOf("Lowest Price", "Highest Price", "Rating", "Reviews").forEach { filter ->
                        DropdownMenuItem(
                            text = { Text(filter) },
                            onClick = { selectedFilter = filter; expanded = false }
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(filteredTutors) { tutor ->
                TutorCard(tutor, navController)
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
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                val painter = rememberAsyncImagePainter(model = tutor.profileImg.ifEmpty { R.drawable.logo })
                Image(
                    painter = painter,
                    contentDescription = "Tutor Profile Picture",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("${tutor.firstName} ${tutor.lastName}", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text("€${tutor.price50Min}", fontSize = 18.sp)
                        Text("⭐ ${String.format("%.1f", Random.nextDouble(4.0, 5.0))}", fontSize = 14.sp)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text("60-min lesson", fontSize = 14.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "${Random.nextInt(20, 150)} reviews",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.clickable { navController.navigate("reviews/${tutor.id}") }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(tutor.aboutMe.ifEmpty { "No introduction provided yet." }, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(4.dp))
            Text("${tutor.lessonCount} lessons", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            val spokenLanguages = tutor.languagesSpoken.take(3).joinToString(", ") +
                    if (tutor.languagesSpoken.size > 3) ", +${tutor.languagesSpoken.size - 3}" else ""
            Text("Speaks $spokenLanguages", fontSize = 14.sp, color = Color.Gray)
        }
    }
}