package com.example.kielibuddy.model

data class Review(
    val id: String = "",
    val tutorId: String = "",
    val studentId: String = "",
    val rating: Int = 0,
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis()
)