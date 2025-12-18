package com.main.servetogether.navigation

sealed class Screen(val route: String){
    object Login : Screen("login")
    object CreateAcc : Screen("create_account")
    object ForgotPass : Screen("forgot_password")
    object Home : Screen("home_screen")
    object RoleSelect : Screen("role_selection")
    object CreateAct : Screen("create_activity")
    object Profile : Screen("profile")
    object OrganizedActivity : Screen("organized_activity")
    object VolunteerActivity : Screen("volunteer_activity/{activityId}") {
        fun passId(activityId: String) =
            "volunteer_activity/$activityId"
    }
    object Donation : Screen("donation_screen/{role}")
    object Support : Screen("support")
}