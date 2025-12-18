package com.main.servetogether.ui.homescreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.main.servetogether.navigation.Screen
import com.main.servetogether.shared.UserViewModel
import com.main.servetogether.ui.MenuBar.MenuBar
import com.main.servetogether.ui.homescreen.components.ActivityFeedSection
import com.main.servetogether.ui.homescreen.components.RecentActivitiesSection
import com.main.servetogether.ui.volunteerActivities.ActivityViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    role: String,
    navController: NavController,
    userViewModel: UserViewModel = viewModel(),
    activityViewModel: ActivityViewModel = viewModel()
) {
    val currentRole by userViewModel.userRole.collectAsState()
    val darkBlue = Color(0xFF0D47A1)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Use rememberSaveable to persist across configuration changes
    // Once dismissed, it stays dismissed forever (or until app restart)
    var notificationDismissed by rememberSaveable { mutableStateOf(false) }
    var showNewActivityDialog by remember { mutableStateOf(false) }
    var latestActivityId by remember { mutableStateOf<String?>(null) }

    // Check for new activities only if notification hasn't been dismissed
    LaunchedEffect(currentRole, notificationDismissed) {
        if (currentRole == "volunteer" && !notificationDismissed) {
            activityViewModel.loadNextPage()
        }
    }

    // Monitor for new activities
    val activities by activityViewModel.allActivities.collectAsState()
    LaunchedEffect(activities, notificationDismissed) {
        if (currentRole == "volunteer" && !notificationDismissed && activities.isNotEmpty()) {
            // Show notification only if not dismissed
            latestActivityId = activities.firstOrNull()?.id
            showNewActivityDialog = true
        }
    }

    if (currentRole == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        if (showNewActivityDialog && !notificationDismissed) {
            AlertDialog(
                onDismissRequest = {
                    // Do nothing on outside click - force user to click a button
                },
                title = {
                    Text(
                        text = "New Activity Posted!",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = darkBlue
                    )
                },
                text = {
                    Text(
                        "A new volunteering activity has been posted. Check it out and join if interested!",
                        fontSize = 16.sp
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showNewActivityDialog = false
                            notificationDismissed = true  // Mark as dismissed forever
                            latestActivityId?.let { id ->
                                navController.navigate("activity_detail/$id")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = darkBlue)
                    ) {
                        Text("View Activity", color = Color.White)
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            showNewActivityDialog = false
                            notificationDismissed = true  // Mark as dismissed forever - NEVER SHOW AGAIN
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.LightGray
                        )
                    ) {
                        Text("Later", color = Color.DarkGray)
                    }
                },
                shape = RoundedCornerShape(16.dp),
                containerColor = Color(0xFFF0F2F5)
            )
        }

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    MenuBar(
                        role = currentRole!!,
                        onItemClick = { route ->
                            scope.launch { drawerState.close() }
                            when (route) {
                                "logout" -> {
                                    FirebaseAuth.getInstance().signOut()
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                                "start_new_act" -> navController.navigate("create_activity")
                                else -> navController.navigate(route)
                            }
                        }
                    )
                }
            }
        ) {
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                text = "ServeTogether",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = {
                                scope.launch { drawerState.open() }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Menu",
                                    tint = Color.White
                                )
                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = darkBlue
                        )
                    )
                },
                containerColor = Color(0xFFF0F2F5)
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            RecentActivitiesSection(navController = navController)
                        }
                        item {
                            ActivityFeedSection(navController)
                        }
                    }
                }
            }
        }
    }
}