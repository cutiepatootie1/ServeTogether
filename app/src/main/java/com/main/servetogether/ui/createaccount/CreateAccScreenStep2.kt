package com.main.servetogether.ui.createaccount

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.main.servetogether.ui.login.RoundedCheckbox
import com.main.servetogether.ui.theme.White
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAccScreenStep2(
    navController: NavController,
    role: String,
    fullName: String,
    email: String,
    password: String,
    viewModel: SignUpViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.signUpState.collectAsState()

    // Step 2 Variables
    var gender by remember { mutableStateOf("") }
    var genderExpanded by remember { mutableStateOf(false) }
    var birthdate by remember { mutableStateOf<Long?>(null) }
    var school by remember { mutableStateOf("") }
    var studentId by remember { mutableStateOf("") }
    var agreeTerms by remember { mutableStateOf(false) }

    val genderOptions = listOf("Male", "Female", "Other", "Prefer not to say")
    val scrollState = rememberScrollState()
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val openDialog = remember { mutableStateOf(false) }
    val showWarningPop = remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = birthdate)

    LaunchedEffect(uiState) {
        when (uiState) {
            is SignUpState.Success -> {
                Toast.makeText(context, "Account Created!", Toast.LENGTH_SHORT).show()
                viewModel.resetState()
                navController.navigate("home_screen/$role") {
                    popUpTo(0) { inclusive = true }
                }
            }
            is SignUpState.Error -> {
                val errorMsg = (uiState as SignUpState.Error).message
                Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(White),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
                .verticalScroll(scrollState)
        ) {
            // Header with Back Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = "Personal Details",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            // Progress Indicator
            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
                ) {
                    Text(
                        "Step 2 of 2",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Text(
                        "100%",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
                LinearProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            // Gender Dropdown
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = "Gender",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium
                )
                Box {
                    OutlinedTextField(
                        value = gender,
                        onValueChange = {},
                        readOnly = true,
                        singleLine = true,
                        trailingIcon = {
                            IconButton(onClick = { genderExpanded = !genderExpanded }) {
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Select Gender"
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .background(color = MaterialTheme.colorScheme.surfaceContainer)
                            .clickable { genderExpanded = true },
                        shape = RoundedCornerShape(12.dp)
                    )
                    DropdownMenu(
                        expanded = genderExpanded,
                        onDismissRequest = { genderExpanded = false },
                        modifier = Modifier.fillMaxWidth(0.85f)
                    ) {
                        genderOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    gender = option
                                    genderExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Birthdate
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .height(65.dp)
            ) {
                OutlinedTextField(
                    value = birthdate?.let { dateFormatter.format(Date(it)) } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Birthdate", color = MaterialTheme.colorScheme.secondary) },
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = MaterialTheme.colorScheme.surface),
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Select Birthdate"
                        )
                    },
                    shape = RoundedCornerShape(12.dp)
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { openDialog.value = true }
                )
            }

            // School Field (Only for volunteers)
            if (role.equals("volunteer", ignoreCase = true)) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = "School",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    OutlinedTextField(
                        value = school,
                        onValueChange = { school = it },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .background(color = MaterialTheme.colorScheme.surfaceContainer),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Student ID
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = "Student ID",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    OutlinedTextField(
                        value = studentId,
                        onValueChange = { studentId = it },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .background(color = MaterialTheme.colorScheme.surfaceContainer),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            // Terms and Conditions
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable { agreeTerms = !agreeTerms }
            ) {
                RoundedCheckbox(
                    checked = agreeTerms,
                    onCheckedChange = { agreeTerms = it },
                    cornerRadius = 5.dp,
                    modifier = Modifier.padding(horizontal = 10.dp)
                )
                Text(
                    "I have read the terms and conditions",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 14.sp
                )
            }

            // Create Account Button
            Button(
                onClick = {
                    if (!agreeTerms) {
                        showWarningPop.value = true
                        return@Button
                    }
                    if (gender.isBlank()) {
                        Toast.makeText(context, "Please select your gender", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    viewModel.signUp(
                        email, password, fullName, school, birthdate,
                        gender, role, studentId
                    )
                },
                enabled = uiState !is SignUpState.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                if (uiState is SignUpState.Loading) {
                    CircularProgressIndicator(color = White)
                } else {
                    Text("Create Account", fontSize = 16.sp)
                }
            }

            // Warning Dialog
            if (showWarningPop.value) {
                AlertDialog(
                    onDismissRequest = { showWarningPop.value = false },
                    title = { Text("Terms and Conditions") },
                    text = { Text("Please read the terms and conditions before proceeding") },
                    confirmButton = {
                        TextButton(onClick = { showWarningPop.value = false }) {
                            Text("Dismiss")
                        }
                    }
                )
            }

            // Date Picker Dialog
            if (openDialog.value) {
                DatePickerDialog(
                    onDismissRequest = { openDialog.value = false },
                    confirmButton = {
                        TextButton(onClick = {
                            datePickerState.selectedDateMillis?.let { birthdate = it }
                            openDialog.value = false
                        }) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { openDialog.value = false }) {
                            Text("Cancel")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }
        }
    }
}