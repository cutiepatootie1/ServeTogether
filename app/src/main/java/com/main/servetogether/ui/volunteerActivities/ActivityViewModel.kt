package com.main.servetogether.ui.volunteerActivities

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.main.servetogether.data.model.VolunteeringActivity
import com.main.servetogether.data.repository.VolunteerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed class ActivityCreateState {
    object Idle : ActivityCreateState()
    object Loading : ActivityCreateState()
    data class Success(val activityId: String) : ActivityCreateState()
    data class Error(val message: String) : ActivityCreateState()
}
class ActivityViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<ActivityCreateState>(ActivityCreateState.Idle)
    val uiState: StateFlow<ActivityCreateState> = _uiState

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance("servedb")

    val repository = VolunteerRepository()
    private val _userActivities = MutableStateFlow<List<VolunteeringActivity>>(emptyList())
    val userActivities: StateFlow<List<VolunteeringActivity>> = _userActivities

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
            onSuccess = {newId -> _uiState.value = ActivityCreateState.Success(newId)},
            onFailure = {e -> _uiState.value = ActivityCreateState.Error(e.message ?: "Error")}
            )
    }

    // 2. FETCH ACTIVITIES (Only the ones created by this user)
    fun fetchOrgActivities() {
        _uiState.value = ActivityCreateState.Loading
        val currentUser = auth.currentUser ?: return


        db.collection("activities")
            // Query: Only show what *I* organized
            .whereEqualTo("organizerId", currentUser.uid)
            .get()
            .addOnSuccessListener { result ->
                val activities = result.toObjects(VolunteeringActivity::class.java)
                _userActivities.value = activities
                _uiState.value = ActivityCreateState.Idle
            }
            .addOnFailureListener { e ->
                _uiState.value = ActivityCreateState.Error(e.message ?: "Failed to load")
            }
    }

    // fetch recent activities associated with the user
    fun fetchUserActivities() {
        val currentUser = auth.currentUser ?: return

        db.collection("activities")
            .whereArrayContains("registeredMembers", currentUser.uid)
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(5)
            .get()
            .addOnSuccessListener { result ->
                val activities = result.toObjects(VolunteeringActivity::class.java)
                _userActivities.value = activities
            }
            .addOnFailureListener { e ->
                // Handle error silently or log it
                android.util.Log.e("Home", "Error fetching recents", e)
            }
    }

    fun resetState(){
        val _uiState = ActivityCreateState.Idle
    }
}
