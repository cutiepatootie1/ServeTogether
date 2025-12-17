package com.main.servetogether.ui.donationscreen


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.main.servetogether.ui.MenuBar.MenuBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonationScreen(
    navController: NavController,
    role: String
) {

    val darkBlue = Color(0xFF0D47A1)
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var donorName by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    var selectedPayment by remember { mutableStateOf("") }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                MenuBar(
                    role = role,
                    onItemClick = { route ->
                        scope.launch { drawerState.close() }
                        navController.navigate(route)
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
                            text = "Donate",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                        }
                    },
                    actions = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
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
                    .verticalScroll(scrollState)
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Icon(
                    imageVector = Icons.Default.VolunteerActivism,
                    contentDescription = "Donate",
                    tint = darkBlue,
                    modifier = Modifier.size(80.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Support the Cause",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Your donation helps us serve more people.",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(24.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {

                    OutlinedTextField(
                        value = donorName,
                        onValueChange = { donorName = it },
                        label = { Text("Donor Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text("Donation Amount") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        label = { Text("Message (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Mode of Payment",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    PaymentOption(
                        name = "GCash",
                        selected = selectedPayment == "GCash",
                        onClick = { selectedPayment = "GCash" },
                        darkBlue = darkBlue
                    )

                    PaymentOption(
                        name = "PayMaya",
                        selected = selectedPayment == "PayMaya",
                        onClick = { selectedPayment = "PayMaya" },
                        darkBlue = darkBlue
                    )

                    PaymentOption(
                        name = "Bank",
                        selected = selectedPayment == "Bank",
                        onClick = { selectedPayment = "Bank" },
                        darkBlue = darkBlue
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { /* Handle donation */ },
                    enabled = selectedPayment.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = darkBlue)
                ) {
                    Text("DONATE NOW", fontSize = 16.sp, color = Color.White)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun DonationScreenPreviewContent() {
    DonationScreen(
        navController = rememberNavController(),
        role = "volunteer"
    )
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    device = "id:pixel_7"
)
@Composable
fun DonationScreenPreview() {
    DonationScreenPreviewContent()
}

@Composable
fun PaymentOption(
    name: String,
    selected: Boolean,
    onClick: () -> Unit,
    darkBlue: Color
) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .height(80.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (selected) darkBlue else Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = name,
                color = if (selected) Color.White else darkBlue,
                fontWeight = FontWeight.Bold
            )
        }
    }
}