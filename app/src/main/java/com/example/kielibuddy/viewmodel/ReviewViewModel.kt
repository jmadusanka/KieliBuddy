package com.example.kielibuddy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kielibuddy.model.Review
import com.example.kielibuddy.repository.ReviewRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReviewViewModel : ViewModel() {
    private val repo = ReviewRepository()

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews

    fun loadReviews(tutorId: String) {
        viewModelScope.launch {
            _reviews.value = repo.getReviewsForTutor(tutorId)
        }
    }

    fun submitReview(
        review: Review,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                repo.submitReview(review)
                onSuccess()
                loadReviews(review.tutorId)
            } catch (e: Exception) {
                onError(e.localizedMessage ?: "Failed to submit review")
            }
        }
    }
}