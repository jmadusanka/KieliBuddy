package com.example.kielibuddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.kielibuddy.ui.theme.KieliBuddyTheme
import com.example.kielibuddy.viewmodel.AuthViewModel
import com.google.firebase.Firebase
import com.google.firebase.initialize

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Firebase.initialize(this) // Initialize Firebase
        enableEdgeToEdge()

        val authViewModel: AuthViewModel by viewModels() // Initialize AuthViewModel

        setContent {
            KieliBuddyTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Navigation(
                        modifier = Modifier.padding(innerPadding),
                        authViewModel = authViewModel // Pass AuthViewModel to Navigation
                    )
                }
            }
        }
    }
}