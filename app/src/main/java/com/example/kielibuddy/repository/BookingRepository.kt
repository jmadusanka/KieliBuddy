package com.example.kielibuddy.repository

import com.example.kielibuddy.model.Booking
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class BookingRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun createBooking(booking: Booking) {
        db.collection("bookings")
            .document(booking.id)
            .set(booking)
            .await()
    }
}