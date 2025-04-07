package com.example.kielibuddy.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.kielibuddy.model.ChatMessage
import com.example.kielibuddy.viewmodel.ChatViewModel
import com.example.kielibuddy.model.UserRole
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ChatScreen(
    navController: NavController,
    chatViewModel: ChatViewModel,
    receiverId: String,
    receiverName: String,
    receiverRole: UserRole,
    receiverProfileImg: String? = null
) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val chatId = listOf(currentUser?.uid ?: "", receiverId).sorted().joinToString("_")

    val messages by chatViewModel.messages.observeAsState(emptyList())
    var messageInput by remember { mutableStateOf(TextFieldValue()) }

    LaunchedEffect(Unit) {
        chatViewModel.loadMessages(chatId)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Chat with $receiverName",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp)
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            reverseLayout = false
        ) {
            items(messages) { message ->
                ChatMessageItem(message = message, isSender = message.senderId == currentUser?.uid)
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            OutlinedTextField(
                value = messageInput,
                onValueChange = { messageInput = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type a message") }
            )

            IconButton(onClick = {
                if (messageInput.text.isNotBlank()) {
                    chatViewModel.sendMessage(
                        receiverId = receiverId,
                        receiverName = receiverName,
                        receiverRole = receiverRole,
                        messageText = messageInput.text,
                        receiverProfileImg = receiverProfileImg
                    )
                    messageInput = TextFieldValue("")
                }
            }) {
                Icon(imageVector = Icons.Default.Send, contentDescription = "Send")
            }
        }
    }
}

@Composable
fun ChatMessageItem(message: ChatMessage, isSender: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isSender) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = if (isSender) Color(0xFF9370DB) else Color(0xFFE0E0E0),
            modifier = Modifier
                .padding(vertical = 4.dp)
                .widthIn(max = 280.dp)
        ) {
            Text(
                text = message.message,
                color = if (isSender) Color.White else Color.Black,
                modifier = Modifier.padding(10.dp)
            )
        }
    }
}
