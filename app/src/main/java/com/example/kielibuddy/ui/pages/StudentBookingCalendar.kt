package com.example.kielibuddy.ui.pages

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.kielibuddy.model.Booking
import com.example.kielibuddy.model.BookingStatus
import com.example.kielibuddy.ui.tutor.TimeSlot // Assuming TimeSlot is defined here
import com.example.kielibuddy.ui.components.BackButton
import com.example.kielibuddy.ui.components.BottomNavigationBar
import com.example.kielibuddy.viewmodel.BookingViewModel
import com.google.firebase.auth.FirebaseAuth
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

data class TutorAvailability(
    val tutorId: String,
    val tutorName: String,
    val availableSlots: Map<LocalDate, List<TimeSlot>>
)

data class BookingConfirmation(
    val tutorId: String,
    val tutorName: String,
    val date: LocalDate,
    val timeSlot: TimeSlot
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentBookingCalendar(
    modifier: Modifier = Modifier,
    navController: NavController,
    tutorId: String? = null
) {
    val today = LocalDate.now()
    var currentWeekStartDate by remember { mutableStateOf(today.with(DayOfWeek.MONDAY)) }
    var selectedDate by remember { mutableStateOf(today) }
    var selectedTimeSlot by remember { mutableStateOf<TimeSlot?>(null) }
    var showConfirmationDialog by remember { mutableStateOf(false) }

    val bookingViewModel: BookingViewModel = viewModel()
    val context = LocalContext.current

    // Mock data
    val mockTutorAvailability = remember {
        TutorAvailability(
            tutorId = tutorId ?: "tutor1",
            tutorName = "Sofia Müller",
            availableSlots = mapOf(
                today to listOf(
                    TimeSlot(time = "09:00 - 10:00", startHour = 9, endHour = 10, isAvailable = true),
                    TimeSlot(time = "10:00 - 11:00", startHour = 10, endHour = 11, isAvailable = true),
                    TimeSlot(time = "11:00 - 12:00", startHour = 11, endHour = 12, isAvailable = true),
                    TimeSlot(time = "14:00 - 15:00", startHour = 14, endHour = 15, isAvailable = true),
                    TimeSlot(time = "15:00 - 16:00", startHour = 15, endHour = 16, isAvailable = true),
                    TimeSlot(time = "16:00 - 17:00", startHour = 16, endHour = 17, isAvailable = true),
                ),
                today.plusDays(1) to emptyList(),
                today.plusDays(2) to listOf(
                    TimeSlot(time = "10:00 - 11:00", startHour = 10, endHour = 11, isAvailable = true),
                    TimeSlot(time = "11:00 - 12:00", startHour = 11, endHour = 12, isAvailable = true),
                    TimeSlot(time = "16:00 - 17:00", startHour = 16, endHour = 17, isAvailable = true),
                ),
                today.plusDays(5) to listOf(
                    TimeSlot(time = "14:00 - 15:00", startHour = 14, endHour = 15, isAvailable = true),
                    TimeSlot(time = "15:00 - 16:00", startHour = 15, endHour = 16, isAvailable = true)
                ),
                today.plusDays(6) to listOf(
                    TimeSlot(time = "09:00 - 10:00", startHour = 9, endHour = 10, isAvailable = true),
                    TimeSlot(time = "11:00 - 12:00", startHour = 11, endHour = 12, isAvailable = true)
                )
            )
        )
    }

    val availableDatesForWeek = remember(currentWeekStartDate, mockTutorAvailability) {
        val endOfWeek = currentWeekStartDate.plusDays(6)
        mockTutorAvailability.availableSlots.keys.filter { !it.isBefore(currentWeekStartDate) && !it.isAfter(endOfWeek) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Book Session", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    BackButton(navController = navController)
                }
            )
        },
        bottomBar = { BottomNavigationBar(navController = navController) },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Tutor Info
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = mockTutorAvailability.tutorName.first().toString().uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                    Text(
                        text = "Book with ${mockTutorAvailability.tutorName}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Weekly Calendar Navigation with "Today" Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { currentWeekStartDate = currentWeekStartDate.minusDays(7) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Week")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Button(
                            onClick = {
                                currentWeekStartDate = today.with(DayOfWeek.MONDAY)
                                selectedDate = today
                            },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A3DE2))
                        ) {
                            Text("Today", fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${currentWeekStartDate.format(DateTimeFormatter.ofPattern("d MMM"))} - " +
                                    "${currentWeekStartDate.plusDays(6).format(DateTimeFormatter.ofPattern("d MMM"))}",
                            fontWeight = FontWeight.Medium
                        )
                    }
                    IconButton(onClick = { currentWeekStartDate = currentWeekStartDate.plusDays(7) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Week")
                    }
                }

                // Weekly Dates with Weekend Highlighting
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    val daysOfWeek = (0..6).map { currentWeekStartDate.plusDays(it.toLong()) }
                    items(daysOfWeek) { date ->
                        val isWeekend = date.dayOfWeek == DayOfWeek.SATURDAY || date.dayOfWeek == DayOfWeek.SUNDAY
                        DateItem(
                            date = date,
                            isSelected = date == selectedDate,
                            hasSlots = availableDatesForWeek.contains(date),
                            isWeekend = isWeekend
                        ) { selectedDate = it; selectedTimeSlot = null }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Available Time Slots in Two Columns
                Text("Available Times", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 8.dp))
                val availableTimeSlotsForDate = mockTutorAvailability.availableSlots[selectedDate] ?: emptyList()
                if (availableTimeSlotsForDate.isEmpty()) {
                    Text("No available slots on this day.", color = Color.Gray)
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(availableTimeSlotsForDate) { slot ->
                            TimeSlotItem(
                                slot = slot,
                                isSelected = selectedTimeSlot == slot,
                                onSelect = { selectedTimeSlot = slot }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Book Button
                Button(
                    onClick = { showConfirmationDialog = true },
                    enabled = selectedTimeSlot != null && selectedTimeSlot?.isAvailable == true,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A3DE2))

                ) {
                    Text("Book Now", fontWeight = FontWeight.Bold)

                }

                if (showConfirmationDialog) {
                    AlertDialog(
                        onDismissRequest = { showConfirmationDialog = false },
                        title = { Text("Confirm Booking") },
                        text = {
                            Text(
                                "Book ${selectedTimeSlot?.time} on ${selectedDate.format(DateTimeFormatter.ofPattern("d MMMM"))} with ${mockTutorAvailability.tutorName}?"
                            )
                        },
                        confirmButton = {
                            Button(onClick = {
                                val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return@Button
                                val booking = Booking(
                                    id = UUID.randomUUID().toString(),
                                    tutorId = mockTutorAvailability.tutorId,
                                    studentId = currentUserId,
                                    date = selectedDate.toString(),
                                    timeSlot = selectedTimeSlot?.time ?: "",
                                    status = BookingStatus.BOOKED
                                )
                                bookingViewModel.bookSession(
                                    booking,
                                    onSuccess = {
                                        showConfirmationDialog = false
                                        navController.navigate("booking_confirmation")
                                    },
                                    onError = {
                                        Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                                    }
                                )
                            },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A3DE2))
                            ) {
                                Text("Confirm")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showConfirmationDialog = false }) {
                                Text("Cancel")
                            }
                        }
                    )
                }
            }
        }
    )
}

