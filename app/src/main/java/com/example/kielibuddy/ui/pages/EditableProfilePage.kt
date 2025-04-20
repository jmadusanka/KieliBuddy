package com.example.kielibuddy.ui.pages

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.kielibuddy.ui.components.BackButton
import com.example.kielibuddy.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditableProfilePage(navController: NavController, authViewModel: AuthViewModel) {
    val userData by authViewModel.userData.observeAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var firstName by remember { mutableStateOf(TextFieldValue(userData?.firstName ?: "")) }
    var lastName by remember { mutableStateOf(TextFieldValue(userData?.lastName ?: "")) }
    var email by remember { mutableStateOf(TextFieldValue(userData?.email ?: "")) }
    var aboutMe by remember { mutableStateOf(TextFieldValue(userData?.aboutMe ?: "")) }
    var languageProficiency by remember { mutableStateOf(TextFieldValue(userData?.langLevel?.joinToString(", ") ?: "")) }
    var birthDate by remember { mutableStateOf(TextFieldValue(userData?.birthDate ?: "")) }
    var uploading by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            uploading = true
            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@let
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Edit Profile",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    BackButton(navController = navController)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF6A3DE2),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Image with Edit Pencil
            Box(
                modifier = Modifier.size(120.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Image(
                    painter = rememberAsyncImagePainter(userData?.profileImg),
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                        .clickable(enabled = !uploading) { launcher.launch("image/*") },
                    contentScale = ContentScale.Crop //  Ensures proper fill
                )
                IconButton(
                    onClick = { launcher.launch("image/*") },
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFF6A3DE2), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Personal Information
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
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                enabled = false
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = birthDate,
                onValueChange = { birthDate = it },
                label = { Text("Date of Birth (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Additional Information
            OutlinedTextField(
                value = languageProficiency,
                onValueChange = { languageProficiency = it },
                label = { Text("Language Proficiency (comma separated)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = aboutMe,
                onValueChange = { aboutMe = it },
                label = { Text("About Me") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Save Button
            Button(
                onClick = {
                    authViewModel.updateUserProfile(
                        firstName.text,
                        lastName.text,
                        userData?.role?.name ?: "STUDENT",
                        onSuccess = {
                            val updates = mapOf(
                                "aboutMe" to aboutMe.text,
                                "langLevel" to languageProficiency.text.split(",").map { it.trim() },
                                "birthDate" to birthDate.text
                            )
                            authViewModel.updateTutorProfileFields(
                                updates,
                                onSuccess = {
                                    Toast.makeText(context, "Profile updated", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                },
                                onFailure = {
                                    Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show()
                                }
                            )
                        },
                        onFailure = {
                            Toast.makeText(context, "Failed to update", Toast.LENGTH_SHORT).show()
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A3DE2))
            ) {
                Text("Save Changes", color = Color.White)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}