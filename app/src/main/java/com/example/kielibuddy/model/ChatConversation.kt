package com.example.kielibuddy.model

data class ChatConversation(
    val id: String = "",
    val otherUserId: String = "",       // ID of the other user (tutor/student)
    val otherUserName: String = "",     // Name of the other user
    val otherUserRole: UserRole = UserRole.STUDENT, // Role of the other user
    val lastMessage: String = "",
    val lastMessageTime: Long = System.currentTimeMillis(),
    val unreadCount: Int = 0,
    val otherUserProfileImg: String? = null // Optional profile image URL
)