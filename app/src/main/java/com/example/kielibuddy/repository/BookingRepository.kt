package com.example.kielibuddy.repository

import com.example.kielibuddy.model.Booking
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class BookingRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun createBooking(booking: Booking) {
        db.collection("bookings")
            .document(booking.id)
            .set(booking)
            .await()
    }

    suspend fun getBookingsForStudent(studentId: String): List<Booking> {
        return try {
            db.collection("bookings")
                .whereEqualTo("studentId", studentId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()
                .documents.mapNotNull { it.toObject(Booking::class.java) }

        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getBookingsForTutor(tutorId: String): List<Booking> {
        return try {
            db.collection("bookings")
                .whereEqualTo("tutorId", tutorId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()
                .documents.mapNotNull { it.toObject(Booking::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

}