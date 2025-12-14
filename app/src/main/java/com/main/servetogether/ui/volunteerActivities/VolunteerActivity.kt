package com.main.servetogether.ui.volunteerActivities

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.main.servetogether.data.model.VolunteerTask
import com.main.servetogether.data.model.VolunteeringActivity
import com.main.servetogether.navigation.Screen
import com.main.servetogether.shared.UserViewModel
import com.main.servetogether.ui.MenuBar.MenuBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VolunteerActivity(
    navController: NavController,
    activityId: String,
    viewModel: ActivityViewModel = viewModel(),
    userViewModel: UserViewModel = viewModel()
) {
    val currentRole by userViewModel.userRole.collectAsState()
    val serveBlue = Color(0xFF004481)
    val backgroundLight = Color(0xFFF5F5F5)
    val activityD by viewModel.currentActivity.collectAsState()
//    val activitiesList by viewModel.userActivities.collectAsState()
//    val activity = activitiesList.find { it.id == activityId }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadActivityDetails(activityId)
        viewModel.fetchUserActivities()
    }
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                MenuBar(
                    role = currentRole ?: "user",
                    onItemClick = { route ->
                        scope.launch { drawerState.close() }
                        if (route == "logout") {
                            FirebaseAuth.getInstance().signOut()
                            navController.navigate(Screen.Login.route)
                        } else if (route == "start_new_act") {
                            navController.navigate("create_activity")
                        } else {
                            navController.navigate(route)
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
                    actions = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Or appropriate back icon
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = serveBlue
                    )
                )
            },
            containerColor = backgroundLight
        ) { paddingValues ->
            if (activityD == null) {
                // Show loading or error if not found
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Loading activity details...")
                }
            } else {
                val activity = activityD!!
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp), // General padding for the content
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // 1. TITLE
                    Text(
                        text = activity.title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2C2C2C)
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // 2. IMAGE
                    // Replace R.drawable.placeholder_image with your actual image resource
                    // Or use AsyncImage (Coil) if loading from URL
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        modifier = Modifier.fillMaxWidth().height(200.dp)
                    ) {
                        // Placeholder using a Box with color if you don't have an image yet
                        Box(
                            modifier = Modifier.fillMaxSize().background(Color.Gray),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Activity Image", color = Color.White)
                            /* Uncomment this when you have an image
                    Image(
                        painter = painterResource(id = R.drawable.your_image),
                        contentDescription = "Activity Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    */
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // 3. REGISTERED COUNT (Replaces Completion Level)
                    Text(
                        text = "Number of registered students", // Fixed grammar from "Numbered"
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = serveBlue
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // The Circle Badge
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(serveBlue)
                    ) {
                        Text(
                            text = "${activity.registeredMembers.size}",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // 4. DESCRIPTION SECTION
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = activity.description,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = serveBlue
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        val dateFormat =
                            java.text.SimpleDateFormat("MM/dd/yy", java.util.Locale.getDefault())
                        val startStr =
                            if (activity.startDate > 0) dateFormat.format(java.util.Date(activity.startDate)) else "TBD"
                        val endStr =
                            if (activity.endDate > 0) dateFormat.format(java.util.Date(activity.endDate)) else "TBD"

                        // Details List
                        DetailRow("Minimum Personnel:", "${activity.minimumPersonnel}", serveBlue)
                        DetailRow("Maximum Personnel:", "${activity.maximumPersonnel}", serveBlue)
                        DetailRow("Date:", "$startStr - $endStr", serveBlue)
                        DetailRow("Location:", activity.location, serveBlue)
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    // 5. REGISTER BUTTON
                    Button(
                        onClick = {
                            viewModel.registerForActivity(
                                activity = activity,
                                onResult = { message ->
                                    // This runs when the logic finishes (Success or Failure)
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                }
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = serveBlue),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        val isRegistered = activity.registeredMembers.contains(FirebaseAuth.getInstance().uid)

                        Text(
                            text = if (isRegistered) "REGISTERED" else "REGISTER",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp)) // Bottom spacing

                    TaskListSection(activityId, onClick = { taskId ->
                        navController.navigate("task_detail/${taskId}")
                    } )
                }
            }
        }
    }
}

@Composable
fun TaskListSection(
    activityId: String,
    viewModel: TaskViewModel = viewModel(), // Inject TaskViewModel
    userViewModel: UserViewModel = viewModel(),
    onClick: (String) -> Unit
) {
    val currentRole by userViewModel.userRole.collectAsState()
    // 1. Observe the list
    val tasks by viewModel.tasks.collectAsState()
    var showDialog by remember { mutableStateOf(false)}

    // 2. Load data once
    LaunchedEffect(activityId) {
        viewModel.loadTasks(activityId)
    }

    Column {
        Text("Tasks", style = MaterialTheme.typography.titleMedium)

        // LIST OF TASKS
        tasks.forEach { task ->
            if (tasks.isEmpty()) {
                Text("No tasks yet.", color = Color.Gray, modifier = Modifier.padding(vertical = 8.dp))
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                        .clickable{onClick(task.id)}
                ) {
                    // Checkbox for Update
                    Checkbox(
                        checked = task.isCompleted,
                        onCheckedChange = { viewModel.toggleTaskStatus(task) }
                    )

                    Text(
                        text = task.title,
                        modifier = Modifier.weight(1f),
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null
                    )
                    if(currentRole == "organization") {
                        // Delete Button
                        IconButton(onClick = { viewModel.deleteTask(task.id) }) {
                            Icon(Icons.Default.Delete, "Delete")
                        }
                    }
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, "Details", tint = Color.LightGray)
                }
            }
        }
        if(currentRole == "organization") {
            TextButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Task")
            }
        }

        // 3. SHOW DIALOG IF STATE IS TRUE
        if (showDialog) {
            AddTaskDialog(
                onDismiss = { showDialog = false },
                onConfirm = { title, dihscription ->
                    viewModel.addTasks(title, dihscription, activityId)
                    showDialog = false // Close dialog after adding
                }
            )
        }
    }
}

@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, time: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "New Task") },
        text = {
            Column {
                Text("What needs to be done?", style = MaterialTheme.typography.bodyMedium)

                Spacer(modifier = Modifier.height(16.dp))

                // 1. Task Title Input
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 2. Estimated Time Input
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Enter instructions or what it entails") },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(title, description) },
                enabled = title.isNotBlank() // Disable if title is empty
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// Helper Composable for the description rows to keep code clean
@Composable
fun DetailRow(label: String, value: String, labelColor: Color) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = "$label ",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = labelColor
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.DarkGray
            )
        )
    }
}