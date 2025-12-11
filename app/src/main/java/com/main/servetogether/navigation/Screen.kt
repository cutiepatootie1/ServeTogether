package com.main.servetogether.navigation

sealed class Screen(val route: String){
    object Login : Screen("login")
    object CreateAcc : Screen("create_account")
    object ForgotPass : Screen("forgot_password")
    object Home : Screen("home_screen")
}