package com.example.kielibuddy.ui.Tutor

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.kielibuddy.ui.components.BackButton
import com.example.kielibuddy.ui.components.BottomNavigationBar
import com.example.kielibuddy.ui.pages.TimeSlotItem
import java.time.*
import java.time.format.TextStyle
import java.util.*

data class TimeSlot(
    val time: String,
    val startHour: Int,
    val isAvailable: Boolean = true
)

@OptIn(ExperimentalMaterial3Api::class)
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
            val isPast = date == today && hour <= currentHour
            TimeSlot(
                time = String.format("%02d:00 - %02d:00", hour, hour + 1),
                startHour = hour,
                isAvailable = !isPast
            )
        }
    }

    val weekendColor = Color(0xFFE6F7FF)

    // Use Scaffold for layout
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        BackButton(navController = navController)
                        Text(
                            text = "Tutor Calendar",
                            fontSize = 22.sp,
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )
                    }
                },
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        },
        content = { paddingValues ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.Start
            ) {
                // Month navigation
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
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

                // Weekday headers
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (day in DayOfWeek.values()) {
                        val isWeekend = day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY
                        Text(
                            text = day.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                            modifier = Modifier.width(40.dp),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isWeekend) Color.Blue else Color.Black
                        )
                    }
                }

                // Calendar grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    val firstDay = currentMonth.atDay(1)
                    val offset = firstDay.dayOfWeek.value - 1

                    items(offset) {
                        Box(modifier = Modifier.size(40.dp))
                    }

                    items(currentMonth.lengthOfMonth()) { day ->
                        val date = currentMonth.atDay(day + 1)
                        val isPast = date.isBefore(today)
                        val isWeekend = date.dayOfWeek == DayOfWeek.SATURDAY || date.dayOfWeek == DayOfWeek.SUNDAY
                        val isToday = date == today

                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    color = when {
                                        isToday -> Color(0xFFE3F2FD)
                                        isWeekend -> weekendColor
                                        else -> Color.Transparent
                                    }
                                )
                                .border(
                                    width = if (date == selectedDate) 2.dp else 1.dp,
                                    color = when {
                                        date == selectedDate -> Color.Blue
                                        isToday -> Color.Blue.copy(alpha = 0.5f)
                                        else -> Color.Transparent
                                    },
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable(enabled = !isPast) {
                                    if (!isPast) selectedDate = date
                                }
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = (day + 1).toString(),
                                textAlign = TextAlign.Center,
                                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                                color = when {
                                    isPast -> Color.Gray
                                    isWeekend -> Color.Blue.copy(alpha = 0.7f)
                                    else -> Color.Black
                                }
                            )
                        }
                    }
                }

                // Time slot section
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Available Sessions for ${selectedDate.dayOfMonth} ${selectedDate.month.getDisplayName(TextStyle.FULL, Locale.getDefault())}",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                val timeSlots by remember(selectedDate) {
                    mutableStateOf(getTimeSlotsForDate(selectedDate))
                }

                val currentSelected = selectedTimeSlots[selectedDate] ?: emptyList()

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxHeight()
                ) {
                    items(timeSlots.size) { index ->
                        val slot = timeSlots[index]
                        val isSelected = currentSelected.any { it.time == slot.time }

                        TimeSlotItem(
                            slot = slot,
                            isSelected = isSelected,
                            onSelect = {
                                if (slot.isAvailable) {
                                    val updatedSlots = selectedTimeSlots[selectedDate]?.toMutableList() ?: mutableListOf()
                                    if (isSelected) {
                                        updatedSlots.removeIf { it.time == slot.time }
                                    } else {
                                        updatedSlots.add(slot)
                                    }
                                    selectedTimeSlots[selectedDate] = updatedSlots
                                    println("Selected slots for $selectedDate: ${updatedSlots.joinToString { it.time }}")
                                }
                            }
                        )
                    }
                }
            }
        }
    )
}
