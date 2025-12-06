package com.main.servetogether.ui.login

import androidx.compose.runtime.Composable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.main.servetogether.R
import com.main.servetogether.navigation.Screen
import com.main.servetogether.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
// UI State
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var keepSignedIn by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(White),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.servetogether_logo), // Replace with your logo resource
                contentDescription = "ServeTogether Logo",
                modifier = Modifier
                    .size(300.dp)
                    .padding(bottom = 16.dp)
            )


            // Username field
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                placeholder = { Text("Username", color = MaterialTheme.colorScheme.secondary) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .background(color = MaterialTheme.colorScheme.surfaceContainer),
                shape = RoundedCornerShape(12.dp)
            )

            // Password field
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Password", color = MaterialTheme.colorScheme.secondary) },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                trailingIcon = {
                    val image = if (passwordVisible)
                        Icons.Default.Visibility
                    else Icons.Default.VisibilityOff

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = image,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password"
                        )
                    }

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .background(color = MaterialTheme.colorScheme.surfaceContainer),
                shape = RoundedCornerShape(12.dp)
            )

            // Keep me signed in
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                RoundedCheckbox(
                    checked = keepSignedIn,
                    onCheckedChange = { keepSignedIn = it },
                    cornerRadius = 5.dp,
                    modifier = Modifier.padding(horizontal = 10.dp)
                )
                Text(
                    "Keep me Signed In",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 14.sp
                )
            }

            // Log in button
            Button(
                onClick = { /* TODO: Handle login */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Log in",fontSize = 16.sp)
            }

            // Forgot password / Create account
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Forgot Password?",
                    modifier = Modifier.clickable {
                        navController.navigate(Screen.ForgotPass.route)
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "  |  ",
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Create an Account",
                    modifier = Modifier.clickable {
                        navController.navigate(Screen.CreateAcc.route)
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )

            }
        }
    }
}

@Composable
fun RoundedCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    borderColor: Color = Color.Gray,
    checkedColor: Color = Color(0xFF6200EE),
    checkmarkColor: Color = Color.White,
    cornerRadius: Dp = 5.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(cornerRadius))
            .background(if (checked) checkedColor else Color.Transparent)
            .border(2.dp, borderColor, RoundedCornerShape(cornerRadius))
            .clickable { onCheckedChange(!checked) },
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Checked",
                tint = checkmarkColor,
                modifier = Modifier.size(size * 0.6f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(navController = rememberNavController())
}
