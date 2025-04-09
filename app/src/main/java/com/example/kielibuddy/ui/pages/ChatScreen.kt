package com.example.kielibuddy.ui.pages

import androidx.compose.foundation.Image
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
import coil.compose.rememberAsyncImagePainter
import com.example.kielibuddy.model.ChatMessage
import com.example.kielibuddy.model.UserModel
import com.example.kielibuddy.model.UserRole
import com.example.kielibuddy.viewmodel.ChatViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
    receiverRole: UserRole
) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val chatId = listOf(currentUser?.uid ?: "", receiverId).sorted().joinToString("_")
    val messages by chatViewModel.messages.observeAsState(emptyList())
    var messageInput by remember { mutableStateOf(TextFieldValue()) }

    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // 🔥 Fetch receiver's profile image dynamically
    var receiverProfileUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(receiverId) {
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(receiverId)
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject(UserModel::class.java)
                receiverProfileUrl = user?.profileImg
            }
    }

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
        TopAppBar(
            title = {},
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF9370DB))
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF9370DB))
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = receiverProfileUrl
                        ?: "https://upload.wikimedia.org/wikipedia/commons/thumb/2/2c/Default_pfp.svg/1200px-Default_pfp.svg.png"
                ),
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(50))
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = receiverName,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
        }

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
                        chatViewModel.sendMessage(
                            context = navController.context,
                            receiverId = receiverId,
                            receiverName = receiverName,
                            receiverRole = receiverRole,
                            messageText = messageInput.text,
                            receiverProfileImg = receiverProfileUrl
                        )
                        messageInput = TextFieldValue("")
                        coroutineScope.launch {
                            scrollState.animateScrollToItem(0)
                        }
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFF9370DB), RoundedCornerShape(24.dp))
            ) {
                Icon(Icons.Filled.Send, contentDescription = "Send", tint = Color.White)
            }
        }
    }
}

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
            shape = if (isCurrentUser)
                RoundedCornerShape(16.dp, 4.dp, 16.dp, 16.dp)
            else
                RoundedCornerShape(4.dp, 16.dp, 16.dp, 16.dp),
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

        Text(
            text = message.timestamp.toChatTime(),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(top = 2.dp, start = 12.dp, end = 12.dp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

private fun Long.toChatTime(): String {
    return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(this))
}
