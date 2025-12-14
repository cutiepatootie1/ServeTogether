package com.main.servetogether.data.model

import com.google.firebase.Timestamp
data class VolunteeringActivity(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val location: String = "",
    val organizerId: String = "",
    val startDate: Long = 0L,
    val endDate: Long = 0L,
    val minimumPersonnel: Int = 0,
    val maximumPersonnel: Int = 0,
    val registeredMembers: List<String> = emptyList(),
    val createdAt: Timestamp = Timestamp.now()
)
