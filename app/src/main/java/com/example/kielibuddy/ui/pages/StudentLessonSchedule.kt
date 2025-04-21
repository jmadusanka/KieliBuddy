package com.example.kielibuddy.ui.pages

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.kielibuddy.model.Booking
import com.example.kielibuddy.model.UserModel
import com.example.kielibuddy.model.UserRole
import com.example.kielibuddy.repository.UserRepository
import com.example.kielibuddy.ui.components.BottomNavigationBar
import com.example.kielibuddy.viewmodel.AuthViewModel
import com.example.kielibuddy.viewmodel.BookingViewModel
import com.google.firebase.auth.FirebaseAuth
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kielibuddy.ui.theme.Purple40
import java.net.URLEncoder
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentScheduleScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    roleMode: UserRole = UserRole.STUDENT
) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userData by authViewModel.userData.observeAsState()
    val bookingViewModel: BookingViewModel = viewModel()
    val bookings by bookingViewModel.studentBookings.collectAsState()

    LaunchedEffect(currentUser?.uid) {
        currentUser?.uid?.let {
            authViewModel.loadUserData(it)
            if (roleMode == UserRole.TEACHER) {
                bookingViewModel.loadTutorBookings(it)
            } else {
                bookingViewModel.loadStudentBookings(it)
            }
        }
    }

    val screenTitle = if (roleMode == UserRole.TEACHER) "My Upcoming Lessons" else "My Bookings"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(screenTitle, color = Color.White)
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Purple40
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
            if (bookings.isNotEmpty()) {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                val now = LocalDateTime.now()

                val sortedBookings = remember(bookings) {
                    bookings.sortedWith(compareBy<Booking> {
                        LocalDateTime.parse("${it.date} ${it.timeSlot.split(" - ").first()}", formatter)
                    }.thenComparing { booking ->
                        val dateTimeStr = "${booking.date} ${booking.timeSlot.split(" - ").first()}"
                        try {
                            LocalDateTime.parse(dateTimeStr, formatter)
                        } catch (e: Exception) {
                            LocalDateTime.MIN
                        }
                    })
                }

                val lessonDuration = Duration.ofMinutes(60) // Assuming lessons last 1 hour
                val (upcomingBookings, pastBookings) = sortedBookings.partition { booking ->
                    val dateTimeStr = "${booking.date} ${booking.timeSlot.split(" - ").first()}"
                    val startDateTime = try {
                        LocalDateTime.parse(dateTimeStr, formatter)
                    } catch (e: Exception) {
                        LocalDateTime.MIN
                    }
                    val endDateTime = startDateTime.plus(lessonDuration)
                    now.isBefore(endDateTime) // Stay in upcoming if current time is before lesson ends
                }


                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (upcomingBookings.isNotEmpty()) {
                        item {
                            Text("Upcoming Lessons", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                        items(upcomingBookings) { booking ->
                            BookingCard(booking = booking, viewerRole = roleMode, isPast = false, navController)
                        }
                    }

                    if (pastBookings.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Past Lessons", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Gray)
                        }
                        items(pastBookings) { booking ->
                            BookingCard(booking = booking, viewerRole = roleMode, isPast = true, navController)
                        }
                    }
                }
            } else {
                Text("No lessons found.", color = Color.Gray)
            }
        }
    }
}

@Composable
fun BookingCard(booking: Booking, viewerRole: UserRole, isPast: Boolean = false, navController: NavController) {
    val now = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    val startDateTime = try {
        val dateTimeStr = "${booking.date} ${booking.timeSlot.split(" - ").first()}"
        LocalDateTime.parse(dateTimeStr, formatter)
    } catch (e: Exception) {
        LocalDateTime.MIN
    }

    val durationUntilStart = Duration.between(now, startDateTime)
    val showCountdown = durationUntilStart.toHours() <= 1 && durationUntilStart.toMinutes() > 5
    val lessonDuration = Duration.ofMinutes(60) // assuming 1 hour lessons
    val endDateTime = startDateTime.plus(lessonDuration)
    val enableJoinButton = now.isAfter(startDateTime.minusMinutes(5)) && now.isBefore(endDateTime) && !isPast

    var otherUser by remember { mutableStateOf<UserModel?>(null) }
    val userRepo = remember { UserRepository() }

    val otherUserId = if (viewerRole == UserRole.STUDENT) booking.tutorId else booking.studentId

    LaunchedEffect(otherUserId) {
        otherUser = userRepo.getUserDetails(otherUserId)
    }

    val profileImg = otherUser?.profileImg
    val fullName = otherUser?.let { "${it.firstName} ${it.lastName}" } ?: otherUserId

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isPast) Color(0xFFF0F0F0) else Color.White
        ),
        elevation = if (isPast) CardDefaults.cardElevation(0.dp) else CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (!profileImg.isNullOrBlank()) {
                    Image(
                        painter = rememberAsyncImagePainter(profileImg),
                        contentDescription = "Profile",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(fullName.first().toString(), color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(fullName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Date: ${booking.date}", fontSize = 14.sp, color = Color.Gray)
                    Text("Time: ${booking.timeSlot}", fontSize = 14.sp, color = Color.Gray)

                    if (showCountdown) {
                        val minutesLeft = durationUntilStart.toMinutes().toInt()
                        Text("Starts in $minutesLeft min", fontSize = 12.sp, color = Color.Red)
                    }




                }

                if (!isPast) {
                    val channelName = URLEncoder.encode("lesson_123", "UTF-8")
                    Button(
                        onClick = { navController.navigate("videoCall/$channelName/$otherUserId") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (enableJoinButton) MaterialTheme.colorScheme.primary else Color.Gray
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.defaultMinSize(minHeight = 32.dp)
                    ) {
                        Text("Join Now", fontSize = 13.sp)
                    }
                }



// }

            }





        }

    }

}