package com.main.servetogether.ui.createaccount

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.main.servetogether.ui.login.RoundedCheckbox
import com.main.servetogether.ui.theme.White
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import com.google.android.material.progressindicator.CircularProgressIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAccScreen(navController: NavController,
                    viewModel: SignUpViewModel = viewModel()
) {
    // Context for Toasts
    val context = LocalContext.current
    // Watch the ViewModel state
    val uiState by viewModel.signUpState.collectAsState()

    LaunchedEffect(uiState) {
        when (uiState) {
            is SignUpState.Success -> {
                Toast.makeText(context, "Account Created!", Toast.LENGTH_SHORT).show()
                navController.navigate("home_screen") {
                    popUpTo(0) {
                        inclusive = true
                    }
                }
                viewModel.resetState()
            }
            is SignUpState.Error -> {
                val errorMsg = (uiState as SignUpState.Error).message
                Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
            else -> {} // Do nothing for Idle/Loading inside this listener
        }
    }
    // Form Variables
    var email by remember { mutableStateOf(("")) }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var school by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var birthdate by remember { mutableStateOf<Long?>(null) }
    var studentid by remember { mutableStateOf("") }
    var agreeTerms by remember { mutableStateOf(false) }

    // UI States
    val showWarningPop = remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val openDialog = remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = birthdate
    )

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

            Text(
                text = "Create an Account",
                modifier = Modifier.padding(vertical = 16.dp),
                style = MaterialTheme.typography.titleLarge.copy(
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 24.sp
                )
            )

            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = "Email",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .background(color = MaterialTheme.colorScheme.surfaceContainer),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            /*
            * USERNAME COL
            *
            * */
            Column(
                modifier = Modifier.padding(8.dp)
            )
            {
                Text(
                    text = "Username",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium
                )

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .background(color = MaterialTheme.colorScheme.surfaceContainer),
                    shape = RoundedCornerShape(12.dp)
                )
            }
            /*
            * PASSWORD COL
            * */
            Column(
                modifier = Modifier.padding(8.dp)
            )
            {
                Text(
                    text = "Password",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
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
            }
            /*
            * CONFIRM PASSWORD COL
            * */
            Column(
                modifier = Modifier.padding(8.dp)
            )
            {
                Text(
                    text = "Confirm Password",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium
                )

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (confirmPasswordVisible)
                            Icons.Default.Visibility
                        else Icons.Default.VisibilityOff

                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                imageVector = image,
                                contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password"
                            )
                        }

                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .background(color = MaterialTheme.colorScheme.surfaceContainer),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            /*
              AREA FOR BIRTHDATE FIELD
            */

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

                /*
                * ENCOUNTERED AN ISSUE WHERE TOUCH AREA IS LIMITED. THIS IS TO FIX THAT.
                */
                Box(
                    modifier = Modifier
                        .matchParentSize() // Matches the size of the parent Box (which is the TextField)
                        .clickable { openDialog.value = true }
                )

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


            Column(
                modifier = Modifier.padding(8.dp)
            )
            {
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

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable {
                        agreeTerms = !agreeTerms
                    }
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
                    /*
                    * MAKE CLICKABLE AS A LINK TO TERMS AND CONDITIONS POPUP TODO
                    *
                    */
                    fontSize = 14.sp
                )
            }

            Button(
                onClick = {
                    if (!agreeTerms) {
                        showWarningPop.value = true
                        return@Button
                    }
                    // Firebase signup
                    viewModel.signUp(email, password, username, school, birthdate)
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
                    Text("Create Account")
                }
            }

            if (showWarningPop.value) {
                AlertDialog(
                    onDismissRequest = {
                        // Dismiss the dialog when the user taps outside or presses back
                        showWarningPop.value = false
                    },
                    title = {
                        Text("Terms and Conditions")
                    },
                    text = {
                        Text("Please read the terms and conditions before proceeding")
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                // Dismiss the pop-up and proceed with registration
                                showWarningPop.value = false

                            }
                        ) {
                            Text("Dismiss")
                        }
                    },
                )
            }


        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateAccScreenPreview() {
    CreateAccScreen(navController = rememberNavController())
}
