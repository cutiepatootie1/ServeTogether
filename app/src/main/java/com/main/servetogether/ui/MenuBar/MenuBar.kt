package com.main.servetogether.ui.MenuBar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.main.servetogether.shared.UserViewModel


@Composable
fun MenuBar(
    role: String,
    onItemClick: (String) -> Unit,
    viewModel: UserViewModel = viewModel()
) {
    val darkBlue = Color(0xFF0D47A1)
    val currentUser by viewModel.userName.collectAsState()
    val userSchool by viewModel.userSchool.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(300.dp) // Typical width for a navigation drawer
            .background(Color.White)
            .padding(vertical = 16.dp)
    ) {
        // Header
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp)) {
            Text(text = "$currentUser", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text(text = "$userSchool", fontSize = 14.sp, color = Color.Gray)
        }

        Divider(modifier = Modifier.padding(horizontal = 16.dp))

        // Menu Items
        MenuItem(icon = Icons.Filled.AccountCircle, text = "View Profile", darkBlue = darkBlue) { onItemClick("profile") }
        MenuItem(icon = Icons.Filled.VolunteerActivism, text = "See activities", darkBlue = darkBlue) { onItemClick("activities") }

        // Highlighted Section
        Column(
            modifier = Modifier
                .background(darkBlue)
                .padding(vertical = 8.dp)
        ) {
            if(role == "organization"){
                SubMenuItem(text = "Start a Collective") { onItemClick("start_new_act")}
            }
            SubMenuItem(text = "Volunteer Activities") { onItemClick("volunteer_activities") }
            SubMenuItem(text = "Volunteer Donations") { onItemClick("volunteer_donations") }
            SubMenuItem(text = "Support") { onItemClick("support") }
        }

        MenuItem(icon = Icons.Filled.Assignment, text = "Tasks", darkBlue = darkBlue) { onItemClick("tasks") }
        MenuItem(icon = Icons.Filled.MilitaryTech, text = "Honor", darkBlue = darkBlue) { onItemClick("honor") }


        Spacer(modifier = Modifier.weight(1f))

        // Log Out
        Divider(modifier = Modifier.padding(horizontal = 16.dp))
        MenuItem(icon = Icons.Filled.Logout, text = "Log Out", darkBlue = darkBlue) { onItemClick("logout") }
    }
}

@Composable
fun MenuItem(icon: ImageVector, text: String, darkBlue: Color, onClick: () -> Unit = {
}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = text, tint = darkBlue)
        Spacer(modifier = Modifier.width(24.dp))
        Text(text = text, color = darkBlue, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun SubMenuItem(text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "-", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.width(24.dp))
        Text(text = text, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
    }
}


@Preview(showBackground = true)
@Composable
fun MenuBarPreview() {
    MenuBar(role = "organization", onItemClick = {})
}
