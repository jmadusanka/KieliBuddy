package com.example.kielibuddy.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.kielibuddy.model.ChatMessage
import com.example.kielibuddy.viewmodel.ChatViewModel
import com.example.kielibuddy.model.UserRole
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController,
    chatViewModel: ChatViewModel,
    receiverId: String,
    receiverName: String,
    receiverRole: UserRole,
    receiverProfileImg: String? = null
) {
    //  Original Firebase/ViewModel
    val currentUser = FirebaseAuth.getInstance().currentUser
    val chatId = listOf(currentUser?.uid ?: "", receiverId).sorted().joinToString("_")
    val messages by chatViewModel.messages.observeAsState(emptyList())
    var messageInput by remember { mutableStateOf(TextFieldValue()) }

    // Auto-scroll to bottom when new messages arrive
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        chatViewModel.loadMessages(chatId)
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            scrollState.animateScrollToItem(0)
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // TopAppBar with Back Button
        TopAppBar(
            title = { Text("Chat with $receiverName", color = Color.White) },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF9370DB),
                titleContentColor = Color.White,
                navigationIconContentColor = Color.White
            )
        )

        // Message List (Reversed for correct ordering)
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            state = scrollState,
            reverseLayout = true
        ) {
            items(messages.reversed()) { message ->
                MessageBubble(
                    message = message,
                    isCurrentUser = message.senderId == currentUser?.uid,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }

        // Input Field (Rounded & Improved)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = messageInput,
                onValueChange = { messageInput = it },
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(24.dp)),
                placeholder = { Text("Type a message...") },
                singleLine = false,
                maxLines = 3
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (messageInput.text.isNotBlank()) {
                        // 🔥 Original sendMessage logic (UNCHANGED)
                        chatViewModel.sendMessage(
                            context = navController.context,
                            receiverId = receiverId,
                            receiverName = receiverName,
                            receiverRole = receiverRole,
                            messageText = messageInput.text,
                            receiverProfileImg = receiverProfileImg
                        )
                        messageInput = TextFieldValue("")
                        coroutineScope.launch {
                            scrollState.animateScrollToItem(0)
                        }
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        Color(0xFF9370DB),
                        RoundedCornerShape(24.dp)
                    )
            ) {
                Icon(
                    Icons.Filled.Send,
                    contentDescription = "Send",
                    tint = Color.White
                )
            }
        }
    }
}

//  New MessageBubble UI (From StudentChatScreen)
@Composable
fun MessageBubble(
    message: ChatMessage,
    isCurrentUser: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalAlignment = if (isCurrentUser) Alignment.End else Alignment.Start
    ) {
        Card(
            shape = when {
                isCurrentUser -> RoundedCornerShape(16.dp, 4.dp, 16.dp, 16.dp)
                else -> RoundedCornerShape(4.dp, 16.dp, 16.dp, 16.dp)
            },
            colors = CardDefaults.cardColors(
                containerColor = if (isCurrentUser) Color(0xFF9370DB)
                else MaterialTheme.colorScheme.surfaceVariant,
                contentColor = if (isCurrentUser) Color.White
                else MaterialTheme.colorScheme.onSurface
            )
        ) {
            Text(
                text = message.message,
                modifier = Modifier.padding(12.dp)
            )
        }

        // Timestamp (New Addition)
        Text(
            text = message.timestamp.toChatTime(),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(top = 2.dp, start = 12.dp, end = 12.dp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

// Timestamp formatter (New Addition)
private fun Long.toChatTime(): String {
    return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(this))
}