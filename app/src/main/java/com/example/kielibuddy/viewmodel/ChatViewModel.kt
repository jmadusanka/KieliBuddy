package com.example.kielibuddy.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kielibuddy.model.ChatConversation
import com.example.kielibuddy.model.ChatMessage
import com.example.kielibuddy.model.UserRole
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch
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
            .orderBy("lastMessageTime", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val conversations = snapshot.documents.mapNotNull { doc ->
                        val otherUserId = (doc["participants"] as? List<*>)?.firstOrNull { it != currentUserId } as? String
                        ChatConversation(
                            id = doc.id,
                            otherUserId = otherUserId ?: "",
                            otherUserName = doc.getString("otherUserName") ?: "",
                            otherUserRole = try {
                                UserRole.valueOf(doc.getString("otherUserRole") ?: "STUDENT")
                            } catch (e: Exception) {
                                UserRole.STUDENT
                            },
                            lastMessage = doc.getString("lastMessage") ?: "",
                            lastMessageTime = (doc.getLong("lastMessageTime") ?: 0L),
                            unreadCount = (doc.getLong("unreadCount") ?: 0L).toInt(),
                            otherUserProfileImg = doc.getString("otherUserProfileImg")
                        )
                    }
                    _conversations.value = conversations
                }
            }
    }

    fun sendMessage(
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
            senderRole = UserRole.STUDENT // or fetch from ViewModel if needed
        )

        val chatRef = db.collection("chats").document(chatId)
        val messageRef = chatRef.collection("messages").document(messageId)

        val chatUpdate = mapOf(
            "participants" to listOf(senderId, receiverId),
            "lastMessage" to messageText,
            "lastMessageTime" to timestamp,
            "otherUserId" to receiverId,
            "otherUserName" to receiverName,
            "otherUserRole" to receiverRole.name,
            "otherUserProfileImg" to receiverProfileImg
        )

        viewModelScope.launch {
            chatRef.set(chatUpdate)
            messageRef.set(message.toMap())
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
}
