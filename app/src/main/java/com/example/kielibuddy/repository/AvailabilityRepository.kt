package com.example.kielibuddy.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.LocalDate

class AvailabilityRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun saveTutorAvailability(tutorId: String, availabilityMap: Map<LocalDate, List<String>>) {
        val formattedMap = availabilityMap.mapKeys { it.key.toString() } // Convert LocalDate to String
        db.collection("tutor_availability")
            .document(tutorId)
            .set(mapOf("availability" to formattedMap))
            .await()
    }

    suspend fun getTutorAvailability(tutorId: String): Map<LocalDate, List<String>> {
        val snapshot = db.collection("tutor_availability")
            .document(tutorId)
            .get()
            .await()

        val result = mutableMapOf<LocalDate, List<String>>()
        val availability = snapshot.get("availability") as? Map<*, *> ?: return emptyMap()

        for ((key, value) in availability) {
            val date = LocalDate.parse(key as String)
            val slots = (value as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
            result[date] = slots
        }
        return result
    }
}


