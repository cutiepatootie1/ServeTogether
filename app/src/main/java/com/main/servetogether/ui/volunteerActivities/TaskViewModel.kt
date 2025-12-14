package com.main.servetogether.ui.volunteerActivities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.main.servetogether.data.model.VolunteerTask
import com.main.servetogether.data.repository.VolTaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TaskViewModel : ViewModel() {
    private val repository = VolTaskRepository()

    private val _tasks = MutableStateFlow<List<VolunteerTask>>(emptyList())
    val tasks: StateFlow<List<VolunteerTask>> = _tasks
    private val _currentTask = MutableStateFlow<VolunteerTask?>(null)
    val currentTask: StateFlow<VolunteerTask?> = _currentTask

    fun loadTasks(activityId: String) {
        viewModelScope.launch {
            repository.getTasksFlow(activityId).collect { listOfTasks ->
                _tasks.value = listOfTasks
            }
        }
    }

    fun loadSingleTask(taskId: String) {
        // Listen to the specific document
        repository.getTasksFlow(taskId) // We need a direct doc listener here, let's simplify:

        // Direct Firestore listener for one document
        FirebaseFirestore.getInstance("servedb").collection("tasks").document(taskId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener
                if (snapshot != null && snapshot.exists()) {
                    val task = snapshot.toObject(VolunteerTask::class.java)
                    _currentTask.value = task
                }
            }
    }

    fun addTasks(title: String, description: String, activityId: String){
        if (title.isBlank()) return

        val newTask = VolunteerTask(
            title = title,
            description = description,
            activityId = activityId,
            isCompleted = false
        )

        repository.addTask(newTask, {}, {})
    }

    // 3. TOGGLE COMPLETE
    fun toggleTaskStatus(task: VolunteerTask) {
        _currentTask.value = task.copy(isCompleted = !task.isCompleted)
        repository.updateTask(task.id, mapOf("isCompleted" to !task.isCompleted))
    }

    // 4. DELETE TASK
    fun deleteTask(taskId: String) {
        repository.deleteTask(taskId)
    }

    fun register(uid: String) {

    }

    fun updateTaskDetails(taskId: String, newTitle: String, newDih: String) {
        val updates = mapOf(
            "title" to newTitle,
            "description" to newDih
        )
        repository.updateTask(taskId, updates)
    }
}