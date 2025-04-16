package com.example.kielibuddy.repository

import com.example.kielibuddy.model.Review
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ReviewRepository {
    private val db = FirebaseFirestore.getInstance()

    // Fetch reviews for a specific tutor
    suspend fun getReviewsForTutor(tutorId: String): List<Review> {
        val db = FirebaseFirestore.getInstance()

        return try {
            val reviewsSnapshot = db.collection("reviews")
                .whereEqualTo("tutorId", tutorId)
                .get()
                .await()

            reviewsSnapshot.documents.mapNotNull { doc ->
                val review = doc.toObject(Review::class.java)
                review?.apply {
                    // Fetch student name from users collection
                    val studentDoc = db.collection("users").document(studentId).get().await()
                    studentName = studentDoc.getString("firstName") ?: ""
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Submit a new review to Firestore
    suspend fun submitReview(review: Review) {
        db.collection("reviews")
            .document(review.id)
            .set(review)
            .await()
    }
}
