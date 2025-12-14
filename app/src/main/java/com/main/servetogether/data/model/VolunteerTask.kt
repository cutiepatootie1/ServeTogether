package com.main.servetogether.data.model

import com.google.firebase.Timestamp

data class VolunteerTask(
    val id: String = "",
    val activityId: String = "",
    val title: String = "",
    val description: String = "",
    val isCompleted: Boolean = false,
    val createdAt: Timestamp = Timestamp.now()
)
