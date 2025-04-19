package com.example.kielibuddy.model

data class PaymentSession(
    val studentId: String,
    val studentName: String,
    val amount: Double,
    val date: String,
    val hours: Double
)
