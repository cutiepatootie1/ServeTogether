package com.main.servetogether.ui.volunteerActivities

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.main.servetogether.data.model.VolunteeringActivity
import com.main.servetogether.data.repository.VolunteerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed class ActivityCreateState {
    object Idle : ActivityCreateState()
    object Loading : ActivityCreateState()
    object Success : ActivityCreateState()
    data class Error(val message: String) : ActivityCreateState()
}
class ActivityViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<ActivityCreateState>(ActivityCreateState.Idle)
    val uiState: StateFlow<ActivityCreateState> = _uiState

    val auth = FirebaseAuth.getInstance()

    val repository = VolunteerRepository()

    fun createActivity(
        title: String,
        description: String,
        location: String,
        startDate: Long,
        endDate: Long,
        minimumPersonnel: Int,
        maximumPersonnel: Int,
    ){
        if (title.isBlank() || description.isBlank()){
            _uiState.value = ActivityCreateState.Error("Please fill in fields")
            return
        }

        val currentUser = auth.currentUser
        if (currentUser == null){
            _uiState.value = ActivityCreateState.Error("Not logged in")
            return
        }

        val initialMembers = listOf(currentUser.uid)

        val newActivity = VolunteeringActivity(
            title = title,
            description = description,
            location = location,
            startDate = startDate,
            endDate = endDate,
            minimumPersonnel = minimumPersonnel,
            maximumPersonnel = maximumPersonnel,
            organizerId = currentUser.uid,
            registeredMembers = initialMembers // adds current user to the member list
        )

        _uiState.value = ActivityCreateState.Loading
        repository.addActivity(newActivity,
            onSuccess = {_uiState.value = ActivityCreateState.Success},
            onFailure = {e -> _uiState.value = ActivityCreateState.Error(e.message ?: "Error")}
            )
    }

    fun resetState(){
        val _uiState = ActivityCreateState.Idle
    }
}