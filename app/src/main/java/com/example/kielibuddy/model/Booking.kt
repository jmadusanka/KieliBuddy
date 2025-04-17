package com.example.kielibuddy.model

data class Booking(
    val id: String = "",
    val tutorId: String = "",
    val studentId: String = "",
    val date: String = "", // Format: YYYY-MM-DD
    val timeSlot: String = "", // Format: 14:00 - 15:00
    val durationMinutes: Int = 60,
    val price: Int = 0,
    val lessonType: LessonType = LessonType.REGULAR,
    val status: BookingStatus = BookingStatus.BOOKED,
    val timestamp: Long = System.currentTimeMillis()
)

enum class LessonType {
    TRIAL,
    REGULAR
}

enum class BookingStatus {
    BOOKED,
    COMPLETED,
    CANCELLED
}
