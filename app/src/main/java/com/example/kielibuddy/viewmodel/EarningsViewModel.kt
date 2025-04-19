package com.example.kielibuddy.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kielibuddy.model.Booking
import com.example.kielibuddy.model.PaymentSession
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class EarningsViewModel : ViewModel() {

    private val _paymentHistory = MutableStateFlow<List<PaymentSession>>(emptyList())
    val paymentHistory: StateFlow<List<PaymentSession>> = _paymentHistory.asStateFlow()

    fun loadEarningsForTutor(tutorId: String) {
        viewModelScope.launch {
            val db = FirebaseFirestore.getInstance()
            val bookingsSnapshot = db.collection("bookings")
                .whereEqualTo("tutorId", tutorId)
                .get()
                .await()

            val bookings = bookingsSnapshot.documents.mapNotNull { it.toObject(Booking::class.java) }

            val sessions = mutableListOf<PaymentSession>()
            for (booking in bookings) {
                val studentName = try {
                    val userSnapshot = db.collection("users").document(booking.studentId).get().await()
                    val firstName = userSnapshot.getString("firstName") ?: ""
                    val lastName = userSnapshot.getString("lastName") ?: ""
                    "$firstName $lastName"
                } catch (e: Exception) {
                    "Unknown Student"
                }

                val formattedDate = try {
                    val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                    formatter.format(parser.parse(booking.date) ?: Date())
                } catch (e: Exception) {
                    booking.date
                }

                val hours = String.format("%.1f", booking.durationMinutes / 60.0).toDouble()

                sessions.add(
                    PaymentSession(
                        studentId = booking.studentId, // ✅ pass studentId here
                        studentName = studentName,
                        amount = booking.price.toDouble(),
                        date = formattedDate,
                        hours = hours
                    )
                )
            }

            _paymentHistory.value = sessions.sortedByDescending { it.date }
        }
    }
}
