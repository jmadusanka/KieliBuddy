package com.example.kielibuddy.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kielibuddy.model.Review

@Composable
fun ReviewList(reviews: List<Review>) {
    Column(modifier = Modifier.fillMaxWidth().padding(top = 24.dp)) {
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
                        Text("⭐".repeat(review.rating), style = MaterialTheme.typography.bodyLarge)
                        Text(review.text, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
