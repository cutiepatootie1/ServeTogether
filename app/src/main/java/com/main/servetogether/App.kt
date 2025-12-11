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
        // Inside MainActivity.kt > onCreate
        try {
            val info = packageManager.getPackageInfo(packageName, android.content.pm.PackageManager.GET_SIGNATURES)
            for (signature in info.signatures!!) {
                val md = java.security.MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val digest = md.digest()
                val hexString = StringBuilder()
                for (b in digest) {
                    hexString.append(String.format("%02X:", b))
                }
                // LOOK AT THIS LOG IN LOGCAT!
                android.util.Log.e("MY_KEY_HASH", "Current SHA-1: ${hexString.toString()}")
            }
        } catch (e: Exception) {
            android.util.Log.e("MY_KEY_HASH", "Error getting hash", e)
        }
    }
}