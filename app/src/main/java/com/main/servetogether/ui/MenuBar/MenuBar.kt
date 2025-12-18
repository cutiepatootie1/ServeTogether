package com.main.servetogether.ui.MenuBar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Support
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.main.servetogether.R
import com.main.servetogether.shared.UserViewModel

@Composable
fun MenuBar(
    role: String,
    onItemClick: (String) -> Unit,
    viewModel: UserViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        // Force load user data when MenuBar is shown
        viewModel.loadUserData()
    }

    val darkBlue = Color(0xFF0D47A1)
    val lightBlue = Color(0xFF1976D2)
    val currentUser by viewModel.userName.collectAsState()
    val userSchool by viewModel.userSchool.collectAsState()

    var activitiesExpanded by remember { mutableStateOf(false) }
    var organizationExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(300.dp)
            .background(Color.White)
    ) {
        // 🎨 Gradient Header with Logo and Profile
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(darkBlue, lightBlue)
                    )
                )
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 📷 Logo (Circular)
            Image(
                painter = painterResource(id = R.drawable.servetogether_logo),
                contentDescription = "ServeTogether Logo",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 👤 Profile Picture (Placeholder)
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f))
                    .border(2.dp, Color.White, CircleShape),
                tint = Color.White
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 📝 User Name
            Text(
                text = currentUser.ifEmpty { "User" },
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // 🏫 School/Role
            Text(
                text = userSchool.ifEmpty { role.replaceFirstChar { it.uppercase() } },
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.9f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Menu Items Section
        Column(modifier = Modifier.weight(1f)) {

            // 👤 View Profile
            MenuItem(
                icon = Icons.Filled.AccountCircle,
                text = "View Profile",
                darkBlue = darkBlue
            ) {
                onItemClick("profile")
            }

            // VOLUNTEER MENU
            if (role.equals("volunteer", ignoreCase = true)) {
                // 📋 See Activities ▼
                MenuItem(
                    icon = Icons.Filled.VolunteerActivism,
                    text = "See Activities",
                    darkBlue = darkBlue,
                    trailingIcon = if (activitiesExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore
                ) {
                    activitiesExpanded = !activitiesExpanded
                }

                // Dropdown: Volunteer Activities & Donations
                AnimatedVisibility(
                    visible = activitiesExpanded,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF5F5F5))
                            .padding(vertical = 4.dp)
                    ) {
                        DropdownItem(text = "Volunteer Activities") {
                            // Navigate to home screen which shows activities
                            onItemClick("home_screen/$role")
                        }
                        DropdownItem(text = "Volunteer Donations") {
                            onItemClick("donation_screen/$role")
                        }
                    }
                }
            }

            // ORGANIZATION MENU
            if (role.equals("organization", ignoreCase = true)) {
                // 📋 See Activities ▼
                MenuItem(
                    icon = Icons.Filled.Assignment,
                    text = "See Activities",
                    darkBlue = darkBlue,
                    trailingIcon = if (activitiesExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore
                ) {
                    activitiesExpanded = !activitiesExpanded
                }

                // Dropdown: Organized Activities & Donations
                AnimatedVisibility(
                    visible = activitiesExpanded,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF5F5F5))
                            .padding(vertical = 4.dp)
                    ) {
                        DropdownItem(text = "Manage Activities") {
                            onItemClick("organized_activity")
                        }
                        DropdownItem(text = "Volunteer Donations") {
                            onItemClick("donation_screen/$role")
                        }
                    }
                }

                // Divider
                Divider(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = Color.LightGray.copy(alpha = 0.5f)
                )

                // ➕ Organization ▼
                MenuItem(
                    icon = Icons.Filled.BusinessCenter,
                    text = "Organization",
                    darkBlue = darkBlue,
                    trailingIcon = if (organizationExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore
                ) {
                    organizationExpanded = !organizationExpanded
                }

                // Dropdown: Start & Manage Activities
                AnimatedVisibility(
                    visible = organizationExpanded,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF5F5F5))
                            .padding(vertical = 4.dp)
                    ) {
                        DropdownItem(text = "Start an Activity") {
                            onItemClick("start_new_act")
                        }
                        DropdownItem(text = "Manage Activities") {
                            onItemClick("organized_activity")
                        }
                    }
                }
            }

            // Divider before Support
            Divider(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                color = Color.LightGray.copy(alpha = 0.5f)
            )

            // ❓ Support
            MenuItem(
                icon = Icons.Filled.Support,
                text = "Support",
                darkBlue = darkBlue
            ) {
                onItemClick("support")
            }
        }

        // 🚪 Log Out (Red) - At Bottom
        Column {
            Divider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = Color.LightGray.copy(alpha = 0.5f)
            )
            MenuItem(
                icon = Icons.Filled.Logout,
                text = "Log Out",
                darkBlue = Color(0xFFD32F2F) // Red color for logout
            ) {
                onItemClick("logout")
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun MenuItem(
    icon: ImageVector,
    text: String,
    darkBlue: Color,
    trailingIcon: ImageVector? = null,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = darkBlue,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            color = darkBlue,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        if (trailingIcon != null) {
            Icon(
                imageVector = trailingIcon,
                contentDescription = null,
                tint = darkBlue,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun DropdownItem(text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(start = 54.dp, end = 16.dp, top = 10.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Bullet point indicator
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(6.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            color = Color.DarkGray,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MenuBarVolunteerPreview() {
    MenuBar(role = "volunteer", onItemClick = {})
}

@Preview(showBackground = true)
@Composable
fun MenuBarOrganizationPreview() {
    MenuBar(role = "organization", onItemClick = {})
}