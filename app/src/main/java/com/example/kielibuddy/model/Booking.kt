package com.example.kielibuddy.model

data class Booking(
    val id: String = "",
    val tutorId: String,
    val studentId: String,
    val date: String, // Format: YYYY-MM-DD
    val timeSlot: String, // Format: 14:00 - 15:00
    val durationMinutes: Int = 60,
    val price: Int = 0, // in EUR or cents based on your pricing model
    val lessonType: LessonType = LessonType.TRIAL,
    val status: BookingStatus = BookingStatus.BOOKED, // Booked, Completed, Cancelled
    val timestamp: Long = System.currentTimeMillis()
)

enum class LessonType {
    REGULAR,
    TRIAL,
    GROUP
}

enum class BookingStatus {
    BOOKED,
    COMPLETED,
    CANCELLED
}
