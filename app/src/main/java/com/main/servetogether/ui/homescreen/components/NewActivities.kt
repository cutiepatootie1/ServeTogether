package com.main.servetogether.ui.homescreen.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.main.servetogether.ui.volunteerActivities.ActivityViewModel

@Composable
fun ActivityFeedSection(
    navController: NavController,
    viewModel: ActivityViewModel = viewModel()
) {
    val activities by viewModel.allActivities.collectAsState()
    val isEnd by viewModel.isEndOfList.collectAsState()
    val isError by viewModel.isError.collectAsState()

    // Initial Load
    LaunchedEffect(Unit) {
        if (activities.isEmpty() && !isEnd) {
            viewModel.loadNextPage()
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Recently Posted",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        // Activities List
        if (activities.isEmpty() && !isEnd && !isError) {
            // Loading initial content
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                activities.forEach { activity ->
                    ActivityListCard(activity, navController)
                }

                // Footer: Loading indicator or end message
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        isError -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Unable to load activities.",
                                    color = Color.Red,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                TextButton(onClick = { viewModel.loadNextPage() }) {
                                    Icon(
                                        Icons.Default.Refresh,
                                        null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Retry")
                                }
                            }
                        }
                        activities.isEmpty() && isEnd -> {
                            Text(
                                text = "No activities available yet.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                        }
                        isEnd -> {
                            Text(
                                text = "You're all caught up!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        else -> {
                            // Not at end, show load more button
                            Button(
                                onClick = { viewModel.loadNextPage() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF0D47A1)
                                )
                            ) {
                                Text("Load More")
                            }
                        }
                    }
                }
            }
        }
    }
}