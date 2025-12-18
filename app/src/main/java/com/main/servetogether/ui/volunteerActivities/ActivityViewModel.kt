package com.main.servetogether.ui.volunteerActivities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.main.servetogether.data.model.VolunteeringActivity
import com.main.servetogether.data.repository.VolunteerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// State for Activity Creation
sealed class ActivityCreateState {
    object Idle : ActivityCreateState()
    object Loading : ActivityCreateState()
    data class Success(val activityId: String) : ActivityCreateState()
    data class Error(val message: String) : ActivityCreateState()
}

class ActivityViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance("servedb")
    private val repository = VolunteerRepository()
    private val auth = FirebaseAuth.getInstance()

    // Activity Creation State
    private val _uiState = MutableStateFlow<ActivityCreateState>(ActivityCreateState.Idle)
    val uiState: StateFlow<ActivityCreateState> = _uiState.asStateFlow()

    // All activities (for feed with pagination)
    private val _allActivities = MutableStateFlow<List<VolunteeringActivity>>(emptyList())
    val allActivities: StateFlow<List<VolunteeringActivity>> = _allActivities.asStateFlow()

    // User's registered activities
    private val _userActivities = MutableStateFlow<List<VolunteeringActivity>>(emptyList())
    val userActivities: StateFlow<List<VolunteeringActivity>> = _userActivities.asStateFlow()

    // Activities organized by current user (for organizations)
    private val _orgActivities = MutableStateFlow<List<VolunteeringActivity>>(emptyList())
    val orgActivities: StateFlow<List<VolunteeringActivity>> = _orgActivities.asStateFlow()

    // Current activity details
    private val _currentActivity = MutableStateFlow<VolunteeringActivity?>(null)
    val currentActivity: StateFlow<VolunteeringActivity?> = _currentActivity.asStateFlow()

    // Pagination states
    private val _isEndOfList = MutableStateFlow(false)
    val isEndOfList: StateFlow<Boolean> = _isEndOfList.asStateFlow()

    private val _isError = MutableStateFlow(false)
    val isError: StateFlow<Boolean> = _isError.asStateFlow()

    private var lastDocument: com.google.firebase.firestore.DocumentSnapshot? = null
    private val pageSize = 10

    // Create Activity Function
    fun createActivity(
        title: String,
        description: String,
        location: String,
        startDate: Long,
        endDate: Long,
        minimumPersonnel: Int,
        maximumPersonnel: Int
    ) {
        // Validation
        if (title.isBlank()) {
            _uiState.value = ActivityCreateState.Error("Please enter a title")
            return
        }

        if (location.isBlank()) {
            _uiState.value = ActivityCreateState.Error("Please enter a location")
            return
        }

        if (startDate == 0L) {
            _uiState.value = ActivityCreateState.Error("Please select a start date")
            return
        }

        if (endDate == 0L) {
            _uiState.value = ActivityCreateState.Error("Please select an end date")
            return
        }

        if (endDate < startDate) {
            _uiState.value = ActivityCreateState.Error("End date must be after start date")
            return
        }

        if (minimumPersonnel <= 0) {
            _uiState.value = ActivityCreateState.Error("Minimum personnel must be at least 1")
            return
        }

        if (maximumPersonnel < minimumPersonnel) {
            _uiState.value = ActivityCreateState.Error("Maximum must be greater than or equal to minimum")
            return
        }

        val userId = auth.currentUser?.uid
        if (userId == null) {
            _uiState.value = ActivityCreateState.Error("You must be logged in to create an activity")
            return
        }

        _uiState.value = ActivityCreateState.Loading

        val activity = VolunteeringActivity(
            title = title,
            description = description,
            location = location,
            organizerId = userId,
            startDate = startDate,
            endDate = endDate,
            minimumPersonnel = minimumPersonnel,
            maximumPersonnel = maximumPersonnel,
            registeredMembers = emptyList()
        )

        repository.addActivity(
            activity = activity,
            onSuccess = { activityId ->
                _uiState.value = ActivityCreateState.Success(activityId)
            },
            onFailure = { exception ->
                _uiState.value = ActivityCreateState.Error(
                    exception.message ?: "Failed to create activity"
                )
            }
        )
    }

    fun resetState() {
        _uiState.value = ActivityCreateState.Idle
    }

    // Fetch activities organized by the current user
    fun fetchOrgActivities() {
        val userId = auth.currentUser?.uid

        viewModelScope.launch {
            try {
                val snapshot = db.collection("activities")
                    .whereEqualTo("organizerId", userId)
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .get()
                    .await()

                val activities = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(VolunteeringActivity::class.java)
                }

                _orgActivities.value = activities
            } catch (e: Exception) {
                _orgActivities.value = emptyList()
            }
        }
    }

    // Fetch activities the user has registered for
    fun fetchRegActivities() {
        val userId = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            try {
                val snapshot = db.collection("activities")
                    .whereArrayContains("registeredMembers", userId)
                    .orderBy("startDate", Query.Direction.ASCENDING)
                    .limit(5)
                    .get()
                    .await()

                val activities = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(VolunteeringActivity::class.java)
                }

                _userActivities.value = activities
            } catch (e: Exception) {
                _userActivities.value = emptyList()
            }
        }
    }

    // Fetch all activities user has registered for (full list)
    fun fetchUserActivities() {
        val userId = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            try {
                val snapshot = db.collection("activities")
                    .whereArrayContains("registeredMembers", userId)
                    .orderBy("startDate", Query.Direction.ASCENDING)
                    .get()
                    .await()

                val activities = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(VolunteeringActivity::class.java)
                }

                _userActivities.value = activities
            } catch (e: Exception) {
                _userActivities.value = emptyList()
            }
        }
    }

    // Load next page of activities (for home feed)
    fun loadNextPage() {
        if (_isEndOfList.value) return

        viewModelScope.launch {
            try {
                _isError.value = false

                val query = db.collection("activities")
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .limit(pageSize.toLong())

                val snapshot = if (lastDocument == null) {
                    query.get().await()
                } else {
                    query.startAfter(lastDocument!!).get().await()
                }

                if (snapshot.documents.isEmpty() || snapshot.documents.size < pageSize) {
                    _isEndOfList.value = true
                }

                if (snapshot.documents.isNotEmpty()) {
                    lastDocument = snapshot.documents.last()
                    val newActivities = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(VolunteeringActivity::class.java)
                    }
                    _allActivities.value += newActivities
                }
            } catch (e: Exception) {
                _isError.value = true
            }
        }
    }

    // Load specific activity details
    fun loadActivityDetails(activityId: String) {
        repository.getActivityById(
            activityId = activityId,
            onSuccess = { activity ->
                _currentActivity.value = activity
            },
            onFailure = { exception ->
                _currentActivity.value = null
            }
        )
    }

    // Register user for an activity
    fun registerForActivity(activity: VolunteeringActivity, onResult: (String) -> Unit) {
        val userId = auth.currentUser?.uid

        if (userId == null) {
            onResult("Please log in to register")
            return
        }

        if (activity.registeredMembers.contains(userId)) {
            onResult("You are already registered")
            return
        }

        if (activity.registeredMembers.size >= activity.maximumPersonnel) {
            onResult("Activity is full")
            return
        }

        repository.registerUserForActivity(
            activityId = activity.id,
            userId = userId,
            onSuccess = {
                // Reload activity details
                loadActivityDetails(activity.id)
                onResult("Registration successful!")
            },
            onFailure = { exception ->
                onResult("Failed to register: ${exception.message}")
            }
        )
    }

    // Delete an activity (for organizers)
    fun deleteActivity(activityId: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                db.collection("activities")
                    .document(activityId)
                    .delete()
                    .await()

                // Remove from local list
                _orgActivities.value = _orgActivities.value.filter { it.id != activityId }
                onResult(true)
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }

    // Reset pagination
    fun resetPagination() {
        _allActivities.value = emptyList()
        _isEndOfList.value = false
        _isError.value = false
        lastDocument = null
    }
}