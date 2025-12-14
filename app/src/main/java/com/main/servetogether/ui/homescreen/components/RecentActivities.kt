package com.main.servetogether.ui.homescreen.components

import com.main.servetogether.ui.volunteerActivities.ActivityViewModel

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.main.servetogether.data.model.VolunteeringActivity
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun RecentActivitiesSection(
    navController: NavController,
    viewModel: ActivityViewModel = viewModel()
) {
    val activities by viewModel.userActivities.collectAsState()

    // Fetch data when this component appears
    LaunchedEffect(Unit) {
        viewModel.fetchUserActivities()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        // --- HEADER ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Your Recent Activities",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            TextButton(onClick = { navController.navigate("manage_activities") }) {
                Text("See All")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // --- CONTENT ---
        if (activities.isEmpty()) {
            // EMPTY STATE CARD
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(100.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F2F5))
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("You haven't joined any activities yet.", color = Color.Gray)
                }
            }
        } else {
            // HORIZONTAL SCROLL LIST
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(activities) { activity ->
                    ActivityMiniCard(
                        activity = activity,
                        onClick = { navController.navigate("activity_detail/${activity.id}") }
                    )
                }
            }
        }
    }
}

// --- MINI CARD COMPONENT ---
@Composable
fun ActivityMiniCard(
    activity: VolunteeringActivity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(200.dp) // Fixed width for horizontal scrolling
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
//            // 1. IMAGE AREA
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(100.dp)
//                    .background(Color.Gray)
//            ) {
//                if (activity.imageUrl.isNotBlank()) {
//                    AsyncImage(
//                        model = activity.imageUrl,
//                        contentDescription = null,
//                        contentScale = ContentScale.Crop,
//                        modifier = Modifier.fillMaxSize()
//                    )
//                } else {
//                    // Placeholder Text if no image
//                    Text(
//                        text = "No Image",
//                        modifier = Modifier.align(Alignment.Center),
//                        color = Color.White,
//                        fontSize = 12.sp
//                    )
//                }
//            }

            // 2. TEXT CONTENT
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = activity.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Date Row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))

                    val dateStr = if (activity.startDate > 0) {
                        SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date(activity.startDate))
                    } else "TBD"

                    Text(
                        text = dateStr,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}