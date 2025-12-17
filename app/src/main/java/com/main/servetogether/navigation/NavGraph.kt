package com.main.servetogether.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import com.main.servetogether.data.model.VolunteeringActivity
import com.main.servetogether.shared.AuthState
import com.main.servetogether.shared.UserViewModel
import com.main.servetogether.ui.createaccount.CreateAccScreen
import com.main.servetogether.ui.forgotpass.ForgotPass
import com.main.servetogether.ui.homescreen.HomeScreen
import com.main.servetogether.ui.login.LoginScreen
import com.main.servetogether.ui.roleselect.RoleSelectionScreen
import com.main.servetogether.ui.volunteerActivities.CreateActivityScreen
import com.main.servetogether.ui.volunteerActivities.TaskDetails
import com.main.servetogether.ui.volunteerActivities.VolunteerActivity
import com.main.servetogether.ui.ProfileScreen.ProfileScreen
import com.main.servetogether.ui.volunteerActivities.VolunteerActivity
import com.main.servetogether.ui.volunteerActivities.OrganizedActivity

import com.main.servetogether.ui.donationscreen.DonationScreen


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavGraph(navController: NavHostController,
                userViewModel: UserViewModel = viewModel()
) {
    val auth = FirebaseAuth.getInstance()
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        // --- 1. NEW: SPLASH / LOGIC CHECK ROUTE ---
//        composable("splash_screen") {
//            val authState by userViewModel.authState.collectAsState()
//            val currentRole by userViewModel.userRole.collectAsState()
//
//            // Logic runs while user admires your logo
//            LaunchedEffect(Unit) {
//                when (authState) {
//                    is AuthState.Authenticated -> {
//                        val user = auth.currentUser
//                        if (user != null) {
//                            // Fetch role...
//                            navController.navigate("home_screen/$currentRole") {
//                                popUpTo(0) { inclusive = true }
//                            }
//                        } else {
//                            navController.navigate(Screen.Login.route)
//                        }
//                    }
//
//                    else -> {}
//                }
//            }
//        }

                //login screen route
                composable(Screen.Login.route, exitTransition = {
                    when (targetState.destination.route) {
                        Screen.CreateAcc.route -> slideOutHorizontally(
                            targetOffsetX = { -1000 },
                            animationSpec = tween(300)
                        )

                        else -> null
                    }
                }) { LoginScreen(navController) }

                composable(Screen.ForgotPass.route) { ForgotPass(navController) }

                composable(Screen.RoleSelect.route) {
                    RoleSelectionScreen(
                        onRoleSelected = { role ->
                            // Navigate to sign up, passing the role as a path parameter
                            navController.navigate("create_account/$role")
                        }
                    )
                }

                // SCREEN 2: Create Account (Receives the variable)
                composable(
                    route = "create_account/{role}", // {role} is a placeholder for the data
                    arguments = listOf(navArgument("role") { type = NavType.StringType }),
                    enterTransition = {
                        slideInHorizontally(
                            initialOffsetX = { 1000 }, // slide from right
                            animationSpec = tween(300)
                        )
                    },
                    exitTransition = {
                        slideOutHorizontally(
                            targetOffsetX = { 1000 },
                            animationSpec = tween(300)
                        )
                    },
                    popExitTransition = {
                        slideOutHorizontally(
                            targetOffsetX = { 1000 },
                            animationSpec = tween(300)
                        )
                    }
                ) { backStackEntry ->
                    // Extract the role from the navigation arguments
                    val role = backStackEntry.arguments?.getString("role") ?: "user"
                    CreateAccScreen(navController, role = role)
                }

                composable(
                    route = "home_screen/{role}",
                    arguments = listOf(navArgument("role") { type = NavType.StringType }),
                    enterTransition = {
                        fadeIn(animationSpec = tween(500))
                    }) { backStackEntry ->
                    val role = backStackEntry.arguments?.getString("role") ?: "user"
                    HomeScreen(role = role, navController)
                }

                //For the Donation
        composable(
            route = Screen.Donation.route,
            arguments = listOf(navArgument("role") { type = NavType.StringType }),
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { 1000 },
                    animationSpec = tween(300)
                )
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { 1000 },
                    animationSpec = tween(300)
                )
            }
        ) { backStackEntry ->

            val role = backStackEntry.arguments?.getString("role") ?: "volunteer"

            DonationScreen(
                navController = navController,
                role = role
            )
        }


                composable(
                    route = "create_activity",
                    enterTransition = {
                        slideInHorizontally(
                            initialOffsetX = { 1000 }, // Slide in from right
                            animationSpec = tween(300)
                        )
                    },
                    popExitTransition = {
                        slideOutHorizontally(
                            targetOffsetX = { 1000 }, // Slide out to right
                            animationSpec = tween(300)
                        )
                    }
                ) { backStackEntry ->
                    val role = backStackEntry.arguments?.getString("role") ?: "user"
                    CreateActivityScreen(navController, role = role)
                }
            composable(
                route = "activity_detail/{activityId}",
                arguments = listOf(navArgument("activityId") {type = NavType.StringType })
            ){ backStackEntry ->
                val id = backStackEntry.arguments?.getString("activityId") ?: ""

                VolunteerActivity(
                    navController = navController,
                    activityId = id
                )
            }
        composable(Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }
        //Volunteer Activity
        composable(
            route = Screen.VolunteerActivity.route,
            arguments = listOf(navArgument("activityId") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val activityId = backStackEntry.arguments?.getString("activityId")
            VolunteerActivity(
                navController = navController,
                activityId = activityId!!
            )
        }


        composable(Screen.OrganizedActivity.route) {
            OrganizedActivity(navController = navController)
        }

        composable(
            route = "task_detail/{taskId}",
            arguments = listOf(navArgument("taskId") { type = NavType.StringType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
            TaskDetails(navController = navController, taskId = taskId)
        }
    }
}


