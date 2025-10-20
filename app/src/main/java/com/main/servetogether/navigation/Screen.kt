package com.main.servetogether.navigation

sealed class Screen(val route: String){
    object Login : Screen("login")

}