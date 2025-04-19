package com.example.kielibuddy.ui.pages

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.kielibuddy.model.Booking
import com.example.kielibuddy.model.BookingStatus
import com.example.kielibuddy.model.LessonType
import com.example.kielibuddy.model.UserModel
import com.example.kielibuddy.repository.AvailabilityRepository
import com.example.kielibuddy.repository.UserRepository
import com.example.kielibuddy.ui.components.BackButton
import com.example.kielibuddy.ui.components.BottomNavigationBar
import com.example.kielibuddy.viewmodel.BookingViewModel
import com.google.firebase.auth.FirebaseAuth
import createStripeCheckoutSession
import kotlinx.coroutines.launch
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentBookingCalendar(
    modifier: Modifier = Modifier,
    navController: NavController,
    tutorId: String? = null,
    isTrial: Boolean = false
) {
    val today = LocalDate.now()
    var currentWeekStartDate by remember { mutableStateOf(today.with(DayOfWeek.MONDAY)) }
    var selectedDate by remember { mutableStateOf(today) }
    var selectedTimeSlot by remember { mutableStateOf<TimeSlot?>(null) }
    var showConfirmationDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val bookingViewModel: BookingViewModel = viewModel()
    val coroutineScope = rememberCoroutineScope()
    val availabilityRepo = remember { AvailabilityRepository() }
    val userRepo = remember { UserRepository() }

    var availabilityMap by remember { mutableStateOf<Map<LocalDate, List<String>>>(emptyMap()) }
    var tutor by remember { mutableStateOf<UserModel?>(null) }
    val bookings by bookingViewModel.studentBookings.collectAsState()

    LaunchedEffect(tutorId) {
        tutorId?.let {
            availabilityMap = availabilityRepo.getTutorAvailability(it)
            tutor = userRepo.getUserDetails(it)
            bookingViewModel.loadStudentBookings(FirebaseAuth.getInstance().currentUser?.uid ?: "")
        }
    }

    val tutorName = tutor?.let { "${it.firstName} ${it.lastName}" } ?: "Tutor"

    val availableDatesForWeek = remember(currentWeekStartDate, availabilityMap) {
        val endOfWeek = currentWeekStartDate.plusDays(6)
        availabilityMap.keys.filter { !it.isBefore(currentWeekStartDate) && !it.isAfter(endOfWeek) }
    }

    val bookedSlots = remember(selectedDate, bookings) {
        bookings.filter {
            it.tutorId == tutorId && it.date == selectedDate.toString()
        }.map { it.timeSlot }
    }

    val availableTimeSlotsForDate = availabilityMap[selectedDate]?.mapIndexedNotNull { index, time ->
        if (time !in bookedSlots) {
            TimeSlot(time, index + 8, true, index + 9)
        } else null
    } ?: emptyList()

    if (isTrial) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, "You're booking a 20-minute free trial session.", Toast.LENGTH_LONG).show()
        }
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (!tutor?.profileImg.isNullOrBlank()) {
                        Image(
                            painter = rememberAsyncImagePainter(tutor!!.profileImg),
                            contentDescription = "Tutor Profile",
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentScale = ContentScale.Crop
                        )
                        Text(
                            text = tutorName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tutorName.first().toString().uppercase(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                    }

                }

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

                Text("Available Times", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 8.dp))

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
                    val sessionLabel = if (isTrial) "20-min Trial Session" else "1-hour Session"
                    val confirmationText = "Book a $sessionLabel at ${selectedTimeSlot?.time} on ${selectedDate.format(DateTimeFormatter.ofPattern("d MMMM"))} with $tutorName?"

                    AlertDialog(
                        onDismissRequest = { showConfirmationDialog = false },
                        title = { Text("Confirm Booking") },
                        text = {
                            Text(confirmationText)
                        },
                        confirmButton = {
                            Button(onClick = {
                                val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return@Button

                                val booking = Booking(
                                    id = UUID.randomUUID().toString(),
                                    tutorId = tutorId ?: "",
                                    studentId = currentUserId,
                                    date = selectedDate.toString(),
                                    timeSlot = selectedTimeSlot?.time ?: "",
                                    durationMinutes = if (isTrial) 20 else 60,
                                    price = if (isTrial) 0 else (tutor?.price50Min ?: 0),
                                    lessonType = if (isTrial) LessonType.TRIAL else LessonType.REGULAR,
                                    status = BookingStatus.BOOKED
                                )

                                Log.d("BookingMeta", booking.toString())

                                coroutineScope.launch {
                                    if (!isTrial) {
                                        createStripeCheckoutSession(
                                            amountInCents = booking.price * 100,
                                            booking = booking,
                                            onSuccess = { url ->
                                                val intent = CustomTabsIntent.Builder().build()
                                                intent.launchUrl(context, Uri.parse(url))
                                            },
                                            onError = { error ->
                                                println("StripeCheckout Stripe Error: $error")
                                                Toast.makeText(context, "Payment failed: $error", Toast.LENGTH_LONG).show()
                                            }
                                        )
                                    } else {
                                        bookingViewModel.bookSession(
                                            booking,
                                            onSuccess = {
                                                Toast.makeText(context, "Booking confirmed!", Toast.LENGTH_SHORT).show()
                                                showConfirmationDialog = false
                                                navController.navigate("StudentScheduleScreen")
                                            },
                                            onError = {
                                                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                                            }
                                        )
                                    }
                                }
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
fun DateItem(
    date: LocalDate,
    isSelected: Boolean,
    hasSlots: Boolean,
    isWeekend: Boolean,
    onDateClick: (LocalDate) -> Unit
) {
    val backgroundColor = if (isSelected) Color(0xFF6A3DE2).copy(alpha = 0.6f) else MaterialTheme.colorScheme.surface
    val textColor = if (isSelected) Color.White else if (isWeekend) Color.Blue else Color.Black
    val fontWeight = if (LocalDate.now() == date) FontWeight.Bold else FontWeight.Normal
    val isPast = date.isBefore(LocalDate.now())

    OutlinedButton(
        onClick = { if (!isPast) onDateClick(date) },
        enabled = !isPast,
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

