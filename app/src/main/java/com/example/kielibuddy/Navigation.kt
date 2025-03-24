package com.example.kielibuddy

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kielibuddy.ui.pages.ForgotPasswordPage
import com.example.kielibuddy.ui.pages.LoginPage
import com.example.kielibuddy.ui.pages.SignupPage
import com.example.kielibuddy.ui.pages.WelcomePage
import com.example.kielibuddy.viewmodel.AuthViewModel
import com.example.kielibuddy.viewmodel.FakeAuthViewModel

@Composable
fun Navigation(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "welcome",
        builder = {
            // Welcome Page (NEW - Added to NavHost)
            composable("welcome") {
                WelcomePage(
                    modifier = modifier,
                    navController = navController
                )
            }

            // Login Page
            composable("login") {
                LoginPage(
                    modifier = modifier,
                    navController = navController,
                    authViewModel = authViewModel
                )
            }

            // Signup Page
            composable("signup") {
                SignupPage(
                    modifier = modifier,
                    navController = navController,
                    authViewModel = authViewModel
                )
            }

            // Forgot Password Page
            composable("forgotPassword") {
                ForgotPasswordPage(
                    modifier = modifier,
                    navController = navController
                )
            }
        }
    )
}