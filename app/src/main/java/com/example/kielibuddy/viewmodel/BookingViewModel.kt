package com.example.kielibuddy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kielibuddy.model.Booking
import com.example.kielibuddy.repository.BookingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BookingViewModel : ViewModel() {
    private val repository = BookingRepository()
    private val _studentBookings = MutableStateFlow<List<Booking>>(emptyList())
    val studentBookings: StateFlow<List<Booking>> = _studentBookings

    fun bookSession(
        booking: Booking,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                repository.createBooking(booking)
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Booking failed")
            }
        }
    }

    fun loadStudentBookings(studentId: String) {
        viewModelScope.launch {
            try {
                val bookings = repository.getBookingsForStudent(studentId)
                println("Loaded bookings for student: $bookings")
                _studentBookings.value = bookings
            } catch (e: Exception) {
                _studentBookings.value = emptyList()
            }
        }
    }

    fun loadTutorBookings(tutorId: String) {
        viewModelScope.launch {
            try {
                val bookings = repository.getBookingsForTutor(tutorId)
                _studentBookings.value = bookings
            } catch (e: Exception) {
                _studentBookings.value = emptyList()
            }
        }
    }

}
