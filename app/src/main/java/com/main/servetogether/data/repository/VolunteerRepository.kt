package com.main.servetogether.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.main.servetogether.data.model.VolunteeringActivity

class VolunteerRepository {
    private val db = FirebaseFirestore.getInstance("servedb")
    private val collection = db.collection("activities")

    fun addActivity(
        activity: VolunteeringActivity,
        onSuccess: () -> Unit,
        onFailure: (Exception)-> Unit
    ){
        val newDocRef = collection.document()
        val activityWithId = activity.copy(id = newDocRef.id)

        newDocRef.set(activityWithId)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }
}