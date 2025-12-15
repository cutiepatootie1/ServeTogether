package com.main.servetogether.ui.volunteerActivities.components

import com.main.servetogether.ui.homescreen.components.ActivityListCard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.main.servetogether.ui.volunteerActivities.ActivityViewModel

@Composable
fun OrganizedSection(
    navController: NavController,
    viewModel: ActivityViewModel = viewModel()
) {
    val activities by viewModel.organizedActivities.collectAsState()
    val isEnd by viewModel.isEndOfList.collectAsState()
    val isError by viewModel.isError.collectAsState()

    // Initial Load
    LaunchedEffect(Unit) {
        if (activities.isEmpty()) {
            viewModel.loadNextPage()
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Recently Posted",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp)
        )

        LazyColumn(
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth().height(400.dp)
        ) {
            items(activities.size) { index ->
                val activity = activities[index]
                OrgActivityCard(navController, activity)

                // PAGINATION TRIGGER
                // If scrolled to the last item, load the next page
                if (index == activities.lastIndex && !isEnd) {
                    LaunchedEffect(Unit) {
                        viewModel.fetchOrgActivities()
                    }
                }
            }

            // 2. FOOTER SECTION
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isError) {
                        // --- TIMEOUT / ERROR STATE ---
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Unable to load activities.",
                                color = Color.Red,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            TextButton(onClick = { viewModel.fetchOrgActivities() }) {
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
                    else if (activities.isEmpty() && isEnd) {
                        // --- SHOW THIS WHEN END IS REACHED ---
                        Text(
                            text = "No more activities to show.",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.LightGray
                        )
                    } else if (isEnd) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "You're all caught up!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        // --- SHOW SPINNER WHILE LOADING MORE ---
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}