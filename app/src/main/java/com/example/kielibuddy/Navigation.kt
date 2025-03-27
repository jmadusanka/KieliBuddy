package com.example.kielibuddy

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kielibuddy.ui.pages.ForgotPasswordPage
import com.example.kielibuddy.ui.pages.HomePage
import com.example.kielibuddy.ui.pages.LoginPage

import com.example.kielibuddy.ui.pages.SignupPage
import com.example.kielibuddy.ui.pages.WelcomePage
import com.example.kielibuddy.viewmodel.AuthViewModel

@Composable
fun Navigation(modifier: Modifier = Modifier, authViewModel: AuthViewModel, activity: Activity) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "welcome") {
        composable("welcome") {
            WelcomePage(navController = navController)
        }
        composable("login") {
            LoginPage(modifier, navController, authViewModel, activity)
        }
        composable("signup") {
            SignupPage(modifier, navController, authViewModel)
        }

        composable("forgotPassword") {
            ForgotPasswordPage(navController = navController, authViewModel = authViewModel)
        }
        composable("home") {
            HomePage(modifier, navController, authViewModel)
        }
    }
}