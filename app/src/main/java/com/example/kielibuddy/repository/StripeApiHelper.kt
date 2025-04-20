package com.example.kielibuddy.repository
import android.util.Log
import com.example.kielibuddy.model.Booking
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

suspend fun createStripeCheckoutSession(
    amountInCents: Int,
    booking: Booking,
    onSuccess: (String) -> Unit,
    onError: (String) -> Unit
) {
    withContext(Dispatchers.IO) {
        try {
            val url = "https://us-central1-kielibudy.cloudfunctions.net/createCheckoutSession"

            val jsonBody = JSONObject().apply {
                put("amount", amountInCents)
                put("successUrl", "https://us-central1-kielibudy.cloudfunctions.net/paymentSuccessRedirect")
                put("cancelUrl", "https://example.com/cancel")
                put("metadata", JSONObject().apply {
                    put("tutorId", booking.tutorId)
                    put("studentId", booking.studentId)
                    put("date", booking.date)
                    put("timeSlot", booking.timeSlot)
                    put("durationMinutes", booking.durationMinutes.toString())
                    put("price", booking.price.toString())
                    put("lessonType", booking.lessonType.name)
                })
            }

            val body = jsonBody.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder().url(url).post(body).build()
            val client = OkHttpClient()

            val response = client.newCall(request).execute()
            val responseString = response.body?.string()

            Log.d("StripeCheckout", "Response Code: ${response.code}")
            Log.d("StripeCheckout", "Response Body: $responseString")

            if (response.isSuccessful && !responseString.isNullOrEmpty()) {
                val result = JSONObject(responseString)
                val checkoutUrl = result.getString("url")
                onSuccess(checkoutUrl)
            } else {
                onError("Stripe error: ${response.code} - $responseString")
            }
        } catch (e: Exception) {
            Log.e("StripeCheckout", "Exception occurred", e)
            onError(e.localizedMessage ?: "Unexpected error")
        }
    }
}
