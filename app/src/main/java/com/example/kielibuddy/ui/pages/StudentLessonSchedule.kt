package com.example.kielibuddy.ui.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.example.kielibuddy.ui.components.BackButton
import com.example.kielibuddy.ui.components.BottomNavigationBar
import com.example.kielibuddy.viewmodel.AuthViewModel
import com.example.kielibuddy.viewmodel.BookingViewModel
import com.google.firebase.auth.FirebaseAuth
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kielibuddy.repository.UserRepository
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentScheduleScreen(navController: NavController, authViewModel: AuthViewModel) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userData by authViewModel.userData.observeAsState()
    val bookingViewModel: BookingViewModel = viewModel()
    val bookings by bookingViewModel.studentBookings.collectAsState()

    LaunchedEffect(currentUser?.uid) {
        currentUser?.uid?.let {
            authViewModel.loadUserData(it)
            bookingViewModel.loadStudentBookings(it)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    BackButton(navController = navController)
                },
                title = {
                    Text("My Bookings", fontWeight = FontWeight.Bold, fontSize = 24.sp)
                },
                actions = {
                    userData?.profileImg?.let { imageUrl ->
                        Image(
                            painter = rememberAsyncImagePainter(imageUrl),
                            contentDescription = "Profile",
                            modifier = Modifier
                                .size(84.dp)
                                .padding(end = 16.dp, top = 16.dp)
                        )
                    } ?: run {
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
            if (bookings.isNotEmpty()) {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(bookings.sortedBy { it.date }) { booking ->
                        BookingCard(booking = booking)
                    }
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_messages),
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
                            navController.navigate("list")
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
fun BookingCard(booking: Booking) {
    val now = LocalTime.now()
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    val startTime = booking.timeSlot.split(" - ").firstOrNull()?.let { LocalTime.parse(it, formatter) }
    val showJoinButton = startTime != null && now.hour == startTime.hour

    var tutor by remember { mutableStateOf<UserModel?>(null) }
    val userRepo = remember { UserRepository() }

    LaunchedEffect(booking.tutorId) {
        tutor = userRepo.getUserDetails(booking.tutorId)
    }

    val tutorImage = tutor?.profileImg
    val tutorName = tutor?.let { "${it.firstName} ${it.lastName}" } ?: booking.tutorId

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!tutorImage.isNullOrBlank()) {
                Image(
                    painter = rememberAsyncImagePainter(tutorImage),
                    contentDescription = "Tutor Image",
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
                    Text(tutorName.first().toString(), color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text("$tutorName", fontWeight = FontWeight.Bold, fontSize = 16.sp)
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