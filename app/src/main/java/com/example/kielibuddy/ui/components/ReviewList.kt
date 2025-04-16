package com.example.kielibuddy.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.kielibuddy.model.Review
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ReviewList(reviews: List<Review>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
    ) {
        Text(
            text = "Student Reviews",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (reviews.isEmpty()) {
            Text("No reviews yet.")
        } else {
            reviews.forEach { review ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {

                        // 🕒 Timestamp
                        Text(
                            text = formatTimestamp(review.timestamp),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        // 👤 Student Name
                        if (review.studentName.isNotEmpty()) {
                            Text(
                                text = "By ${review.studentName}",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.Gray,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }


                        // ⭐ Only filled stars
                        Row(modifier = Modifier.padding(bottom = 4.dp)) {
                            repeat(review.rating) {
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFFFC107),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // 💬 Comment
                        Text(review.comment, style = MaterialTheme.typography.bodyMedium)

                    }
                }
            }
        }
    }
}

fun formatTimestamp(timestamp: Long): String {
    val date = Date(timestamp)
    val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return formatter.format(date)
}
