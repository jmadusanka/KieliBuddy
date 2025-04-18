package com.example.kielibuddy.ui.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.kielibuddy.model.Booking
import com.example.kielibuddy.model.UserModel
import com.example.kielibuddy.model.UserRole
import com.example.kielibuddy.repository.UserRepository
import com.example.kielibuddy.ui.components.BackButton
import com.example.kielibuddy.ui.components.BottomNavigationBar
import com.example.kielibuddy.viewmodel.AuthViewModel
import com.example.kielibuddy.viewmodel.BookingViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDashBoard(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {
    val userData by authViewModel.userData.observeAsState()
    val tutors = remember { mutableStateOf(emptyList<UserModel>()) }
    val bookingViewModel: BookingViewModel = viewModel()
    val studentBookings by bookingViewModel.studentBookings.collectAsState()

    LaunchedEffect(Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null && userData == null) {
            authViewModel.loadUserData(uid)
            authViewModel.loadUserData(uid)
        }

        val db = FirebaseFirestore.getInstance()
        val result = db.collection("users")
            .whereEqualTo("role", UserRole.TEACHER.name)
            .limit(3)
            .get().await()
            .mapNotNull { it.toObject(UserModel::class.java) }
        tutors.value = result
    }

    LaunchedEffect(userData?.id) {
        userData?.id?.let { id ->
            bookingViewModel.loadStudentBookings(id)
        }
    }

    if (userData == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { paddingValues ->
        Column(modifier = modifier.padding(paddingValues).fillMaxSize()) {
            TopAppBar(
                modifier = Modifier.height(56.dp),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                navigationIcon = {
                    BackButton(navController = navController)
                },
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            "Student Dashboard",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            )

            ProfileSectionWithMenu(userData, navController, authViewModel)

            LazyColumn(modifier = Modifier.fillMaxSize()) {

                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    ProgressSection()
                }

                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row (modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween){
                        Text(
                            "Upcoming Lesson", fontSize = 18.sp,
                            fontWeight = FontWeight.Bold, color = Color.Black,
                            modifier = Modifier.padding(10.dp)
                        )
                        Button(
                            onClick = { navController.navigate("StudentScheduleScreen") },
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


                }

                item {
                    println("studentBookings: $studentBookings")
                    ScheduleSection(navController = navController, bookings = studentBookings)
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Recommended Tutors",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                        )
                        Button(
                            onClick = { navController.navigate("list") },
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
                }

                items(tutors.value) { tutor ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = "${tutor.firstName} ${tutor.lastName}", fontWeight = FontWeight.Bold)
                            Text(text = tutor.aboutMe ?: "", fontSize = 12.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = {
                                navController.navigate("chat/${tutor.id}/${tutor.firstName}")
                            }) {
                                Text("Chat")
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun ProfileSectionWithMenu(user: UserModel?, navController: NavController, authViewModel: AuthViewModel) {
    var menuExpanded by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color(0xFF6A3DE2), RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = rememberAsyncImagePainter(user?.profileImg),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "${user?.firstName ?: ""} ${user?.lastName ?: ""}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Text(
                    text = "${user?.role ?: ""}",
                    fontSize = 12.sp,
                    color = Color.LightGray
                )
            }
        }
        Box {
            IconButton(onClick = { menuExpanded = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = "More Options", tint = Color.White)
            }
            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false },
                offset = DpOffset(x = (-8).dp, y = 4.dp)
            ) {
                DropdownMenuItem(
                    text = { Text("Edit Profile") },
                    onClick = {
                        menuExpanded = false
                        navController.navigate("editProfile")
                    },
                    leadingIcon = { Icon(Icons.Default.Edit, contentDescription = "Edit") }
                )
                DropdownMenuItem(
                    text = { Text("Sign Out") },
                    onClick = {
                        menuExpanded = false
                        authViewModel.signout()
                    },
                    leadingIcon = { Icon(Icons.Default.AccountCircle, contentDescription = "Sign Out") }
                )
                DropdownMenuItem(
                    text = { Text("View Sample Pages") },
                    onClick = {
                        menuExpanded = false
                        navController.navigate("gallery")
                    },
                    leadingIcon = { Icon(Icons.Default.Menu, contentDescription = "Sample page") }
                )

            }
        }
    }
}

@Composable
fun ProgressSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text("Learning Summary", fontSize = 16.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(8.dp))
            Text("You’ve completed 5.5 hours this week", fontSize = 14.sp, color = Color.Gray)
        }
    }
}

@Composable
fun ScheduleSection(navController: NavController, bookings: List<Booking>) {
    val topLessons = bookings.sortedBy { it.date }.take(3)
    val userRepo = remember { UserRepository() }
    val studentMap = remember { mutableStateMapOf<String, UserModel>() }
    val now = LocalTime.now()
    val today = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("HH:mm")

    LaunchedEffect(topLessons) {
        topLessons.forEach { booking ->
            if (!studentMap.containsKey(booking.tutorId)) {
                val tutor = userRepo.getUserDetails(booking.tutorId)
                if (tutor != null) {
                    studentMap[booking.tutorId] = tutor
                }
            }
        }
    }

    Column(modifier = Modifier.padding(horizontal = 10.dp)) {
        topLessons.forEach { booking ->
            val tutor = studentMap[booking.tutorId]
            val isToday = LocalDate.parse(booking.date) == today
            val startTime = booking.timeSlot.split(" - ").firstOrNull()?.let { LocalTime.parse(it, formatter) }
            val minutesUntil = startTime?.let { Duration.between(now, it).toMinutes() } ?: Long.MAX_VALUE
            val showJoinButton = minutesUntil in -5..55

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F3F3)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (!tutor?.profileImg.isNullOrBlank()) {
                            Image(
                                painter = rememberAsyncImagePainter(tutor?.profileImg),
                                contentDescription = "Tutor Image",
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
                                Text(tutor?.firstName?.firstOrNull()?.toString() ?: "T", color = Color.White)
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Lesson with ${tutor?.firstName ?: "Your tutor"}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text("${booking.date}, ${booking.timeSlot}", fontSize = 14.sp, color = Color.Gray)
                            if (isToday) {
                                Text("Today", fontSize = 12.sp, color = Color(0xFF4E2AB3), fontWeight = FontWeight.Bold)
                            }
                            if (minutesUntil in 0..60) {
                                Text("Starts in $minutesUntil min", fontSize = 12.sp, color = Color(0xFF6A3DE2))
                            }
                        }
                    }

                    if (showJoinButton) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { navController.navigate("join_session") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A3DE2)),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text("Join Now", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
