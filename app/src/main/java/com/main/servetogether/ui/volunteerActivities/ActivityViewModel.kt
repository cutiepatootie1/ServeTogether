package com.main.servetogether.ui.volunteerActivities

import android.util.Log.e
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
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

    private val _allActivities = MutableStateFlow<List<VolunteeringActivity>>(emptyList())
    val allActivities: StateFlow<List<VolunteeringActivity>> = _allActivities
    // Pagination State
    private var lastVisible: DocumentSnapshot? = null
    private val _isEndOfList = MutableStateFlow(false)
    val isEndOfList: StateFlow<Boolean> = _isEndOfList
    private var isLoadingMore = false
    private val _isError = MutableStateFlow(false)
    val isError: StateFlow<Boolean> = _isError

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
// ... inside VolunteerViewModel ...

    // Holds the single activity for the Detail Screen
    private val _currentActivity = MutableStateFlow<VolunteeringActivity?>(null)
    val currentActivity: StateFlow<VolunteeringActivity?> = _currentActivity

    fun loadActivityDetails(activityId: String) {
        // Reset first so we don't show old data
        _currentActivity.value = null

        repository.getActivityById(
            activityId = activityId,
            onSuccess = { activity ->
                _currentActivity.value = activity
            },
            onFailure = { e ->
                // Handle error (maybe add an Error state later)
                android.util.Log.e("DETAIL_ERR", "Could not load activity", e)
            }
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

    fun fetchRegActivities() {
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

    // fetch recent activities associated with the user
    fun fetchUserActivities() {

        db.collection("activities")
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(5)
            .get()
            .addOnSuccessListener { result ->
                val activities = result.toObjects(VolunteeringActivity::class.java)
                _allActivities.value = activities
            }
            .addOnFailureListener { e ->
                // Handle error silently or log it
                android.util.Log.e("Home", "Error fetching recents", e)
            }
    }

    fun registerForActivity(
        activity: VolunteeringActivity,
        onResult: (String) -> Unit // Callback to send message back to UI
    ) {
        val currentUser = auth.currentUser

        // 1. Check Login
        if (currentUser == null) {
            onResult("You need to be logged in to register.")
            return
        }

        // 2. Check if ALREADY registered
        if (activity.registeredMembers.contains(currentUser.uid)) {
            onResult("You are already registered for this activity!")
            return
        }

        // 3. Register
        repository.registerUserForActivity(
            activityId = activity.id,
            userId = currentUser.uid,
            onSuccess = {
                onResult("Successfully Registered!")
                // Optionally refresh data here if you aren't using real-time listeners
                fetchUserActivities()
            },
            onFailure = {
                onResult("Registration failed. Please try again.")
            }
        )
    }

    fun loadNextPage() {
        if (_isEndOfList.value || isLoadingMore) return

        isLoadingMore = true
        _isError.value = false

        val timeoutHandler = android.os.Handler(android.os.Looper.getMainLooper())
        val timeoutRunnable = Runnable {
            if (isLoadingMore) {
                isLoadingMore = false
                _isError.value = true // Trigger timeout error
            }
        }

        // Set timeout for 15 seconds
        timeoutHandler.postDelayed(timeoutRunnable, 15000)

        db.collection("activities")
            .limit(10)
            .get()
            .addOnSuccessListener { result ->
                timeoutHandler.removeCallbacks(timeoutRunnable)
                val activities = result.toObjects(VolunteeringActivity::class.java)
                _allActivities.value = activities
                _uiState.value = ActivityCreateState.Idle
                isLoadingMore = false
            }
            .addOnFailureListener { e ->
                timeoutHandler.removeCallbacks(timeoutRunnable)
                isLoadingMore = false
                _isError.value = true
                _uiState.value = ActivityCreateState.Error(e.message ?: "Failed to load")
            }
    }

    // Call this to wipe and refresh the feed (e.g., Pull-to-Refresh)
    fun refreshFeed() {
        lastVisible = null
        _isEndOfList.value = false
        _allActivities.value = emptyList()
        loadNextPage()
    }

    fun resetState(){
        val _uiState = ActivityCreateState.Idle
    }
}
