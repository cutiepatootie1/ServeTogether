package com.main.servetogether.ui.volunteerActivities.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.main.servetogether.data.model.VolunteeringActivity
import com.main.servetogether.ui.volunteerActivities.ActivityViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun OrganizedSection(
    navController: NavController,
    viewModel: ActivityViewModel = viewModel()
) {
    val darkBlue = Color(0xFF0D47A1)

    // Load organized activities
    LaunchedEffect(Unit) {
        viewModel.fetchOrgActivities()
    }

    // Explicitly specify the type for organizedActivities
    val organizedActivities: List<VolunteeringActivity> by viewModel.orgActivities.collectAsState()

    Column(modifier = Modifier.fillMaxWidth()) {
        // Section Header
        Text(
            text = "My Organized Activities",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = darkBlue,
            modifier = Modifier.padding(16.dp)
        )

        // Check if list is empty
        if (organizedActivities.isEmpty()) {
            EmptyOrganizedState(
                onCreateClick = {
                    navController.navigate("create_activity")
                }
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(
                    items = organizedActivities,
                    key = { activity -> activity.id }
                ) { activity ->
                    OrganizedActivityCard(
                        activity = activity,
                        onClick = {
                            navController.navigate("activity_detail/${activity.id}")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun OrganizedActivityCard(
    activity: VolunteeringActivity,
    onClick: () -> Unit
) {
    val darkBlue = Color(0xFF0D47A1)
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val startStr = if (activity.startDate > 0) {
        dateFormat.format(Date(activity.startDate))
    } else {
        "TBD"
    }

    val registrationProgress = if (activity.maximumPersonnel > 0) {
        activity.registeredMembers.size.toFloat() / activity.maximumPersonnel.toFloat()
    } else {
        0f
    }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Title and Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = activity.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = darkBlue
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Status Badge
                StatusBadge(
                    isActive = activity.registeredMembers.size >= activity.minimumPersonnel
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Date and Location
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = darkBlue,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = startStr,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.width(16.dp))

                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = darkBlue,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = activity.location,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Registration Progress
            RegistrationProgress(
                currentCount = activity.registeredMembers.size,
                maxCount = activity.maximumPersonnel,
                minCount = activity.minimumPersonnel,
                progress = registrationProgress,
                darkBlue = darkBlue
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Action Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onClick) {
                    Text("View Details")
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(isActive: Boolean) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isActive)
            Color(0xFF4CAF50).copy(alpha = 0.1f)
        else
            Color(0xFFFF9800).copy(alpha = 0.1f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isActive)
                    Icons.Default.CheckCircle
                else
                    Icons.Default.Schedule,
                contentDescription = null,
                tint = if (isActive)
                    Color(0xFF4CAF50)
                else
                    Color(0xFFFF9800),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = if (isActive) "Active" else "Pending",
                style = MaterialTheme.typography.labelSmall,
                color = if (isActive)
                    Color(0xFF4CAF50)
                else
                    Color(0xFFFF9800),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun RegistrationProgress(
    currentCount: Int,
    maxCount: Int,
    minCount: Int,
    progress: Float,
    darkBlue: Color
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Volunteers Registered",
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray
            )
            Text(
                text = "$currentCount/$maxCount",
                style = MaterialTheme.typography.labelMedium,
                color = darkBlue,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = if (currentCount >= minCount)
                Color(0xFF4CAF50)
            else
                Color(0xFFFF9800),
            trackColor = Color.LightGray.copy(alpha = 0.3f)
        )
    }
}

@Composable
fun EmptyOrganizedState(onCreateClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Empty state icon
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0D47A1).copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.EventNote,
                    contentDescription = null,
                    tint = Color(0xFF0D47A1),
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "No Activities Yet",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0D47A1)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Start organizing your first activity!",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onCreateClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0D47A1)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "CREATE ACTIVITY",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}