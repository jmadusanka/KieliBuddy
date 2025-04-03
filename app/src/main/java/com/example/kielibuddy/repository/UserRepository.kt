package com.example.kielibuddy.repository

import com.example.kielibuddy.model.UserModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val fireStore = FirebaseFirestore.getInstance()

    suspend fun getUserDetails(userId: String): UserModel? {
        return try {
            val document = fireStore.collection("users").document(userId).get().await()
            document.toObject(UserModel::class.java) // Convert Firestore data to UserModel
        } catch (e: Exception) {
            null
        }
    }
}