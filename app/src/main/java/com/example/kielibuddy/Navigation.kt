package com.example.kielibuddy

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kielibuddy.ui.Tutor.TutorDisplayCalendar
import com.example.kielibuddy.ui.pages.EditableProfilePage
import com.example.kielibuddy.ui.pages.EditableTutorProfilePage
import com.example.kielibuddy.ui.pages.ForgotPasswordPage
import com.example.kielibuddy.ui.pages.HomePage
import com.example.kielibuddy.ui.pages.LoginPage
import com.example.kielibuddy.ui.pages.ProfileScreen
import com.example.kielibuddy.ui.pages.SamplePageGallery
import com.example.kielibuddy.ui.pages.SignupCompletePage

import com.example.kielibuddy.ui.pages.SignupPage
import com.example.kielibuddy.ui.pages.StudentChatScreen
import com.example.kielibuddy.ui.pages.StudentDashBoard
import com.example.kielibuddy.ui.pages.TutorListScreen
import com.example.kielibuddy.ui.pages.WelcomePage
import com.example.kielibuddy.ui.screens.tutor.TutorHomeScreen.TutorDashboard
import com.example.kielibuddy.viewmodel.AuthState
import com.example.kielibuddy.viewmodel.AuthViewModel

@Composable
fun Navigation(modifier: Modifier = Modifier, authViewModel: AuthViewModel, activity: Activity) {
    val navController = rememberNavController()
    val authState by authViewModel.authState.observeAsState()
    // Call checkAuthStatus when app starts
    LaunchedEffect(Unit) {
        authViewModel.checkAuthStatus(navController)
    }

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Unauthenticated -> {
                navController.navigate("login") {
                    popUpTo(0) { inclusive = true }
                }
            }
            else -> Unit
        }
    }

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
        composable("completeSignup") {
            SignupCompletePage(navController = navController, authViewModel = authViewModel)
        }

        composable("studentHome") {
            StudentDashBoard(navController = navController, authViewModel = authViewModel)
        }

        composable("tutorHome") {
            TutorDashboard(navController = navController, authViewModel = authViewModel)
        }
        composable("editProfile") {
            EditableProfilePage(navController = navController, authViewModel = authViewModel)
        }
        composable("gallery") {
            SamplePageGallery(navController = navController)
        }

        composable("chatScreen") {
            StudentChatScreen(navController = navController)
        }
        composable("profile") {
            ProfileScreen(navController = navController, authViewModel = authViewModel)
        }

        composable("list") {
            TutorListScreen(navController = navController)
        }
        composable("calender") {
            TutorDisplayCalendar(navController = navController)
        }
        //  bottom navigation routes
        composable("search") {
            TutorListScreen(navController = navController)
        }
        composable("studentChat") {
            StudentChatScreen(navController = navController)
        }
        composable("tutorCalendar") {
            TutorDisplayCalendar(navController = navController)
        }
        composable("tutorEditProfile") {
            EditableTutorProfilePage(navController = navController, authViewModel = authViewModel)
        }
    }
}