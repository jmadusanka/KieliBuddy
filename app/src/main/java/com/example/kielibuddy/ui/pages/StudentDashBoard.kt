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
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.kielibuddy.model.UserModel
import com.example.kielibuddy.ui.components.BackButton
import com.example.kielibuddy.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDashBoard(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {
    val userData by authViewModel.userData.observeAsState()

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

    // Main Layout
    Column(modifier = modifier.fillMaxSize()) {
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
                        fontSize = 18.sp
                    )
                }
            }
        )

        ProfileSectionWithMenu(userData, navController, authViewModel)

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { navController.navigate("gallery") },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("View Sample Pages")
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                ProgressSection()
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Upcoming Lesson", fontSize = 18.sp,
                    fontWeight = FontWeight.Bold, color = Color.Black,
                    modifier = Modifier.padding(10.dp)
                )
            }

            item {
                ScheduleSection(navController)
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
                        Text("All")
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
fun ScheduleSection(navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F3F3)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text("Finnish Lesson with John Doe", fontSize = 16.sp, color = Color.Black)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Friday, 3 PM - 4 PM",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
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