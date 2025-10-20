package com.main.servetogether.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.main.servetogether.ui.login.LoginScreen

@Composable
fun AppNavGraph(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) { LoginScreen(navController) }
    }
}
