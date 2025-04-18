package com.example.kielibuddy.model

data class ChatConversation(
    val id: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val senderProfileImg: String? = null,
    val receiverId: String = "",
    val receiverName: String = "",
    val receiverProfileImg: String? = null,
    val lastMessage: String = "",
    val lastMessageTime: Long = System.currentTimeMillis(),
    val unreadCount: Int = 0,

    var otherUserId: String = "",
    var otherUserName: String = "",
    var otherUserProfileImg: String? = null
)