// ChatInbox.kt
package com.example.kielibuddy.ui.pages

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.kielibuddy.model.ChatConversation
import com.example.kielibuddy.model.UserModel
import com.example.kielibuddy.model.UserRole
import com.example.kielibuddy.ui.components.BottomNavigationBar
import com.example.kielibuddy.ui.theme.KieliBuddyTheme
import java.text.SimpleDateFormat
import java.util.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatInbox(
    navController: NavController,
    currentUser: UserModel,
    conversations: List<ChatConversation> = emptyList() // Should come from ViewModel
) {
    Scaffold(
        topBar = {
            // Header
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
            // Bottom navigation bar
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
                ConversationList(navController, conversations)
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
private fun ConversationList(
    navController: NavController,
    conversations: List<ChatConversation>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {
        items(conversations) { conversation ->
            ConversationItem(
                conversation = conversation,
                onClick = {
                    navController.navigate("chat/${conversation.otherUserId}/${conversation.otherUserName}")
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
        //  profile image or initial
        ProfileAvatar(
            profileImg = conversation.otherUserProfileImg,
            userName = conversation.otherUserName
        )

        Spacer(modifier = Modifier.width(16.dp))

        ConversationDetails(conversation)
    }
}

@Composable
private fun ProfileAvatar(
    profileImg: String?,
    userName: String
) {
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

@Preview(name = "Student Inbox")
@Composable
fun StudentInboxPreview() {
    KieliBuddyTheme {
        ChatInbox(
            navController = rememberNavController(),
            currentUser = UserModel(
                id = "student123",
                firstName = "John",
                lastName = "Doe",
                role = UserRole.STUDENT
            ),
            conversations = listOf(
                ChatConversation(
                    id = "1",
                    otherUserId = "tutor123",
                    otherUserName = "Anna",
                    otherUserRole = UserRole.TEACHER,
                    lastMessage = "Sure! I can help you with Finnish grammar",
                    lastMessageTime = System.currentTimeMillis() - 10000,
                    unreadCount = 2
                )
            )
        )
    }
}

@Preview(name = "Tutor Inbox")
@Composable
fun TutorInboxPreview() {
    KieliBuddyTheme {
        ChatInbox(
            navController = rememberNavController(),
            currentUser = UserModel(
                id = "tutor123",
                firstName = "Anna",
                lastName = "Korpela",
                role = UserRole.TEACHER
            ),
            conversations = listOf(
                ChatConversation(
                    id = "1",
                    otherUserId = "student123",
                    otherUserName = "John Doe",
                    otherUserRole = UserRole.STUDENT,
                    lastMessage = "I need help with verb conjugation",
                    lastMessageTime = System.currentTimeMillis() - 5000,
                    unreadCount = 1
                )
            )
        )
    }
}

@Preview(name = "Empty Inbox")
@Composable
fun EmptyInboxPreview() {
    KieliBuddyTheme {
        ChatInbox(
            navController = rememberNavController(),
            currentUser = UserModel(
                id = "student123",
                firstName = "John",
                lastName = "Doe",
                role = UserRole.STUDENT
            ),
            conversations = emptyList()
        )
    }
}