package com.main.servetogether.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.main.servetogether.data.model.VolunteerTask
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class VolTaskRepository {
    private val db = FirebaseFirestore.getInstance("servedb")
    private val collection = db.collection("tasks")

    fun addTask(task: VolunteerTask, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val newDoc = collection.document()
        val taskWithId = task.copy(id = newDoc.id)

        newDoc.set(taskWithId)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun getTasksFlow(activityId: String) : Flow<List<VolunteerTask>> = callbackFlow {
        val listener = collection
            .whereEqualTo("activityId", activityId)
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val tasks = snapshot.toObjects(VolunteerTask::class.java)
                    trySend(tasks)
                }
            }
        awaitClose { listener.remove() }
    }

    fun updateTask(taskId: String, updates: Map<String, Any>) {
        collection.document(taskId).update(updates)
    }

    fun deleteTask(taskId: String) {
        collection.document(taskId).delete()
    }
}