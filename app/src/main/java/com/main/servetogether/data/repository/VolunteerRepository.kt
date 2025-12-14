package com.main.servetogether.data.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.main.servetogether.data.model.VolunteeringActivity

class VolunteerRepository {
    private val db = FirebaseFirestore.getInstance("servedb")
    private val collection = db.collection("activities")

    fun addActivity(
        activity: VolunteeringActivity,
        onSuccess: (String) -> Unit,
        onFailure: (Exception)-> Unit
    ){
        val newDocRef = collection.document()
        val newId = newDocRef.id
        val activityWithId = activity.copy(id = newDocRef.id)

        newDocRef.set(activityWithId)
            .addOnSuccessListener { onSuccess(newId) }
            .addOnFailureListener { e -> onFailure(e) }
    }
    fun registerUserForActivity(
        activityId: String,
        userId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("activities").document(activityId)
            .update("registeredMembers", FieldValue.arrayUnion(userId))
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    // Get a single activity by ID
    fun getActivityById(
        activityId: String,
        onSuccess: (VolunteeringActivity) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("activities").document(activityId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val activity = document.toObject(VolunteeringActivity::class.java)
                    if (activity != null) {
                        onSuccess(activity)
                    } else {
                        onFailure(Exception("Parsing error"))
                    }
                } else {
                    onFailure(Exception("Activity not found"))
                }
            }
            .addOnFailureListener { e -> onFailure(e) }
    }
}