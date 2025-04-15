package com.example.kielibuddy.ui.pages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.kielibuddy.ui.tutor.TimeSlot

@Composable
fun TimeSlotItem(
    slot: TimeSlot,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 0.5.dp, horizontal = 10.dp)
            .background(
                color = when {
                    isSelected -> Color.Blue.copy(alpha = 0.3f) // Highlight selected slot
                    slot.isAvailable -> Color.White
                    else -> Color.LightGray
                },
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                border = BorderStroke(1.dp, if (isSelected) Color.Blue else Color.Gray),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(enabled = slot.isAvailable) { onSelect() }
            .padding(12.dp)
    ) {
        Text(
            text = slot.time, // Only show the start time
            color = if (slot.isAvailable) Color.Black else Color.Gray
        )
    }
}
