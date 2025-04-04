package com.example.kielibuddy.model

data class UserModel(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val profileImg: String = "",
    val subscription: SubscriptionStatus = SubscriptionStatus.FREE,
    val role: UserRole = UserRole.STUDENT,
    val status: Int = 1, // 1 = active, 0 = inactive
    val createdAt: Long = System.currentTimeMillis(),
    val profileCompleted: Boolean = false
)

// Enum for user role
enum class UserRole {
    STUDENT, TEACHER, ADMIN
}

// Subscription Data (Tracks student subscriptions to teachers)
data class Subscription(
    val studentId: String,
    val teacherId: String,
    val startDate: Long,
    val endDate: Long,
    val status: SubscriptionStatus // ACTIVE, EXPIRED, etc.
)

enum class SubscriptionStatus {
    ACTIVE, EXPIRED, CANCELED, FREE
}

// Teacher's available time slots
data class AvailableTimeSlot(
    val teacherId: String,
    val dayOfWeek: String,  // Example: "Monday"
    val startTime: String,  // Example: "14:00"
    val endTime: String     // Example: "15:30"
)

// Student's progress tracking
data class Progress(
    val studentId: String,
    val teacherId: String,
    val date: Long,
    val topicsCovered: List<String>,  // Example: ["Grammar", "Pronunciation"]
    val notes: String = ""
)