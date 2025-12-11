package com.main.servetogether.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val db: FirebaseFirestore by lazy {FirebaseFirestore.getInstance("servedb")}

    // Suspend function means it runs in the background
    suspend fun createAccount(
        email: String,
        pass: String,
        username: String,
        school: String,
        birthdate: Long?,
        role : String
    ): Result<String> {
        return try {
            // 1. Check if username exists
            val userCheck = db.collection("users")
                .whereEqualTo("username", username)
                .get()
                .await() // .await() turns the Firebase callback into a clean coroutine

            if (!userCheck.isEmpty) {
                return Result.failure(Exception("Username already taken"))
            }

            // 2. Create Auth User
            val authResult = auth.createUserWithEmailAndPassword(email, pass).await()
            val uid = authResult.user?.uid ?: throw Exception("User ID null")

            // 3. Save to Firestore
            val userMap = hashMapOf(
                "uid" to uid,
                "username" to username,
                "email" to email,
                "school" to school,
                "birthdate" to birthdate,
                "createdAt" to System.currentTimeMillis(),
                "role" to role
            )

            db.collection("users").document(uid).set(userMap).await()

            // Success!
            Result.success(uid)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Inside AuthRepository class
    suspend fun login(email: String, pass: String): Result<String> {
        return try {
            // This is the standard Firebase Login command
            auth.signInWithEmailAndPassword(email, pass).await()
            Result.success(auth.currentUser?.uid ?: "")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}