package com.example.kielibuddy.ui.screens.tutor.TutorHomeScreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.kielibuddy.model.Booking
import com.example.kielibuddy.model.UserModel
import com.example.kielibuddy.model.UserRole
import com.example.kielibuddy.repository.UserRepository
import com.example.kielibuddy.ui.components.BottomNavigationBar
import com.example.kielibuddy.ui.components.ReviewList
import com.example.kielibuddy.ui.theme.Purple40
import com.example.kielibuddy.viewmodel.AuthViewModel
import com.example.kielibuddy.viewmodel.BookingViewModel
import com.example.kielibuddy.viewmodel.ReviewViewModel
import com.google.firebase.auth.FirebaseAuth
import com.example.kielibuddy.viewmodel.EarningsViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TutorDashboard(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {
    val userData by authViewModel.userData.observeAsState()
    val bookingViewModel: BookingViewModel = viewModel()
    val tutorBookings by bookingViewModel.studentBookings.collectAsState()
    val reviewViewModel = remember { ReviewViewModel() }
    val reviews by reviewViewModel.reviews.collectAsState()
    val earningsViewModel: EarningsViewModel = viewModel()
    val paymentHistory by earningsViewModel.paymentHistory.collectAsState()
    var showMenu by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()


    LaunchedEffect(Unit) {
        FirebaseAuth.getInstance().currentUser?.uid?.let {
            authViewModel.loadUserData(it)
        }
    }

    LaunchedEffect(userData?.id) {
        userData?.id?.let {
            bookingViewModel.loadTutorBookings(it)
            reviewViewModel.loadReviews(it)
            earningsViewModel.loadEarningsForTutor(it)
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
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("Dashboard", color = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Purple40),
                navigationIcon = {
                    IconButton(onClick = { }) {
                        // Left blank intentionally
                    }
                },
                actions = {
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(Icons.Filled.MoreVert, "Menu")
                    }
                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        DropdownMenuItem(
                            text = { Text("Edit Profile") },
                            onClick = {
                                showMenu = false
                                navController.navigate("tutorEditProfile")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("View Sample Pages") },
                            onClick = {
                                showMenu = false
                                navController.navigate("gallery")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Sign out") },
                            onClick = {
                                showMenu = false
                                authViewModel.signout()
                            }
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController, userRole = userData?.role ?: UserRole.TEACHER)
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
            val averageRating = if (reviews.isNotEmpty()) {
                "%.1f".format(reviews.map { it.rating }.average()) + "/5 ⭐"
            } else {
                "No Rating"
            }

            TutorProfile(
                user = userData,
                languages = userData?.languagesSpoken ?: emptyList(),
                rating = averageRating
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { navController.navigate("profile") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4E2AB3))
            ) {
                Text("View Public Profile", color = Color.White)
            }


            Button(
                onClick = { navController.navigate("tutorEarnings") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4E2AB3))
            ) {
                Text(text = "View Earnings", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))


//            Button(
//                onClick = { navController.navigate("videoCall/lesson123") },
//                modifier = Modifier.fillMaxWidth(),
//                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4E2AB3))
//            ) {
//                Text("Join Video", color = Color.White)
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))

            val totalEarnings = paymentHistory.sumOf { it.amount }
            val totalStudents = paymentHistory.map { it.studentName }.distinct().count()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                InfoCard(
                    title = "Earnings",
                    value = "€${"%.2f".format(totalEarnings)}",
                    textColor = Color(0xFF6A3DE2)
                )
                InfoCard(
                    title = "Students",
                    value = "$totalStudents Students",
                    textColor = Color(0xFF6A3DE2)
                )
            }


            Spacer(modifier = Modifier.height(16.dp))

            UpcomingLessonsCard(lessons = tutorBookings, navController = navController)

            Spacer(modifier = Modifier.height(16.dp))

            ReviewList(reviews = reviews)
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
        modifier = Modifier.fillMaxSize()
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
fun UpcomingLessonsCard(lessons: List<Booking>, navController: NavController) {
    val topLessons = lessons.sortedBy { it.date }.take(2)
    val userRepo = remember { UserRepository() }
    val studentMap = remember { mutableStateMapOf<String, UserModel>() }

    LaunchedEffect(topLessons) {
        topLessons.forEach { booking ->
            if (!studentMap.containsKey(booking.studentId)) {
                val student = userRepo.getUserDetails(booking.studentId)
                if (student != null) {
                    studentMap[booking.studentId] = student
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Upcoming Lessons",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Button(
                onClick = { navController.navigate("TutorScheduleScreen") },
                shape = RoundedCornerShape(50),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.LightGray,
                    contentColor = Color.Black
                )
            ) {
                Text("View All")
            }
        }

        if (topLessons.isEmpty()) {
            Text("No upcoming lessons scheduled.", color = Color.Gray, modifier = Modifier.padding(16.dp))
        } else {
            topLessons.forEach { booking ->
                val student = studentMap[booking.studentId]
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F3F3)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (!student?.profileImg.isNullOrBlank()) {
                                Image(
                                    painter = rememberAsyncImagePainter(student?.profileImg),
                                    contentDescription = "Student Image",
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color.LightGray)
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color.Gray),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(student?.firstName?.firstOrNull()?.toString() ?: "S", color = Color.White)
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = student?.let { "Lesson with ${it.firstName}" } ?: booking.studentId,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(text = "${booking.date}, ${booking.timeSlot}", fontSize = 14.sp, color = Color.Gray)
                            }
                        }
                    }
                }
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