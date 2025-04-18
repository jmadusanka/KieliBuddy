package com.example.kielibuddy.ui.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.kielibuddy.model.ChatConversation
import com.example.kielibuddy.model.UserModel
import com.example.kielibuddy.model.UserRole
import com.example.kielibuddy.ui.components.BottomNavigationBar
import com.example.kielibuddy.viewmodel.ChatViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatInbox(
    navController: NavController,
    chatViewModel: ChatViewModel,
    currentUser: UserModel
) {
    val context = LocalContext.current
    val conversations by chatViewModel.conversations.observeAsState(emptyList())

    LaunchedEffect(Unit) {
        chatViewModel.loadConversations(currentUser.id)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (currentUser.role) {
                            UserRole.TEACHER -> "My Students"
                            UserRole.STUDENT -> "My Tutors"
                            else -> "Messages"
                        },
                        color = Color.White
                    )
                },
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
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (conversations.isEmpty()) {
                EmptyInboxState(currentUser.role)
            } else {
                ConversationList(navController, conversations, currentUserId = currentUser.id)

            }
        }
    }
}

@Composable
private fun EmptyInboxState(userRole: UserRole) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = when (userRole) {
                UserRole.TEACHER -> "No student conversations yet"
                UserRole.STUDENT -> "No tutor conversations yet"
                else -> "No messages yet"
            },
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}



@Composable
fun ConversationItem(
    conversation: ChatConversation,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProfileAvatar(
            profileImg = conversation.otherUserProfileImg,
            userName = conversation.otherUserName
        )

        Spacer(modifier = Modifier.width(16.dp))

        ConversationDetails(conversation)
    }
}

@Composable
private fun ConversationList(
    navController: NavController,
    conversations: List<ChatConversation>,
    currentUserId: String
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {
        items(conversations) { conversation ->

            val isCurrentUserSender = conversation.senderId == currentUserId
            val otherUserId = if (isCurrentUserSender) conversation.receiverId else conversation.senderId
            val otherUserName = if (isCurrentUserSender) conversation.receiverName else conversation.senderName
            val otherUserProfileImg = if (isCurrentUserSender) conversation.receiverProfileImg else conversation.senderProfileImg

            ConversationItem(
                conversation = conversation.copy(
                    otherUserId = otherUserId,
                    otherUserName = otherUserName,
                    otherUserProfileImg = otherUserProfileImg
                ),
                onClick = {
                    navController.navigate("chat/$otherUserId/$otherUserName")
                }
            )

            Divider(
                modifier = Modifier.padding(horizontal = 16.dp),
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            )
        }
    }
}


@Composable
private fun ProfileAvatar(
    profileImg: String?,
    userName: String
) {
    if (!profileImg.isNullOrBlank()) {
        Image(
            painter = rememberAsyncImagePainter(profileImg),
            contentDescription = "Profile Image",
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
        )
    } else {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Color(0xFF9370DB).copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = userName.take(1).uppercase(),
                style = MaterialTheme.typography.titleLarge.copy(
                    color = Color(0xFF9370DB),
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}


@Composable
private fun ConversationDetails(conversation: ChatConversation) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = conversation.otherUserName,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = conversation.lastMessageTime.toRelativeTime(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = conversation.lastMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = if (conversation.unreadCount > 0) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            if (conversation.unreadCount > 0) {
                UnreadCountBadge(conversation.unreadCount)
            }
        }
    }
}

@Composable
private fun UnreadCountBadge(count: Int) {
    Spacer(modifier = Modifier.width(8.dp))
    Box(
        modifier = Modifier
            .size(20.dp)
            .clip(CircleShape)
            .background(Color(0xFF9370DB)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.labelSmall,
            color = Color.White
        )
    }
}

private fun Long.toRelativeTime(): String {
    val now = System.currentTimeMillis()
    val diff = now - this

    return when {
        diff < 60000 -> "Just now"
        diff < 3600000 -> "${diff / 60000}m ago"
        diff < 86400000 -> "${diff / 3600000}h ago"
        diff < 604800000 -> "${diff / 86400000}d ago"
        else -> SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(this))
    }
}
