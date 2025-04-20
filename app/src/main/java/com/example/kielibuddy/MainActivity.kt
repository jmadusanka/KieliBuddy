package com.example.kielibuddy

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.kielibuddy.ui.theme.KieliBuddyTheme
import com.example.kielibuddy.viewmodel.AuthViewModel
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val authViewModel: AuthViewModel by viewModels()
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            //println("FCM Initial token: $token")
        }

        setContent {
            KieliBuddyTheme {
                navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Navigation(
                        modifier = Modifier.padding(innerPadding),
                        authViewModel = authViewModel,
                        activity = this,
                        navController = navController
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val data = intent?.data
        if (data?.scheme == "kielibuddy") {
            when (data.host) {
                "payment-success" -> navController.navigate("payment_success")
                "payment-cancelled" -> navController.navigate("payment_cancelled")
            }
        }
    }
}