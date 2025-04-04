package com.example.kielibuddy.ui.pages

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.kielibuddy.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage

@Composable
fun EditableProfilePage(navController: NavController, authViewModel: AuthViewModel) {
    val userData by authViewModel.userData.observeAsState()
    val context = LocalContext.current

    var firstName by remember { mutableStateOf(TextFieldValue(userData?.firstName ?: "")) }
    var lastName by remember { mutableStateOf(TextFieldValue(userData?.lastName ?: "")) }
    var selectedRole by remember { mutableStateOf(userData?.role?.name ?: "STUDENT") }
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = rememberAsyncImagePainter(userData?.profileImg),
            contentDescription = "Profile Image",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .clickable(enabled = !uploading) { launcher.launch("image/*") }
        )

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

        Text("Role")
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { selectedRole = "STUDENT" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedRole == "STUDENT") Color(0xFF6A3DE2) else Color.Gray
                )
            ) { Text("Student") }

            Button(
                onClick = { selectedRole = "TEACHER" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedRole == "TEACHER") Color(0xFF6A3DE2) else Color.Gray
                )
            ) { Text("Tutor") }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                authViewModel.updateUserProfile(
                    firstName.text,
                    lastName.text,
                    selectedRole,
                    onSuccess = {
                        Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                        navController.popBackStack() // or nav to another screen
                    },
                    onFailure = {
                        Toast.makeText(context, "Failed to update profile", Toast.LENGTH_SHORT).show()
                    }
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Changes")
        }
    }
}