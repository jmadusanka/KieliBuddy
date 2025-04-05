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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.kielibuddy.model.ChatMessage
import com.example.kielibuddy.model.UserRole
import com.example.kielibuddy.ui.theme.KieliBuddyTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material3.TextFieldDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentChatScreen(
    navController: NavController,
    tutorName: String = "Tutor Name"
) {
    // Sample messages (replace with Model data later)
    val messages = remember {
        listOf(
            ChatMessage(
                id = "1",
                senderId = "tutor123",
                receiverId = "student456",
                message = "Hello! How can I help you today?",
                senderRole = UserRole.TEACHER,
                timestamp = System.currentTimeMillis() - 10000
            ),
            ChatMessage(
                id = "2",
                senderId = "student456",
                receiverId = "tutor123",
                message = "Hi! I need help with Finnish ",
                senderRole = UserRole.STUDENT,
                timestamp = System.currentTimeMillis() - 5000
            ),
            ChatMessage(
                id = "3",
                senderId = "tutor123",
                receiverId = "student456",
                message = "Sure! I can help you",
                senderRole = UserRole.TEACHER,
                timestamp = System.currentTimeMillis() - 2000
            )
        )
    }

    var messageText by remember { mutableStateOf(TextFieldValue()) }
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Auto-scroll to bottom when new messages arrive
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
        // Header with purple background
        TopAppBar(
            title = { Text(tutorName, color = Color.White) },
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

        // Messages list with simple touch scrolling
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
                    isStudent = message.senderRole == UserRole.STUDENT,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }

        // Message input area
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = messageText,
                onValueChange = { messageText = it },
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(24.dp)),
                placeholder = { Text("Type your message...") },
                singleLine = false,
                maxLines = 3
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (messageText.text.isNotBlank()) {
                        // Send logic would go here
                        messageText = TextFieldValue("")
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
                    contentDescription = "Send message",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun MessageBubble(
    message: ChatMessage,
    isStudent: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalAlignment = if (isStudent) Alignment.End else Alignment.Start
    ) {
        Card(
            shape = when {
                isStudent -> RoundedCornerShape(16.dp, 4.dp, 16.dp, 16.dp)
                else -> RoundedCornerShape(4.dp, 16.dp, 16.dp, 16.dp)
            },
            colors = CardDefaults.cardColors(
                containerColor = if (isStudent) Color(0xFF9370DB)
                else MaterialTheme.colorScheme.surfaceVariant,
                contentColor = if (isStudent) Color.White
                else MaterialTheme.colorScheme.onSurface
            )
        ) {
            Text(
                text = message.message,
                modifier = Modifier.padding(12.dp)
            )
        }

        Text(
            text = message.timestamp.toChatTime(),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(top = 2.dp, start = 12.dp, end = 12.dp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

// Timestamp formatting
private fun Long.toChatTime(): String {
    return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(this))
}

@Preview(name = "Light Mode", showBackground = true)
@Composable
fun StudentChatScreenLightPreview() {
    KieliBuddyTheme {
        StudentChatScreen(
            navController = rememberNavController(),
            tutorName = "Anna Korpela"
        )
    }
}

@Preview(name = "Dark Mode", showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun StudentChatScreenDarkPreview() {
    KieliBuddyTheme {
        StudentChatScreen(
            navController = rememberNavController(),
            tutorName = "Anna Korpela"
        )
    }
}