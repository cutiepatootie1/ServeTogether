package com.main.servetogether.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance("servedb") // Use same instance as LoginScreen

    suspend fun createAccount(
        email: String,
        password: String,
        fullName: String,
        gender: String,
        school: String,
        birthdate: Long?,
        role: String,
        studentId: String
    ): Result<Unit> {
        return try {
            // Create authentication account
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: throw Exception("Failed to get user ID")

            // Prepare user data
            val userData = hashMapOf(
                "email" to email,
                "fullName" to fullName,
                "gender" to gender,
                "school" to school,
                "birthdate" to birthdate,
                "role" to role,
                "studentId" to studentId, // Now accepts the actual student ID value
                "createdAt" to System.currentTimeMillis()
            )

            // Save to Firestore
            db.collection("users").document(uid).set(userData).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        auth.signOut()
    }

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }
}