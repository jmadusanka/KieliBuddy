package com.example.kielibuddy.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.kielibuddy.R
import com.example.kielibuddy.ui.theme.KieliBuddyTheme

sealed class BottomNavItem(
    val route: String,
    val iconResId: Int,
    val title: String
) {
    object Search : BottomNavItem("search", R.drawable.ic_search, "Search")
    object Chat : BottomNavItem("studentChat", R.drawable.ic_messages, "Messages")
    object Calendar : BottomNavItem("tutorCalendar", R.drawable.ic_schedule, "Schedule")
}


@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem.Search,
        BottomNavItem.Chat,
        BottomNavItem.Calendar
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        modifier = Modifier.height(56.dp),
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = item.iconResId),
                        contentDescription = item.title,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        fontSize = 12.sp
                    )
                },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
@Preview(showBackground = true)
@Composable
fun BottomNavigationBarPreview() {
    KieliBuddyTheme {
        val mockNavController = rememberNavController()
        BottomNavigationBar(navController = mockNavController)
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun BottomNavigationBarDarkPreview() {
    KieliBuddyTheme {
        val mockNavController = rememberNavController()
        BottomNavigationBar(navController = mockNavController)
    }
}