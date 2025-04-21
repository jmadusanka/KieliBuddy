package com.example.kielibuddy.ui.pages

import android.net.Uri
import android.widget.VideoView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.kielibuddy.model.UserModel
import com.example.kielibuddy.ui.components.ReviewForm
import com.example.kielibuddy.ui.components.ReviewList
import com.example.kielibuddy.ui.theme.Purple40
import com.example.kielibuddy.viewmodel.AuthViewModel
import com.example.kielibuddy.viewmodel.EarningsViewModel
import com.example.kielibuddy.viewmodel.ReviewViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    tutorId: String? = null
) {
    val reviewViewModel = remember { ReviewViewModel() }
    val earningsViewModel: EarningsViewModel = viewModel()
    var userData by remember { mutableStateOf<UserModel?>(null) }

    LaunchedEffect(tutorId) {
        if (tutorId != null) {
            val snapshot = FirebaseFirestore.getInstance()
                .collection("users")
                .document(tutorId)
                .get()
                .await()
            userData = snapshot.toObject(UserModel::class.java)
        } else {
            userData = authViewModel.userData.value
        }
    }

    LaunchedEffect(userData?.id) {
        userData?.id?.let {
            reviewViewModel.loadReviews(it)
            earningsViewModel.loadEarningsForTutor(it)
        }
    }

    val reviews by reviewViewModel.reviews.collectAsState()
    val paymentHistory by earningsViewModel.paymentHistory.collectAsState()

    if (userData == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val rating = if (reviews.isNotEmpty()) "%.1f/5 ★".format(reviews.map { it.rating }.average()) else "No Rating"
    val totalEarnings = paymentHistory.sumOf { it.amount } / 100.0
    val totalHours = paymentHistory.sumOf { it.hours }
    val totalStudents = paymentHistory.map { it.studentId }.distinct().count()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("Profile", color = Color.White)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = { Spacer(modifier = Modifier.width(48.dp)) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Purple40)
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(bottom = 80.dp)) {
                item {
                    if (!userData?.introVideoUrl.isNullOrEmpty()) {
                        AndroidView(
                            factory = { context ->
                                VideoView(context).apply {
                                    setVideoURI(Uri.parse(userData!!.introVideoUrl))
                                    setOnPreparedListener { it.isLooping = true }
                                    start()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(200.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(userData?.profileImg),
                            contentDescription = "Profile Picture",
                            modifier = Modifier.size(70.dp).clip(CircleShape).border(2.dp, Color.Gray, CircleShape),
                            contentScale = ContentScale.Crop
                        )

                        Text(
                            text = "${userData?.firstName} ${userData?.lastName}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    HorizontalDivider(thickness = 1.dp, color = Color.LightGray)

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                            Text(text = rating, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                            Text(text = "Rating", style = MaterialTheme.typography.bodySmall)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                            Text(text = "€${userData?.price50Min ?: 0}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                            Text(text = "Per hour", style = MaterialTheme.typography.bodySmall)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                            Text(text = "${"%.1f".format(totalHours)}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                            Text(text = "Hours", style = MaterialTheme.typography.bodySmall)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                            Text(text = "$totalStudents", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                            Text(text = "Students", style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
                    Spacer(modifier = Modifier.height(16.dp))

                    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp), horizontalAlignment = Alignment.Start) {
                        Text(text = "About me", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = userData?.aboutMe ?: "", style = MaterialTheme.typography.bodyMedium)

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(text = "I speak", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = userData?.languagesSpoken?.joinToString("\n") ?: "", style = MaterialTheme.typography.bodyMedium)
                    }

                    Button(
                        onClick = { navController.navigate("StudentBooking/${tutorId}") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(1.dp, Color.Gray),
                        interactionSource = remember { MutableInteractionSource() },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                    ) {
                        Text("See my schedule")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    userData?.id?.let { tutorId ->
                        ReviewForm(tutorId = tutorId, reviewViewModel = reviewViewModel)
                        ReviewList(reviews = reviews)
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter).padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { navController.navigate("chat/${tutorId}/${userData?.firstName}") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Chat", color = Color.Black)
                }

                Button(
                    onClick = { navController.navigate("StudentBooking/${tutorId}?isTrial=true") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A3DE2)),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Try a Free Lesson", color = Color.White)
                }
            }
        }
    }
}
