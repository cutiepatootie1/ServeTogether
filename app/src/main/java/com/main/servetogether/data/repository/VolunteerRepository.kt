package com.main.servetogether.data.repository

import com.google.firebase.firestore.FirebaseFirestore
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
}