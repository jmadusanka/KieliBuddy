package com.example.kielibuddy.ui.pages

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.livedata.observeAsState
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Edit
import androidx.compose.ui.draw.clip
import coil.compose.rememberAsyncImagePainter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.navigation.NavController
import com.example.kielibuddy.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.util.UnstableApi

import androidx.media3.ui.PlayerView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.compose.ui.viewinterop.AndroidView
import android.graphics.Color as AndroidColor

@OptIn(UnstableApi::class)
@kotlin.OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditableTutorProfilePage(navController: NavController, authViewModel: AuthViewModel) {
    val userData by authViewModel.userData.observeAsState()
    val context = LocalContext.current

    var uploading by remember { mutableStateOf(false) }

    var firstName by remember { mutableStateOf(TextFieldValue(userData?.firstName ?: "")) }
    var lastName by remember { mutableStateOf(TextFieldValue(userData?.lastName ?: "")) }
    var aboutMe by remember { mutableStateOf(TextFieldValue(userData?.aboutMe ?: "")) }
    var country by remember { mutableStateOf(TextFieldValue(userData?.countryOfBirth ?: "")) }
    var price60 by remember { mutableStateOf(TextFieldValue(userData?.price50Min?.toInt()?.toString() ?: "")) }
    var languages by remember { mutableStateOf(TextFieldValue(userData?.languagesSpoken?.joinToString(", ") ?: "")) }
    var birthday by remember { mutableStateOf(TextFieldValue(userData?.birthday ?: "")) }

    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    var introVideoUrl by remember { mutableStateOf(userData?.introVideoUrl ?: "") }

    val videoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            uploading = true
            val videoRef = FirebaseStorage.getInstance().reference.child("introVideos/$uid.mp4")
            videoRef.putFile(uri).addOnSuccessListener {
                videoRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    introVideoUrl = downloadUrl.toString()
                    authViewModel.updateIntroVideo(downloadUrl.toString())
                    uploading = false
                    Toast.makeText(context, "Intro video uploaded", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                uploading = false
                Toast.makeText(context, "Failed to upload video", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            uploading = true
            val storageRef = FirebaseStorage.getInstance().reference.child("profileImages/$uid.jpg")
            storageRef.putFile(uri)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                        authViewModel.updateProfileImage(downloadUrl.toString())
                        authViewModel.updateUserProfileImageOnly(downloadUrl.toString())
                        uploading = false
                        Toast.makeText(context, "Profile image updated", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    uploading = false
                    Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show()
                }
        }
    }

    val scrollState = rememberScrollState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Edit Profile", color = Color.White)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF6A3DE2)
                )
            )
        },
        containerColor = Color(0xE6E0F5FF)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.size(110.dp), contentAlignment = Alignment.BottomEnd) {
                Image(
                    painter = rememberAsyncImagePainter(userData?.profileImg),
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .clickable(enabled = !uploading) { launcher.launch("image/*") }
                )
                IconButton(
                    onClick = { launcher.launch("image/*") },
                    modifier = Modifier
                        .size(28.dp)
                        .offset(x = (-4).dp, y = (-4).dp)
                        .background(Color.White, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("First Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Last Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = aboutMe,
                onValueChange = { aboutMe = it },
                label = { Text("About Me") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = country,
                onValueChange = { country = it },
                label = { Text("Country of Birth") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (introVideoUrl.isNotEmpty()) {
                Text("Current Intro Video:", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                val exoPlayer = remember(context) {
                    ExoPlayer.Builder(context).build().apply {
                        setMediaItem(MediaItem.fromUri(introVideoUrl))
                        prepare()
                        playWhenReady = false
                    }
                }
                DisposableEffect(Unit) {
                    onDispose { exoPlayer.release() }
                }
                AndroidView(
                    factory = {
                        PlayerView(it).apply {
                            this.player = exoPlayer
                            this.useController = true
                            this.setShutterBackgroundColor(AndroidColor.TRANSPARENT)
                        }
                    }, modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }

            Button(
                onClick = { videoLauncher.launch("video/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Upload Intro Video")
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = price60,
                onValueChange = { price60 = it },
                label = { Text("Price for 60 min") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = languages,
                onValueChange = { languages = it },
                label = { Text("Languages Spoken (comma-separated)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = birthday,
                onValueChange = { birthday = it },
                label = { Text("Birthday (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = {
                val updates = mapOf(
                    "firstName" to firstName.text,
                    "lastName" to lastName.text,
                    "aboutMe" to aboutMe.text,
                    "countryOfBirth" to country.text,
                    "introVideoUrl" to introVideoUrl,
                    "price50Min" to (price60.text.toIntOrNull() ?: 0) as Any,
                    "languagesSpoken" to languages.text.split(",").map { it.trim() },
                    "birthday" to birthday.text
                )
                authViewModel.updateTutorProfileFields(updates,
                    onSuccess = {
                        Toast.makeText(context, "Profile updated", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    },
                    onFailure = {
                        Toast.makeText(context, "Failed to update", Toast.LENGTH_SHORT).show()
                    })
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Save Changes")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { navController.navigate("profile") },
                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancel", color = Color.Black)
            }

        }
    }
}
