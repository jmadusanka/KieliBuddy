package com.example.kielibuddy.ui.pages

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.kielibuddy.R
import com.example.kielibuddy.model.Booking
import com.example.kielibuddy.model.UserModel
import com.example.kielibuddy.model.UserRole
import com.example.kielibuddy.repository.UserRepository
import com.example.kielibuddy.ui.components.BackButton
import com.example.kielibuddy.ui.components.BottomNavigationBar
import com.example.kielibuddy.viewmodel.AuthViewModel
import com.example.kielibuddy.viewmodel.BookingViewModel
import com.google.firebase.auth.FirebaseAuth
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kielibuddy.ui.theme.Purple40
import kotlinx.coroutines.launch
import java.time.LocalTime
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
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(bookings.sortedBy { it.date }) { booking ->
                        BookingCard(booking = booking, viewerRole = roleMode)
                    }
                }
            } else {
                Text("No lessons found.", color = Color.Gray)
            }
        }
    }
}

@Composable
fun BookingCard(booking: Booking, viewerRole: UserRole) {
    val now = LocalTime.now()
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    val startTime = booking.timeSlot.split(" - ").firstOrNull()?.let { LocalTime.parse(it, formatter) }
    val showJoinButton = startTime != null && now.hour == startTime.hour

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
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
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
                Text("${if (viewerRole == UserRole.STUDENT) "Tutor" else "Student"}: $fullName", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Date: ${booking.date}", fontSize = 14.sp, color = Color.Gray)
                Text("Time: ${booking.timeSlot}", fontSize = 14.sp, color = Color.Gray)
            }

            if (showJoinButton) {
                Button(onClick = { /* TODO: link to video call */ }) {
                    Text("Join Now")
                }
            }
        }
    }
}
