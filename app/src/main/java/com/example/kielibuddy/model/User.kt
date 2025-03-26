package com.example.kielibuddy.model

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val photoUrl: String = "",
    val subscription: String = ""
) {
    companion object {
        lateinit var value: User
    }
}