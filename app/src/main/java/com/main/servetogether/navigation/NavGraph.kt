package com.main.servetogether.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.main.servetogether.ui.createaccount.CreateAccScreen
import com.main.servetogether.ui.forgotpass.ForgotPass
import com.main.servetogether.ui.login.LoginScreen

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
        composable(
            Screen.CreateAcc.route,
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
        )

        { CreateAccScreen(navController) }

        composable(Screen.ForgotPass.route) { ForgotPass(navController) }
    }
}
