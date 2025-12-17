package com.main.servetogether.ui.ProfileScreen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.main.servetogether.shared.ProfileState
import com.main.servetogether.shared.UserViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateProfileScreen(
    navController: NavController,
    viewModel: UserViewModel = viewModel()
) {
    val context = LocalContext.current
    val darkBlue = Color(0xFF0D47A1)

    // Collect states from ViewModel
    val profileState by viewModel.profileState.collectAsState()

    // Local state for form fields - ALL EDITABLE
    var fullName by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var genderExpanded by remember { mutableStateOf(false) }
    var dateOfBirth by remember { mutableStateOf("") }
    var school by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var studentId by remember { mutableStateOf("") }

    val genderOptions = listOf("Male", "Female", "Other", "Prefer not to say")
    val scrollState = rememberScrollState()
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val openDialog = remember { mutableStateOf(false) }
    var birthdate by remember { mutableStateOf<Long?>(null) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = birthdate
    )

    // Load profile data when screen opens
    LaunchedEffect(Unit) {
        viewModel.loadProfileData()
    }

    // Update local state when ViewModel data changes
    LaunchedEffect(viewModel.fullName, viewModel.gender, viewModel.dateOfBirth,
        viewModel.school, viewModel.email, viewModel.studentId) {
        fullName = viewModel.fullName
        gender = viewModel.gender
        dateOfBirth = viewModel.dateOfBirth
        school = viewModel.school
        email = viewModel.email
        studentId = viewModel.studentId

        // Parse dateOfBirth string to Long for DatePicker
        try {
            if (dateOfBirth.isNotEmpty()) {
                birthdate = dateFormatter.parse(dateOfBirth)?.time
            }
        } catch (e: Exception) {
            // If parsing fails, keep birthdate as null
        }
    }

    // Show success/error messages and navigate
    LaunchedEffect(profileState) {
        when (profileState) {
            is ProfileState.Success -> {
                if (viewModel.fullName == fullName &&
                    viewModel.gender == gender &&
                    viewModel.school == school &&
                    viewModel.email == email &&
                    viewModel.studentId == studentId) {
                    // Don't show toast on initial load
                } else {
                    Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                    // Navigate back to profile screen
                    navController.popBackStack()
                }
            }
            is ProfileState.Error -> {
                val error = (profileState as ProfileState.Error).message
                Toast.makeText(context, "Error: $error", Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Edit Profile",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
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
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Update Your Information",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Profile Picture
                Box(contentAlignment = Alignment.BottomEnd) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(140.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray),
                        tint = Color.White
                    )
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(2.dp, Color.White, CircleShape)
                            .align(Alignment.BottomEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = "Change Picture",
                            tint = darkBlue,
                            modifier = Modifier
                                .padding(8.dp)
                                .align(Alignment.Center)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Form Fields - ALL EDITABLE
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    // Full Name - EDITABLE
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = profileState !is ProfileState.Loading,
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Gender Dropdown - EDITABLE
                    Box {
                        OutlinedTextField(
                            value = gender,
                            onValueChange = {},
                            label = { Text("Gender") },
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = {
                                    if (profileState !is ProfileState.Loading) {
                                        genderExpanded = !genderExpanded
                                    }
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Select Gender"
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (profileState !is ProfileState.Loading) {
                                        genderExpanded = true
                                    }
                                },
                            enabled = profileState !is ProfileState.Loading
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
                    Spacer(modifier = Modifier.height(12.dp))

                    // Date of Birth with DatePicker - EDITABLE
                    Box(
                        modifier = Modifier.height(65.dp)
                    ) {
                        OutlinedTextField(
                            value = birthdate?.let { dateFormatter.format(Date(it)) } ?: dateOfBirth,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Date of Birth") },
                            modifier = Modifier.fillMaxSize(),
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = "Select Date"
                                )
                            },
                            enabled = profileState !is ProfileState.Loading
                        )

                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable {
                                    if (profileState !is ProfileState.Loading) {
                                        openDialog.value = true
                                    }
                                }
                        )

                        if (openDialog.value) {
                            DatePickerDialog(
                                onDismissRequest = { openDialog.value = false },
                                confirmButton = {
                                    TextButton(onClick = {
                                        datePickerState.selectedDateMillis?.let {
                                            birthdate = it
                                            dateOfBirth = dateFormatter.format(Date(it))
                                        }
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
                    Spacer(modifier = Modifier.height(12.dp))

                    // School - EDITABLE
                    OutlinedTextField(
                        value = school,
                        onValueChange = { school = it },
                        label = { Text("School") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = profileState !is ProfileState.Loading,
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Email - EDITABLE
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = profileState !is ProfileState.Loading,
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Student ID - EDITABLE
                    OutlinedTextField(
                        value = studentId,
                        onValueChange = { studentId = it },
                        label = { Text("Student ID") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = profileState !is ProfileState.Loading,
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // UPDATE NOW BUTTON
                Button(
                    onClick = {
                        viewModel.updateProfile(
                            newFullName = fullName,
                            newGender = gender,
                            newDateOfBirth = dateOfBirth,
                            newSchool = school,
                            newEmail = email,
                            newStudentId = studentId
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = darkBlue),
                    enabled = profileState !is ProfileState.Loading
                ) {
                    if (profileState is ProfileState.Loading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text("UPDATE NOW", fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Show loading overlay when loading
            if (profileState is ProfileState.Loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = darkBlue)
                }
            }
        }
    }
}