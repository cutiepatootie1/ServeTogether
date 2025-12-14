package com.main.servetogether.ui.homescreen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.main.servetogether.data.model.VolunteeringActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Define the specific dark blue color from the design
val ServeBlueDark = Color(0xFF1E3A5F) // A close match to the design's dark blue

@Composable
fun ActivityListCard(
    activity: VolunteeringActivity,
    navController: NavController
) {
    // Date formatting helper
    val dateFormat = SimpleDateFormat("MM/dd/yy", Locale.getDefault())
    val dateString = if (activity.startDate > 0 && activity.endDate > 0) {
        "${dateFormat.format(Date(activity.startDate))} - ${dateFormat.format(Date(activity.endDate))}"
    } else {
        "TBD"
    }

    Card(
        onClick = {navController.navigate("activity_detail/${activity.id}")},
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp), // Add padding around the card
        shape = RoundedCornerShape(16.dp), // Rounded corners for the card itself
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)) // Light gray background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column {
                // 1. Card Image (Top Section)
                // Replace R.drawable.placeholder_image with your actual image resource
                // or use AsyncImage (Coil) if loading from a URL.
//                Image(
//                    painter = painterResource(id = R.drawable.placeholder_image),
//                    contentDescription = null,
//                    contentScale = ContentScale.Crop,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(200.dp)
//                        // Clip only the top corners of the image to match the card
//                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
//                )

                // 2. Content Section (Bottom Section)
                Column(modifier = Modifier.padding(16.dp)) {

                    // Title and Points Badge Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        // Title
                        Text(
                            text = activity.title,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = ServeBlueDark,
                                fontSize = 22.sp
                            ),
                            modifier = Modifier.weight(1f) // Allow text to take available space
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Date Row
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Date",
                            tint = ServeBlueDark,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = dateString,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = ServeBlueDark
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Location Row
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = ServeBlueDark,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = activity.location,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = ServeBlueDark
                            )
                        )
                    }
                }
            }

        }
    }
}