package com.example.kielibuddy.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kielibuddy.model.ChatConversation
import com.example.kielibuddy.model.ChatMessage
import com.example.kielibuddy.model.UserRole
import com.example.kielibuddy.util.sendPushNotification
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*

class ChatViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _conversations = MutableLiveData<List<ChatConversation>>()
    val conversations: LiveData<List<ChatConversation>> = _conversations

    private val _messages = MutableLiveData<List<ChatMessage>>()
    val messages: LiveData<List<ChatMessage>> = _messages

    fun loadConversations(currentUserId: String) {
        db.collection("chats")
            .whereArrayContains("participants", currentUserId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val conversations = snapshot.documents.mapNotNull { doc ->
                        val senderId = doc.getString("senderId") ?: return@mapNotNull null
                        val receiverId = doc.getString("receiverId") ?: return@mapNotNull null

                        val isCurrentUserSender = currentUserId == senderId

                        ChatConversation(
                            id = doc.id,
                            senderId = senderId,
                            senderName = doc.getString("senderName") ?: "",
                            senderProfileImg = doc.getString("senderProfileImg"),
                            receiverId = receiverId,
                            receiverName = doc.getString("receiverName") ?: "",
                            receiverProfileImg = doc.getString("receiverProfileImg"),
                            lastMessage = doc.getString("lastMessage") ?: "",
                            lastMessageTime = doc.getLong("lastMessageTime") ?: 0L,
                            unreadCount = (doc.getLong("unreadCount") ?: 0L).toInt(),

                            otherUserId = if (isCurrentUserSender) receiverId else senderId,
                            otherUserName = if (isCurrentUserSender) doc.getString("receiverName") ?: "" else doc.getString("senderName") ?: "",
                            otherUserProfileImg = if (isCurrentUserSender) doc.getString("receiverProfileImg") else doc.getString("senderProfileImg")
                        )
                    }
                    _conversations.value = conversations
                } else {
                    _conversations.value = emptyList()
                }
            }
    }

    fun sendMessage(
        context: Context,
        receiverId: String,
        receiverName: String,
        receiverRole: UserRole,
        messageText: String,
        receiverProfileImg: String?
    ) {
        val sender = auth.currentUser ?: return
        val senderId = sender.uid
        val chatId = listOf(senderId, receiverId).sorted().joinToString("_")
        val messageId = UUID.randomUUID().toString()
        val timestamp = System.currentTimeMillis()

        val message = ChatMessage(
            id = messageId,
            senderId = senderId,
            receiverId = receiverId,
            message = messageText,
            timestamp = timestamp,
            senderRole = UserRole.STUDENT
        )

        val chatRef = db.collection("chats").document(chatId)
        val messageRef = chatRef.collection("messages").document(messageId)

        val chatUpdate = mapOf(
            "participants" to listOf(senderId, receiverId),
            "lastMessage" to messageText,
            "lastMessageTime" to timestamp,
            "senderId" to senderId,
            "senderName" to (sender.displayName ?: "Student"),
            "senderProfileImg" to sender.photoUrl?.toString(),
            "receiverId" to receiverId,
            "receiverName" to receiverName,
            "receiverProfileImg" to receiverProfileImg
        )

        viewModelScope.launch {
            chatRef.set(chatUpdate)
            messageRef.set(message.toMap())

            val token = getReceiverToken(receiverId)
            if (token.isNotEmpty()) {
                sendPushNotification(
                    context = context,
                    toToken = token,
                    title = "New Message from ${sender.displayName ?: "User"}",
                    message = messageText
                )
            }
        }
    }

    fun loadMessages(chatId: String) {
        db.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val msgs = snapshot.documents.mapNotNull { doc ->
                        ChatMessage.fromMap(doc.data ?: emptyMap())
                    }
                    _messages.value = msgs
                }
            }
    }

    private suspend fun getReceiverToken(userId: String): String {
        val doc = db.collection("users").document(userId).get().await()
        return doc.getString("fcmToken") ?: ""
    }
}
