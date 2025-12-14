package com.main.servetogether.ui.volunteerActivities

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.ui.graphics.Color
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.main.servetogether.R
import com.main.servetogether.navigation.Screen
import com.main.servetogether.shared.UserViewModel
import com.main.servetogether.ui.MenuBar.MenuBar
import com.main.servetogether.ui.theme.White
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateActivityScreen(navController: NavController,
                         viewModel: ActivityViewModel = viewModel(),
                         userViewModel: UserViewModel = viewModel(),
                         role: String)
{
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val currentRole by userViewModel.userRole.collectAsState()

    LaunchedEffect(uiState) {
        when (uiState) {
            is ActivityCreateState.Success -> {
                val newId = (uiState as ActivityCreateState.Success).activityId
              Toast.makeText(context, "New activity created",Toast.LENGTH_SHORT)
                navController.navigate("activity_detail/$newId"){
                    popUpTo("create_activity"){inclusive = true}
                }
                viewModel.resetState()
            }
            is ActivityCreateState.Error -> {
                val errorMsg = (uiState as ActivityCreateState.Error).message
            }

            else -> {}
        }
    }

    // FORM VARIABLES
    var title by remember { mutableStateOf(("")) }
    var description by remember { mutableStateOf(("")) }
    var location by remember { mutableStateOf(("")) }
    var startDate by remember { mutableStateOf<Long?>(null)  }
    var endDate by remember { mutableStateOf<Long?>(null)  }
    var minimumPersonnel by remember { mutableStateOf(("")) }
    var maximumPersonnel by remember {mutableStateOf((""))}

    // Control Dialog Visibility
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }

    //UI STATE
    val showWarningPop = remember {mutableStateOf(false)}
    val scrollState = rememberScrollState()
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val openDialog = remember { mutableStateOf(false) }
    val startDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = startDate
    )
    val endDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = endDate
    )
    // FRONT END HERE

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
                        } else {
                            navController.navigate(route)
                        }
                    }
                )
            }
        }
    ) {
//        Scaffold(
//            topBar = {
//                val darkBlue = Color(0xFF0D47A1)
//                CenterAlignedTopAppBar(
//                    title = {
//                        Text(
//                            text = "Create Activity",
//                            color = Color.White,
//                            fontWeight = FontWeight.Bold
//                        )
//                    },
//                    navigationIcon = {
//                        IconButton(onClick = {
//                            scope.launch { drawerState.open() }
//                        }) {
//                            Icon(
//                                imageVector = Icons.Default.Menu,
//                                contentDescription = "Menu",
//                                tint = Color.White
//                            )
//                        }
//                    },
//                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
//                        containerColor = darkBlue
//                    )
//                )
//            },
//            containerColor = Color(0xFFF0F2F5),
//            ) { padding ->
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = { Text("Create Activity") },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            ) { padding ->
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .padding(16.dp)
                        .fillMaxSize()
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // START OF FORM
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth().height(120.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Location") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    // --- DATE PICKERS ROW ---
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Start Date Input
                        Box(modifier = Modifier.weight(1f).padding(end = 4.dp)) {
                            OutlinedTextField(
                                value = startDate?.let { dateFormatter.format(Date(it)) } ?: "",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Start Date") },
                                trailingIcon = { Icon(Icons.Default.CalendarToday, null) },
                                modifier = Modifier.fillMaxWidth()
                            )
                            // Invisible box to catch clicks
                            Box(
                                modifier = Modifier.matchParentSize()
                                    .clickable { showStartPicker = true })
                        }

                        // End Date Input
                        Box(modifier = Modifier.weight(1f).padding(start = 4.dp)) {
                            OutlinedTextField(
                                value = endDate?.let { dateFormatter.format(Date(it)) } ?: "",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("End Date") },
                                trailingIcon = { Icon(Icons.Default.CalendarToday, null) },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Box(
                                modifier = Modifier.matchParentSize()
                                    .clickable { showEndPicker = true })
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    // --- PERSONNEL LIMITS ROW ---
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedTextField(
                            value = minimumPersonnel,
                            onValueChange = {
                                if (it.all { char -> char.isDigit() }) minimumPersonnel = it
                            },
                            label = { Text("Min People") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f).padding(end = 4.dp)
                        )

                        OutlinedTextField(
                            value = maximumPersonnel,
                            onValueChange = {
                                if (it.all { char -> char.isDigit() }) maximumPersonnel = it
                            },
                            label = { Text("Max People") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f).padding(start = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(32.dp))

                    // --- SUBMIT BUTTON ---
                    Button(
                        onClick = {
                            // Convert Strings to Ints safely
                            val min = minimumPersonnel.toIntOrNull() ?: 0
                            val max = maximumPersonnel.toIntOrNull() ?: 0

                            // Call the ViewModel function we created earlier
                            viewModel.createActivity(
                                title = title,
                                description = description,
                                location = location,
                                startDate = startDate ?: 0L,
                                endDate = endDate ?: 0L,
                                minimumPersonnel = min,
                                maximumPersonnel = max
                            )
                        },
                        enabled = uiState !is ActivityCreateState.Loading, // Disable while loading
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        if (uiState is ActivityCreateState.Loading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text("Post Activity")
                        }
                    }
                }
            }
            // --- DATE PICKER DIALOGS ---

            if (showStartPicker) {
                val dateState = rememberDatePickerState()
                DatePickerDialog(
                    onDismissRequest = { showStartPicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            startDate = dateState.selectedDateMillis
                            showStartPicker = false
                        }) { Text("OK") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showStartPicker = false }) { Text("Cancel") }
                    }
                ) {
                    DatePicker(state = dateState)
                }
            }

            if (showEndPicker) {
                val dateState = rememberDatePickerState()
                DatePickerDialog(
                    onDismissRequest = { showEndPicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            endDate = dateState.selectedDateMillis
                            showEndPicker = false
                        }) { Text("OK") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showEndPicker = false }) { Text("Cancel") }
                    }
                ) {
                    DatePicker(state = dateState)
                }
            }
        }

}


