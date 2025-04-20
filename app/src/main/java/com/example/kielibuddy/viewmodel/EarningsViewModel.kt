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
            val purchasesSnapshot = db.collection("purchases")
                .whereEqualTo("tutorId", tutorId)
                .get()
                .await()

            val sessions = purchasesSnapshot.documents.mapNotNull { doc ->
                val data = doc.data ?: return@mapNotNull null
                val amount = (data["amount"] as? Number)?.toDouble()?.div(100.0) ?: return@mapNotNull null
                val studentId = data["studentId"] as? String ?: return@mapNotNull null

                // You can later fetch user data if needed
                val studentName = try {
                    val userSnapshot = db.collection("users").document(studentId).get().await()
                    val firstName = userSnapshot.getString("firstName") ?: ""
                    val lastName = userSnapshot.getString("lastName") ?: ""
                    "$firstName $lastName"
                } catch (e: Exception) {
                    "Unknown Student"
                }

                // Format the timestamp into a readable date
                val timestamp = (data["timestamp"] as? Number)?.toLong() ?: 0L
                val formattedDate = try {
                    val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                    formatter.format(Date(timestamp))
                } catch (e: Exception) {
                    "Unknown"
                }

                PaymentSession(
                    studentId = studentId,
                    studentName = studentName,
                    amount = amount,
                    date = formattedDate,
                    hours = 1.0 // or 0.0 if not tracked in purchases
                )
            }

            _paymentHistory.value = sessions.sortedByDescending { it.date }
        }
    }
}