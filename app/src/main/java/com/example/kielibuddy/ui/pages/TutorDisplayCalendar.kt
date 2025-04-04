package com.example.kielibuddy.ui.Tutor

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.kielibuddy.ui.screens.Tutor.TimeSlotItem
import java.time.*
import java.time.format.TextStyle
import java.util.*

data class TimeSlot(
    val time: String,
    val startHour: Int,
    val isAvailable: Boolean = true
)

@Composable
fun TutorDisplayCalendar(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val today = LocalDate.now()
    val currentTime = LocalTime.now()
    val currentHour = currentTime.hour

    var currentMonth by remember { mutableStateOf(YearMonth.from(today)) }
    var selectedDate by remember { mutableStateOf(today) }

    val selectedTimeSlots = remember { mutableStateMapOf<LocalDate, List<TimeSlot>>() }

    fun getTimeSlotsForDate(date: LocalDate): List<TimeSlot> {
        return (8..21).map { hour ->
            val isPastSlot = date.isEqual(today) && hour <= currentHour

            TimeSlot(
                time = String.format("%02d:00 - %02d:00", hour, hour + 1),
                startHour = hour,
                isAvailable = !isPastSlot
            )
        }
    }

    val weekendBackgroundColor = Color(0xFFE6F7FF)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Month")
            }

            Text(
                text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${currentMonth.year}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Row {
                OutlinedButton(
                    onClick = {
                        currentMonth = YearMonth.from(today)
                        selectedDate = today
                    },
                    modifier = Modifier.padding(end = 8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Today",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black

                    )
                }

                IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Month")
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            for (dayOfWeek in DayOfWeek.values()) {
                val isWeekend = dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY
                Text(
                    text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                    modifier = Modifier.width(40.dp),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isWeekend) Color.Blue else Color.Black
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            val firstDayOfMonth = currentMonth.atDay(1)
            val padding = firstDayOfMonth.dayOfWeek.value - 1

            items(padding) {
                Box(modifier = Modifier.size(40.dp))
            }

            items(currentMonth.lengthOfMonth()) { day ->
                val date = currentMonth.atDay(day + 1)
                val isPastDate = date.isBefore(today)
                val isWeekend = date.dayOfWeek == DayOfWeek.SATURDAY || date.dayOfWeek == DayOfWeek.SUNDAY
                val isToday = date.isEqual(today)

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = when {
                                isToday -> Color(0xFFE3F2FD)
                                isWeekend -> weekendBackgroundColor
                                else -> Color.Transparent
                            }
                        )
                        .border(
                            BorderStroke(
                                width = if (date == selectedDate) 2.dp else 1.dp,
                                color = when {
                                    date == selectedDate -> Color.Blue
                                    isToday -> Color.Blue.copy(alpha = 0.5f)
                                    else -> Color.Transparent
                                }
                            )
                        )
                        .clickable(enabled = !isPastDate) {
                            if (!isPastDate) selectedDate = date
                        }
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (day + 1).toString(),
                        textAlign = TextAlign.Center,
                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                        color = when {
                            isPastDate -> Color.Gray
                            isWeekend -> Color.Blue.copy(alpha = 0.7f)
                            else -> Color.Black
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Available Sessions for ${selectedDate.dayOfMonth} ${selectedDate.month.getDisplayName(TextStyle.FULL, Locale.getDefault())}",
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        val timeSlotsForSelectedDate by remember(selectedDate) {
            mutableStateOf(getTimeSlotsForDate(selectedDate))
        }

        val currentSelectedSlots = selectedTimeSlots[selectedDate] ?: emptyList()

        LazyColumn {
            items(timeSlotsForSelectedDate) { slot ->
                val isSlotSelected = currentSelectedSlots.any { it.time == slot.time }

                TimeSlotItem(
                    slot = slot,
                    isSelected = isSlotSelected,
                    onSelect = {
                        if (slot.isAvailable) {
                            val currentSlots = selectedTimeSlots[selectedDate]?.toMutableList() ?: mutableListOf()

                            if (isSlotSelected) {
                                currentSlots.removeIf { it.time == slot.time }
                            } else {
                                currentSlots.add(slot)
                            }

                            selectedTimeSlots[selectedDate] = currentSlots
                            println("Selected time slots for $selectedDate: ${currentSlots.joinToString { it.time }}")
                        }
                    }
                )
            }
        }
    }
}