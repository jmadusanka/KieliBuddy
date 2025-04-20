package com.example.kielibuddy

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.kielibuddy.model.UserRole
import com.example.kielibuddy.ui.pages.*
import com.example.kielibuddy.ui.screens.tutor.TutorHomeScreen.TutorDashboard
import com.example.kielibuddy.viewmodel.AuthState
import com.example.kielibuddy.viewmodel.AuthViewModel
import com.example.kielibuddy.viewmodel.ChatViewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument

@Composable
fun Navigation(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel,
    activity: Activity,
    navController: NavHostController
) {
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
        composable("profile") {
            ProfileScreen(navController = navController, authViewModel = authViewModel)
        }
        composable("list") {
            TutorListScreen(navController = navController, authViewModel = authViewModel)
        }
        composable("calender") {
            TutorDisplayCalendar(navController = navController)
        }
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
        composable("payment_success") {
            PaymentSuccessScreen(navController)
        }
        composable("payment_cancelled") {
            PaymentCancelledScreen(navController)
        }
    }
}
