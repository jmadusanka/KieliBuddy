package com.example.kielibuddy.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.kielibuddy.model.Review
import com.example.kielibuddy.viewmodel.ReviewViewModel
import com.google.firebase.auth.FirebaseAuth
import java.util.*

@Composable
fun ReviewForm(
    tutorId: String,
    reviewViewModel: ReviewViewModel,
    onReviewSubmit: () -> Unit
) {
    var rating by remember { mutableStateOf(0) }
    var comment by remember { mutableStateOf(TextFieldValue("")) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Leave a Review", style = MaterialTheme.typography.titleMedium)

        Row(Modifier.padding(vertical = 8.dp)) {
            for (i in 1..5) {
                IconButton(onClick = { rating = i }) {
                    Text(
                        if (i <= rating) "★" else "☆",
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }
        }

        OutlinedTextField(
            value = comment,
            onValueChange = { comment = it },
            placeholder = { Text("Write your feedback") },
            modifier = Modifier.fillMaxWidth()
        )

        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Button(
            onClick = {
                val review = Review(
                    id = UUID.randomUUID().toString(),
                    tutorId = tutorId,
                    studentId = currentUserId,
                    rating = rating,
                    comment = comment.text,
                    timestamp = System.currentTimeMillis()
                )
                reviewViewModel.submitReview(
                    review,
                    onSuccess = {

                        rating = 0
                        comment = TextFieldValue("")
                        errorMessage = null
                        onReviewSubmit()
                    },
                    onError = { message ->
                        errorMessage = message
                    }
                )
            },
            enabled = rating > 0 && comment.text.isNotBlank(),
            modifier = Modifier.padding(top = 12.dp)
        ) {
            Text("Submit")
        }
    }
}
