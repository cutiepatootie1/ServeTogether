package com.main.servetogether.ui.ProfileScreen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.runtime.rememberCoroutineScope
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
import com.google.firebase.auth.FirebaseAuth
import com.main.servetogether.navigation.Screen
import com.main.servetogether.shared.ProfileState
import com.main.servetogether.shared.UserViewModel
import com.main.servetogether.ui.MenuBar.MenuBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: UserViewModel = viewModel()
) {
    val context = LocalContext.current
    val darkBlue = Color(0xFF0D47A1)

    // Collect states from ViewModel
    val profileState by viewModel.profileState.collectAsState()
    val currentRole by viewModel.userRole.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // Load profile data when screen opens
    LaunchedEffect(Unit) {
        viewModel.loadProfileData()
    }

    // Show error messages
    LaunchedEffect(profileState) {
        when (profileState) {
            is ProfileState.Error -> {
                val error = (profileState as ProfileState.Error).message
                Toast.makeText(context, "Error: $error", Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                MenuBar(
                    role = currentRole ?: "volunteer",
                    onItemClick = { route ->
                        scope.launch { drawerState.close() }

                        when (route) {
                            "profile" -> {
                                // Already on profile
                            }
                            "activities" -> {
                                navController.navigate("activities_screen")
                            }
                            "start_new_act" -> {
                                navController.navigate("create_activity")
                            }
                            "organized_activities" -> {
                                navController.navigate("organized_activities")
                            }
                            "donation_screen/\$role\"" -> {
                                navController.navigate("donation_screen/${currentRole ?: "volunteer"}")
                            }
                            "support" -> {
                                // Navigate to support screen
                            }
                            "logout" -> {
                                FirebaseAuth.getInstance().signOut()
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
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
                            text = "My Profile",
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
                        text = "Personal Information",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    // Profile Picture
                    Box(
                        modifier = Modifier.padding(bottom = 32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(140.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray),
                            tint = Color.White
                        )
                    }

                    // Profile Information Card
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .padding(16.dp)
                    ) {
                        ProfileInfoRow("Full Name", viewModel.fullName)
                        ProfileInfoRow("Gender", viewModel.gender)
                        ProfileInfoRow("Date of Birth", viewModel.dateOfBirth)
                        ProfileInfoRow("School", viewModel.school)
                        ProfileInfoRow("Email", viewModel.email)
                        ProfileInfoRow("Student ID", viewModel.studentId)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Edit Profile Button
                    Button(
                        onClick = {
                            navController.navigate("update_profile")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = darkBlue),
                        enabled = profileState !is ProfileState.Loading
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text("EDIT PROFILE", fontSize = 16.sp, color = Color.White)
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
}

@Composable
fun ProfileInfoRow(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value.ifEmpty { "Not provided" },
            fontSize = 16.sp,
            color = Color.Black,
            fontWeight = FontWeight.Normal
        )
        if (label != "Student ID") {
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.LightGray.copy(alpha = 0.5f))
            )
        }
    }
}