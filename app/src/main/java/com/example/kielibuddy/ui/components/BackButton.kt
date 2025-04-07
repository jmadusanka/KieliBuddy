package com.example.kielibuddy.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController

@Composable
fun BackButton(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = { navController.popBackStack() },
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = Color.Black
        )
    }
}




