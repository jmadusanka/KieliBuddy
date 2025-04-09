package com.example.kielibuddy.repository

import com.example.kielibuddy.model.Review
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ReviewRepository {
    private val db = FirebaseFirestore.getInstance()

    // Fetch reviews for a specific tutor
    suspend fun getReviewsForTutor(tutorId: String): List<Review> {
        return try {
            db.collection("reviews")
                .whereEqualTo("tutorId", tutorId)
                .orderBy("timestamp")
                .get()
                .await()
                .documents.mapNotNull { it.toObject(Review::class.java) }
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
