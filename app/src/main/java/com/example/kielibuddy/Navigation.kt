package com.example.kielibuddy

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kielibuddy.model.UserRole

import com.example.kielibuddy.ui.pages.ChatScreen
import com.example.kielibuddy.ui.pages.EditableProfilePage
import com.example.kielibuddy.ui.pages.EditableTutorProfilePage
import com.example.kielibuddy.ui.pages.ForgotPasswordPage
import com.example.kielibuddy.ui.pages.HomePage
import com.example.kielibuddy.ui.pages.LoginPage
import com.example.kielibuddy.ui.pages.ProfileScreen
import com.example.kielibuddy.ui.pages.SamplePageGallery
import com.example.kielibuddy.ui.pages.SignupCompletePage

import com.example.kielibuddy.ui.pages.SignupPage
import com.example.kielibuddy.ui.pages.StudentDashBoard
import com.example.kielibuddy.ui.pages.TutorListScreen
import com.example.kielibuddy.ui.pages.WelcomePage
import com.example.kielibuddy.ui.screens.tutor.TutorHomeScreen.TutorDashboard
import com.example.kielibuddy.viewmodel.AuthState
import com.example.kielibuddy.viewmodel.AuthViewModel
import com.example.kielibuddy.viewmodel.ChatViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.kielibuddy.ui.pages.ChatInbox
import com.example.kielibuddy.ui.pages.StudentBookingCalendar
import com.example.kielibuddy.ui.pages.StudentPublicProfileScreen
import com.example.kielibuddy.ui.pages.StudentScheduleScreen
import com.example.kielibuddy.ui.pages.TutorDisplayCalendar
import com.example.kielibuddy.ui.pages.TutorEarningsScreen
import com.example.kielibuddy.ui.pages.VideoCallScreen

@Composable
fun Navigation(modifier: Modifier = Modifier, authViewModel: AuthViewModel, activity: Activity) {
    val navController = rememberNavController()
    val authState by authViewModel.authState.observeAsState()
    val chatViewModel: ChatViewModel = viewModel()
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

//        composable("chatScreen") {
//            StudentChatScreen(navController = navController)
//        }
        composable("profile") {
            ProfileScreen(navController = navController, authViewModel = authViewModel)
        }

        composable("list") {
            TutorListScreen(navController = navController, authViewModel = authViewModel)
        }
        composable("calender") {
            TutorDisplayCalendar(navController = navController)
        }
        //  bottom navigation routes
        composable("search") {
            TutorListScreen(navController = navController, authViewModel = authViewModel)
        }

        composable("tutorCalendar") {
            TutorDisplayCalendar(navController = navController)
        }
        composable("tutorEditProfile") {
            EditableTutorProfilePage(navController = navController, authViewModel = authViewModel)
        }

        composable("StudentPublicProfileScreen") {
            StudentPublicProfileScreen(navController = navController, authViewModel = authViewModel)
        }

        composable("StudentScheduleScreen") {
            StudentScheduleScreen(navController = navController, authViewModel = authViewModel)
        }

        composable("StudentBookingCalendar") {
            StudentBookingCalendar(navController = navController)
        }

        composable("TutorScheduleScreen") {
            StudentScheduleScreen(navController = navController, authViewModel = authViewModel, roleMode = UserRole.TEACHER)
        }


        composable("chat/{receiverId}/{receiverName}") { backStackEntry ->
            val receiverId = backStackEntry.arguments?.getString("receiverId") ?: ""
            val receiverName = backStackEntry.arguments?.getString("receiverName") ?: ""

            ChatScreen(
                navController = navController,
                chatViewModel = chatViewModel,
                receiverId = receiverId,
                receiverName = receiverName,
                receiverRole = UserRole.TEACHER
            )
        }

        composable("inbox") {
            ChatInbox(
                navController = navController,
                chatViewModel = chatViewModel,
                currentUser = authViewModel.userData.value!!
            )
        }

        composable("videoCall/{channelName}") { backStackEntry ->
            val channelName = backStackEntry.arguments?.getString("channelName") ?: ""
            VideoCallScreen(
                navController = navController,
                channelName = channelName,
                appId = "52d90b7c9c4e4416b229514e958b9c74"
            )
        }

        composable("tutorPublicProfile/{tutorId}") { backStackEntry ->
            val tutorId = backStackEntry.arguments?.getString("tutorId") ?: ""
            ProfileScreen(
                navController = navController,
                authViewModel = authViewModel,
                tutorId = tutorId
            )
        }

        composable(
            route = "StudentBooking/{tutorId}?isTrial={isTrial}",
            arguments = listOf(
                navArgument("tutorId") { type = NavType.StringType },
                navArgument("isTrial") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            val tutorId = backStackEntry.arguments?.getString("tutorId")
            val isTrial = backStackEntry.arguments?.getBoolean("isTrial") ?: false

            StudentBookingCalendar(
                navController = navController,
                tutorId = tutorId,
                isTrial = isTrial
            )
        }

        composable("tutorEarnings") {
            TutorEarningsScreen(navController = navController)
        }
    }
}


