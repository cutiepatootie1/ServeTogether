package com.main.servetogether.ui.volunteerActivities

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetails(navController: NavController,
                taskId: String,
                viewModel: TaskViewModel = viewModel())
{
    val task by viewModel.currentTask.collectAsState()

    // Local state for editing
    var titleEdit by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // Load data
    LaunchedEffect(taskId) {
        viewModel.loadSingleTask(taskId)
    }

    // Update local state when task loads
    LaunchedEffect(task) {
        task?.let {
            titleEdit = it.title
            description = it.description
        }
    }
    if (task == null) {
        CircularProgressIndicator()
    } else {
        // Now use 'task' safely without '!!'
        val safeTask = task!!

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Task Details") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                        }
                    },
                    actions = {
                        // Delete Button
                        IconButton(onClick = {
                            viewModel.deleteTask(taskId)
                            navController.popBackStack()
                        }) {
                            Icon(
                                Icons.Default.Delete,
                                "Delete",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )
            }
        ) { padding ->
            if (task == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .padding(16.dp)
                        .fillMaxSize()
                ) {
                    // 1. STATUS TOGGLE CARD
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (safeTask.isCompleted) Color(0xFFE8F5E9) else Color(
                                0xFFFFF3E0
                            )
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { viewModel.toggleTaskStatus(safeTask) }) {
                                Icon(
                                    imageVector = if (safeTask.isCompleted) Icons.Default.CheckCircle else Icons.Outlined.Circle,
                                    contentDescription = null,
                                    tint = if (safeTask.isCompleted) Color(0xFF4CAF50) else Color(
                                        0xFFFF9800
                                    ),
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = if (safeTask.isCompleted) "Completed" else "Pending",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "Tap icon to toggle status",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 2. EDITABLE FIELDS
                    Text(
                        "Edit Details",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = titleEdit,
                        onValueChange = { titleEdit = it },
                        label = { Text("Task Title") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // 3. SAVE BUTTON
                    Button(
                        onClick = {
                            viewModel.updateTaskDetails(taskId, titleEdit, description)
                            navController.popBackStack()
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Text("Save Changes")
                    }
                }
            }
        }
    }
}