package com.main.servetogether.ui.volunteerActivities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.main.servetogether.data.model.VolunteerTask
import com.main.servetogether.data.repository.VolTaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TaskViewModel : ViewModel() {
    private val repository = VolTaskRepository()

    private val _tasks = MutableStateFlow<List<VolunteerTask>>(emptyList())
    val tasks: StateFlow<List<VolunteerTask>> = _tasks

    fun loadTasks(activityId: String) {
        viewModelScope.launch {
            repository.getTasksFlow(activityId).collect { listOfTasks ->
                _tasks.value = listOfTasks
            }
        }
    }

    fun addTasks(title: String, description: String, activityId: String){
        if (title.isBlank()) return

        val newTask = VolunteerTask(
            title = title,
            description = title,
            activityId = activityId,
            isCompleted = false
        )

        repository.addTask(newTask, {}, {})
    }

    // 3. TOGGLE COMPLETE
    fun toggleTaskStatus(task: VolunteerTask) {
        repository.updateTask(task.id, mapOf("isCompleted" to !task.isCompleted))
    }

    // 4. DELETE TASK
    fun deleteTask(taskId: String) {
        repository.deleteTask(taskId)
    }
}