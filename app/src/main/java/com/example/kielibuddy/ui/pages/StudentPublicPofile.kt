package com.example.kielibuddy.ui.pages

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.kielibuddy.model.UserModel
import com.example.kielibuddy.model.UserRole
import com.example.kielibuddy.ui.components.BackButton
import com.example.kielibuddy.ui.components.BottomNavigationBar
import com.example.kielibuddy.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

// Define a color palette for consistent design
private val primaryColor = Color(0xFF6A3DE2)
private val secondaryColor = Color(0xFF9E8CF7)
private val backgroundLight = Color(0xFFF7F5FE)
private val accentColor = Color(0xFFFF9800)
private val textPrimary = Color(0xFF333333)
private val textSecondary = Color(0xFF666666)
private val dividerColor = Color(0xFFEEEEEE)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentPublicProfileScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val userData = remember {
        mutableStateOf(
            UserModel(
                id = "dummyId",
                firstName = "John",
                lastName = "Doe",
                role = UserRole.STUDENT,
                profileImg = "https://via.placeholder.com/150",
                aboutMe = "A passionate language learner eager to improve my skills.",
                nativeLanguage = "English",
                languageProficiency = "Beginner in Spanish",
                focusAreas = "Speaking and listening comprehension",
                background = "University student with an interest in linguistics.",
                lessonPrefs = "Prefer 30-minute sessions twice a week.",
                completedLessons = 5
            )
        )
    }

    val loading = remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        delay(100) // Simulate a short loading time
        loading.value = false
    }

    if (loading.value) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = primaryColor)
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = textPrimary,
                    navigationIconContentColor = primaryColor
                ),
                navigationIcon = {
                    BackButton(navController = navController)
                },
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Student Profile",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }
            )
        },
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(backgroundLight),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                ProfileHeaderSection(userData.value, navController, authViewModel)
            }
            item {
                StudentStatsCard(userData.value)
            }
            item {
                StudentInfoSection(userData.value)
            }
        }
    }
}

@Composable
fun ProfileHeaderSection(user: UserModel, navController: NavController, authViewModel: AuthViewModel) {
    var menuExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(primaryColor)
            .padding(top = 24.dp, bottom = 32.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            // Profile Image
            Image(
                painter = rememberAsyncImagePainter(user.profileImg),
                contentDescription = "Profile Picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .border(3.dp, Color.White, CircleShape)
                    .align(Alignment.Center)
            )

            // Menu Button
            IconButton(
                onClick = { menuExpanded = true },
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = "More Options",
                    tint = Color.White
                )
            }

            // Dropdown Menu
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
                    leadingIcon = { Icon(Icons.Default.ExitToApp, contentDescription = "Sign Out") }
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

        Spacer(modifier = Modifier.height(16.dp))

        // Name and Role
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "${user.firstName} ${user.lastName}",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = user.role.toString(),
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun StudentStatsCard(user: UserModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = (-20).dp)
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
                icon = Icons.Outlined.Edit,
                value = "${user.completedLessons}",
                label = "Lessons"
            )

            Divider(
                modifier = Modifier
                    .height(40.dp)
                    .width(1.dp),
                color = dividerColor
            )

            StatItem(
                icon = Icons.Outlined.AddCircle,
                value = user.nativeLanguage.toString(),
                label = "Native"
            )

            Divider(
                modifier = Modifier
                    .height(40.dp)
                    .width(1.dp),
                color = dividerColor
            )

            StatItem(
                icon = Icons.Outlined.AccountBox,
                value = user.languageProficiency.toString(),
                label = "Learning"
            )
        }
    }
}

@Composable
fun StatItem(icon: androidx.compose.ui.graphics.vector.ImageVector, value: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = primaryColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = textPrimary
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = textSecondary
        )
    }
}

@Composable
fun StudentInfoSection(user: UserModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // About Me Section with icon
            InfoBlockWithIcon(
                icon = Icons.Outlined.Person,
                title = "About Me",
                content = user.aboutMe
            )

            // Language Focus
            InfoBlockWithIcon(
                icon = Icons.Outlined.Person,
                title = "Areas of Focus",
                content = user.focusAreas
            )

            // Background
            InfoBlockWithIcon(
                icon = Icons.Outlined.Edit,
                title = "Background & Interests",
                content = user.background
            )

            // Lesson Preferences
            InfoBlockWithIcon(
                icon = Icons.Outlined.Menu,
                title = "Lesson Preferences",
                content = user.lessonPrefs
            )
        }
    }
}

@Composable
fun InfoBlockWithIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    content: String?
) {
    if (!content.isNullOrBlank()) {
        var expanded by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .animateContentSize()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = primaryColor,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = textPrimary,
                    modifier = Modifier.weight(1f)
                )
                if (content.length > 80) {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                            contentDescription = if (expanded) "Show less" else "Show more",
                            tint = primaryColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = content,
                fontSize = 14.sp,
                color = textSecondary,
                maxLines = if (expanded || content.length <= 80) Int.MAX_VALUE else 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (title != "Lesson Preferences") {
                Divider(color = dividerColor)
            }
        }
    }
}