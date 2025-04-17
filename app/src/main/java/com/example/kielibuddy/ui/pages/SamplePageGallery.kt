package com.example.kielibuddy.ui.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun SamplePageGallery(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text("📱 Sample UI Gallery", fontSize = 24.sp, modifier = Modifier.padding(bottom = 16.dp))

        val pageList = listOf(
            "Home Page" to "home",
            "Student Dashboard" to "studentHome",
            "Tutor Dashboard" to "tutorHome",
            "StudentChatScreen" to "chatScreen",
            "Profile" to "profile",
            "List" to "list",
            "calender" to "calender",
            "StudentPublicProfileScreen" to "StudentPublicProfileScreen",
            "StudentScheduleScreen" to "StudentScheduleScreen",
            "StudentBookingCalendar" to "StudentBookingCalendar",


        )

        pageList.forEach { (title, route) ->
            Text(
                text = title,
                fontSize = 18.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable { navController.navigate(route) }
            )
            Divider()
        }
    }
}