@Composable
fun DateItem(date: LocalDate, isSelected: Boolean, hasSlots: Boolean, isWeekend: Boolean, onDateClick: (LocalDate) -> Unit) {
    val backgroundColor = if (isSelected) Color(0xFF6A3DE2).copy(alpha = 0.6f) else MaterialTheme.colorScheme.surface
    val textColor = if (isSelected) Color.White else if (isWeekend) Color.Blue else Color.Black
    val fontWeight = if (LocalDate.now() == date) FontWeight.Bold else FontWeight.Normal



    OutlinedButton(
        onClick = { onDateClick(date) },
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = backgroundColor),
        contentPadding = PaddingValues(4.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()), fontSize = 10.sp, color = textColor.copy(alpha = 0.8f))
            Text(date.dayOfMonth.toString(), fontWeight = fontWeight, color = textColor, fontSize = 14.sp)
            if (hasSlots && !isSelected) {
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .background(Color.Green, CircleShape)
                        .align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
fun BookingConfirmationScreen(
    navController: NavController,
    bookingConfirmation: BookingConfirmation
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.CheckCircle,
            contentDescription = "Success",
            tint = Color.Green,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Booking Confirmed!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Text("You have a session with ${bookingConfirmation.tutorName} on:", textAlign = TextAlign.Center)
        Text(
            "${bookingConfirmation.date.format(DateTimeFormatter.ofPattern("d MMMM"))} at ${bookingConfirmation.timeSlot.time}",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = { navController.navigate("student_dashboard") }) {
            Text("Back to Dashboard")
        }
    }
}