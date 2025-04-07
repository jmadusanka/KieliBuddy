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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.kielibuddy.R
import com.example.kielibuddy.ui.screens.tutor.TutorHomeScreen.ReviewsCard
import com.example.kielibuddy.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profile",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Scrollable content
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp)
            ) {
                item {
                    // 📹 Video segment at the top
                    AndroidView(
                        factory = { context ->
                            VideoView(context).apply {
                                // setVideoURI(Uri.parse("android.resource://${context.packageName}/${R.raw.sample_video}"))
                                setOnPreparedListener { it.isLooping = true }
                                start()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 🖼️ Profile Picture
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Image(
                            painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(70.dp)
                                .clip(CircleShape)
                                .border(2.dp, Color.Gray, CircleShape),
                            alignment = Alignment.TopStart
                        )

                        Text(
                            text = "Jenu johm",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(thickness = 1.dp, color = Color.LightGray)

                    // Stats section
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Rating
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "4.2/5 ★",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Rating",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        // Price
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "€5",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Per hour",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        // Hours
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "27",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Hours",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        // Students
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "11",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Students",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
                    Spacer(modifier = Modifier.height(16.dp))

                    // About me section
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "About me",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Lorem Ipsum has been the industry's standard dummy text ever since the 1500s...",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Start
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Languages section
                        Text(
                            text = "I speak",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Finnish\nEnglish\nSwedish",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Button(
                        onClick = { /* Handle view schedule */ },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(1.dp, Color.Gray),
                        interactionSource = remember { MutableInteractionSource() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    ) {
                        Text(
                            text = "See my schedule",
                            color = Color.Black
                        )
                    }

                    // Reviews card
                    ReviewsCard(
                        reviews = listOf(
                            "Great tutor! Helped me improve my Finnish quickly. ⭐⭐⭐⭐⭐",
                            "Very patient and knowledgeable. Highly recommend! ⭐⭐⭐⭐⭐",
                            "Fun lessons and great explanations. ⭐⭐⭐⭐"
                        )
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { /* Handle chat */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.LightGray
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Chat",
                        color = Color.Black
                    )
                }

                Button(
                    onClick = { /* Handle full lesson */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6A3DE2)
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Buy a trial lesson",
                        color = Color.White
                    )
                }
            }
        }
    }
}