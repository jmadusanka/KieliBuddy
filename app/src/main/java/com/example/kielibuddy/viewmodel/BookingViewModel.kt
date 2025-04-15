package com.example.kielibuddy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kielibuddy.model.Booking
import com.example.kielibuddy.repository.BookingRepository
import kotlinx.coroutines.launch

class BookingViewModel : ViewModel() {
    private val repository = BookingRepository()

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
}
