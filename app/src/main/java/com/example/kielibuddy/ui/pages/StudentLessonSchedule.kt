package com.example.kielibuddy.ui.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.kielibuddy.R
import com.example.kielibuddy.ui.components.BackButton
import com.example.kielibuddy.ui.components.BottomNavigationBar
import com.example.kielibuddy.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentScheduleScreen(navController: NavController, authViewModel: AuthViewModel) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userData by authViewModel.userData.observeAsState()
    val schedule = remember { mutableStateListOf<LessonSchedule>() } // Placeholder for schedule data
    val hasSchedule = schedule.isNotEmpty()

    LaunchedEffect(currentUser?.uid) {
        currentUser?.uid?.let { authViewModel.loadUserData(it) }
        schedule.addAll(generateDummySchedule())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    BackButton(navController = navController)
                },
                title = {
                    Text(
                        "Schedule",
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp
                    )
                },
                actions = {
                    // Display user's profile image if available
                    userData?.profileImg?.let { imageUrl ->
                        Image(
                            painter = rememberAsyncImagePainter(imageUrl),
                            contentDescription = "Profile",
                            modifier = Modifier
                                .size(84.dp)
                                .padding(end = 16.dp, top = 16.dp)
                        )
                    } ?: run {
                        // Show a default placeholder if the profile image is not available
                        Image(
                            painter = painterResource(id = R.drawable.ic_schedule),
                            contentDescription = "Profile",
                            modifier = Modifier
                                .size(36.dp)
                                .padding(end = 16.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = Color.Black,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (hasSchedule) {
                Text(
                    "Your Upcoming Lessons",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    color = Color.Black
                )
                LazyColumn {
                    items(schedule) { lesson ->
                        ScheduleItem(lesson = lesson)
                        Divider()
                    }
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_messages), // Replace with your empty schedule icon
                        contentDescription = "No Schedule",
                        modifier = Modifier.size(120.dp),
                        colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.Gray.copy(alpha = 0.7f))
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "No lessons scheduled yet!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Book a lesson with a tutor to see your schedule here.",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = {
                            navController.navigate("list") // Navigate to the tutor list
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6A3DE2),
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp)
                    ) {
                        Text("Find a tutor", fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun ScheduleItem(lesson: LessonSchedule) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = lesson.tutorName,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${lesson.dayOfWeek.name.toLowerCase().capitalize()} - ${lesson.date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))} - ${lesson.time.format(DateTimeFormatter.ofPattern("HH:mm"))}",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
        // You can add more details or an icon here if needed
    }
}

// Dummy data generation
fun generateDummySchedule(): List<LessonSchedule> {
    val currentDate = LocalDate.now(java.time.ZoneId.of("Europe/Helsinki")) // Using current date in Oulu
    return listOf(
        LessonSchedule(
            tutorName = "Alice Johnson",
            dayOfWeek = DayOfWeek.MONDAY,
            date = currentDate.plusDays(7), // Next Monday
            time = LocalTime.of(10, 0)
        ),
        LessonSchedule(
            tutorName = "Bob Williams",
            dayOfWeek = DayOfWeek.WEDNESDAY,
            date = currentDate.plusDays(9), // Next Wednesday
            time = LocalTime.of(15, 30)
        ),
        LessonSchedule(
            tutorName = "Charlie Brown",
            dayOfWeek = DayOfWeek.FRIDAY,
            date = currentDate.plusDays(11), // Next Friday
            time = LocalTime.of(11, 0)
        )
        // Add more dummy lessons as needed
    )
}

// Data class for Lesson Schedule
data class LessonSchedule(
    val tutorName: String,
    val dayOfWeek: DayOfWeek,
    val date: LocalDate,
    val time: LocalTime
)