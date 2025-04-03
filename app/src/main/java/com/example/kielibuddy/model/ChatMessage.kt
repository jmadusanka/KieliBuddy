package com.example.kielibuddy.model

import java.text.SimpleDateFormat
import java.util.*

data class ChatMessage(
    val id: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val senderRole: UserRole = UserRole.STUDENT, // Replaces isTutor with enum
    val status: MessageStatus = MessageStatus.SENT
) {
    // Calculated property for backward compatibility
    val isTutor: Boolean
        get() = senderRole == UserRole.TEACHER

    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "senderId" to senderId,
        "receiverId" to receiverId,
        "message" to message,
        "timestamp" to timestamp,
        "senderRole" to senderRole.name,
        "status" to status.name
    )

    companion object {
        fun fromMap(map: Map<String, Any>): ChatMessage {
            return ChatMessage(
                id = map["id"] as? String ?: "",
                senderId = map["senderId"] as? String ?: "",
                receiverId = map["receiverId"] as? String ?: "",
                message = map["message"] as? String ?: "",
                timestamp = (map["timestamp"] as? Long) ?: 0L,
                senderRole = try {
                    UserRole.valueOf(map["senderRole"] as? String ?: "STUDENT")
                } catch (e: IllegalArgumentException) {
                    UserRole.STUDENT
                },
                status = try {
                    MessageStatus.valueOf(map["status"] as? String ?: "SENT")
                } catch (e: IllegalArgumentException) {
                    MessageStatus.SENT
                }
            )
        }
    }
}

enum class MessageStatus {
    SENT, DELIVERED, READ;

    companion object {
        fun fromString(value: String?): MessageStatus {
            return try {
                value?.let { enumValueOf<MessageStatus>(it) } ?: SENT
            } catch (e: IllegalArgumentException) {
                SENT
            }
        }
    }
}

fun Long.toChatTime(): String {
    return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(this))
}

fun Long.toChatDate(): String {
    return SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(this))
}























//
//package com.example.kielibuddy.model
//
//import java.util.*
//
//data class ChatMessage(
//    val id: String = "",              // Unique message ID
//    val senderId: String = "",        // ID of the sender (student or tutor)
//    val receiverId: String = "",      // ID of the recipient
//    val message: String = "",         // The message content
//    val timestamp: Long = 0L,         // Timestamp in milliseconds
//    val isTutor: Boolean = false,     // True if sent by tutor
//    val status: MessageStatus = MessageStatus.SENT // Delivery status
//) {
//    // Helper function for Firebase serialization
//    fun toMap(): Map<String, Any?> {
//        return mapOf(
//            "id" to id,
//            "senderId" to senderId,
//            "receiverId" to receiverId,
//            "message" to message,
//            "timestamp" to timestamp,
//            "isTutor" to isTutor,
//            "status" to status.name
//        )
//    }
//}
//
//enum class MessageStatus {
//    SENT, DELIVERED, READ
//}
//
//// Extension function to format timestamp
//fun Long.toChatTime(): String {
//    val date = Date(this)
//    val format = java.text.SimpleDateFormat("hh:mm a", Locale.getDefault())
//    return format.format(date)
//}