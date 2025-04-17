package com.example.kielibuddy.repository

import com.example.kielibuddy.model.Booking
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class BookingRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun createBooking(booking: Booking) {
        val db = FirebaseFirestore.getInstance()

        suspend fun createBooking(booking: Booking) {
            db.collection("bookings")
                .document(booking.id)
                .set(booking)
                .await()
        }
    }

    suspend fun getBookingsForStudent(studentId: String): List<Booking> {
        return try {
            db.collection("bookings")
                .whereEqualTo("studentId", studentId)
                .get()
                .await()
                .documents.mapNotNull { it.toObject(Booking::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }
}