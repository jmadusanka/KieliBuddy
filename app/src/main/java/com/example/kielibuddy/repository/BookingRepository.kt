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

    suspend fun getBookingsForStudent(studentId: String): List<Booking> {
        return try {
            val snapshot = db.collection("bookings")
                .get()
                .await()

            for (doc in snapshot.documents) {
                //println("📄 Booking: ${doc.id} => ${doc.data}")
            }

            val filtered = snapshot.documents
                .mapNotNull { it.toObject(Booking::class.java) }
                .filter { it.studentId == studentId }
            filtered
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getBookingsForTutor(tutorId: String): List<Booking> {
        return try {
            db.collection("bookings")
                .whereEqualTo("tutorId", tutorId)
                .get()
                .await()
                .documents.mapNotNull { it.toObject(Booking::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

}