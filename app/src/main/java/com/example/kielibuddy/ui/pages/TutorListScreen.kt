package com.example.kielibuddy.ui.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.kielibuddy.R
import com.example.kielibuddy.model.UserModel
import com.example.kielibuddy.ui.theme.Purple40
import com.example.kielibuddy.viewmodel.AuthViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TutorListScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    var selectedFilter by remember { mutableStateOf("Default") }
    var tutors by remember { mutableStateOf<List<UserModel>>(emptyList()) }
    var expanded by remember { mutableStateOf(false) }
    var reviewStats by remember { mutableStateOf<Map<String, Pair<Double, Int>>>(emptyMap()) }
    var lessonCounts by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }

    LaunchedEffect(Unit) {
        val snapshot = FirebaseFirestore.getInstance().collection("users")
            .whereEqualTo("role", "TEACHER")
            .get().await()
        tutors = snapshot.documents.mapNotNull { it.toObject(UserModel::class.java) }

        val reviewSnapshot = FirebaseFirestore.getInstance().collection("reviews").get().await()
        val grouped = reviewSnapshot.documents.groupBy { it.getString("tutorId") ?: "" }
        val statsMap = grouped.mapValues { (_, docs) ->
            val ratings = docs.mapNotNull { it.getLong("rating")?.toDouble() }
            ratings.average() to ratings.size
        }
        reviewStats = statsMap

        val bookingsSnapshot = FirebaseFirestore.getInstance().collection("bookings").get().await()
        val groupedBookings = bookingsSnapshot.documents.groupBy { it.getString("tutorId") ?: "" }
        val lessonsMap = groupedBookings.mapValues { (_, docs) ->
            docs.mapNotNull { it.getString("studentId") }.distinct().count()
        }
        lessonCounts = lessonsMap

    }

    val filteredTutors = remember(selectedFilter, tutors, reviewStats) {
        when (selectedFilter) {
            "Lowest Price" -> tutors.sortedBy { it.price50Min }
            "Highest Price" -> tutors.sortedByDescending { it.price50Min }
            "Rating" -> tutors.sortedByDescending { reviewStats[it.id]?.first ?: 0.0 }
            "Reviews" -> tutors.sortedByDescending { reviewStats[it.id]?.second ?: 0 }
            else -> tutors
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Finnish", color = Color.White)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    Spacer(modifier = Modifier.width(48.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Purple40)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF9F7FF))
                .padding(12.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("${filteredTutors.size} tutors", color = Color.Gray, fontSize = 16.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (selectedFilter == "Default") "Sort by relevance" else "Sort by: $selectedFilter",
                        fontSize = 14.sp
                    )
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
                    TutorCard(tutor, navController, reviewStats[tutor.id], lessonCounts[tutor.id] ?: 0)
                }
            }
        }
    }
}

@Composable
fun TutorCard(tutor: UserModel, navController: NavController, stats: Pair<Double, Int>? = null, lessonCount: Int = 0) {
    val ratingText = stats?.let { "%.1f\u2B50".format(it.first) } ?:  "N/A"
    val reviewCount = stats?.second ?: 0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .padding(vertical = 8.dp)
            .clickable { navController.navigate("tutorPublicProfile/${tutor.id}") },
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
                    Text(
                        text = "${tutor.firstName} ${tutor.lastName}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("€${tutor.price50Min}", fontSize = 16.sp)
                        Text(ratingText, fontSize = 14.sp)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("60-min lesson", fontSize = 12.sp, color = Color.Gray)
                        Text(
                            text = "$reviewCount reviews",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.clickable {
                                navController.navigate("reviews/${tutor.id}")
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                tutor.aboutMe.ifEmpty { "No introduction provided yet." },
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))
            Text("$lessonCount lessons", fontSize = 13.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(4.dp))
            val spokenLanguages = tutor.languagesSpoken.take(3).joinToString(", ") +
                    if (tutor.languagesSpoken.size > 3) ", +${tutor.languagesSpoken.size - 3}" else ""
            Text("Speaks $spokenLanguages", fontSize = 13.sp, color = Color.Gray)
        }
    }
}
