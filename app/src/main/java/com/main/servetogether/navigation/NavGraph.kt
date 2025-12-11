package com.main.servetogether.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.main.servetogether.ui.createaccount.CreateAccScreen
import com.main.servetogether.ui.forgotpass.ForgotPass
import com.main.servetogether.ui.homescreen.HomeScreen
import com.main.servetogether.ui.login.LoginScreen
import com.main.servetogether.ui.roleselect.RoleSelectionScreen

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavGraph(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
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
        /*
        create account screen routes and animations
         */
//        composable(
//            Screen.CreateAcc.route,
//            enterTransition = {
//                slideInHorizontally(
//                    initialOffsetX = { 1000 }, // slide from right
//                    animationSpec = tween(300)
//                )
//            },
//            exitTransition = {
//                slideOutHorizontally(
//                    targetOffsetX = { 1000 },
//                    animationSpec = tween(300)
//                )
//            },
//                    popExitTransition = {
//                slideOutHorizontally(
//                    targetOffsetX = { 1000 },
//                    animationSpec = tween(300)
//                )
//            }
//        )
//
//        { CreateAccScreen(navController) }

        composable(Screen.ForgotPass.route) { ForgotPass(navController) }
        composable(Screen.Home.route,
            enterTransition = {
                fadeIn(animationSpec = tween(500))
            }){
            HomeScreen(navController)
        }

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
            CreateAccScreen(navController,role = role)
        }
    }
}
