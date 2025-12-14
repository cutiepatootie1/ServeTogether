package com.main.servetogether.navigation

sealed class Screen(val route: String){
    object Login : Screen("login")
    object CreateAcc : Screen("create_account")
    object ForgotPass : Screen("forgot_password")
    object Home : Screen("home_screen")
    // TODO: ADD NEW SCREEN FOR ROLE SELECTION WHICH WILL GIVE USERS NECESSARY PERMISSIONS FOR LATER FEATURES
    object RoleSelect : Screen("role_selection")
    object CreateAct : Screen("create_activity")
}