package com.main.servetogether

import com.google.firebase.FirebaseApp
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.main.servetogether.navigation.AppNavGraph
import com.main.servetogether.ui.theme.ServeTogetherTheme

class App : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContent{
            ServeTogetherTheme {
                val  navController = rememberNavController()
                AppNavGraph(navController)
            }
        }
    }
}