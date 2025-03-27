
package com.example.kielibuddy.ui.pages.students

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.kielibuddy.R

@Composable
fun TutorListScreen(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Find Your Tutor",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6A3DE2)
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(getTutors()) { tutor ->
                TutorCard(
                    tutor = tutor,
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun TutorCard(
    tutor: Tutor,
    navController: NavController
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                contentDescription = "Tutor Image",
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = tutor.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                }
                Text(text = tutor.charges, fontSize = 14.sp, color = Color.Black)
                Text(text = tutor.introduction, fontSize = 12.sp, color = Color.Gray)
                Text(text = "${tutor.students} students • ${tutor.lessons} lessons", fontSize = 12.sp, color = Color.Gray)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "⭐ ${tutor.rating}", fontSize = 12.sp, color = Color.Black)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${tutor.reviews} reviews",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.clickable { navController.navigate("reviews/${tutor.name}") }
                    )
                }
            }
        }
    }
}

data class Tutor(
    val name: String,
    val charges: String,
    val students: Int,
    val lessons: Int,
    val rating: Double,
    val reviews: Int,
    val introduction: String
)

fun getTutors(): List<Tutor> {
    return listOf(
        Tutor(name = "John Doe", charges = "€25", students = 30, lessons = 1804, rating = 5.0, reviews = 22,  introduction = "Native Finnish speaker with years of tutoring experience."),
        Tutor(name = "Jane Smith", charges = "€17", students = 17, lessons = 291, rating = 5.0, reviews = 3,  introduction = "An inspired teacher willing to help with your Finnish!"),
        Tutor(name = "Michael Lee", charges = "€30", students = 25, lessons = 1500, rating = 4.8, reviews = 18,  introduction = "Experienced in teaching Finnish with a friendly approach.")
    )
}