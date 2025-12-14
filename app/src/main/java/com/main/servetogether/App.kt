package com.main.servetogether

import com.google.firebase.FirebaseApp
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestoreSettings
import com.main.servetogether.navigation.AppNavGraph
import com.main.servetogether.shared.UserViewModel
import com.main.servetogether.ui.theme.ServeTogetherTheme

class App : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        val userViewModel by viewModels<UserViewModel>()
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
//        val settings = firestoreSettings {
//            isPersistenceEnabled = true // CACHE DATA LOCALLY
//        }
//        val db = FirebaseFirestore.getInstance("servedb")
//        try {
//            db.firestoreSettings = settings
//        } catch (e: Exception) {
//            // This catches cases where settings were already applied
//            android.util.Log.e("Firestore", "Could not set settings", e)
//        }
        setContent{
            ServeTogetherTheme {
                val  navController = rememberNavController()
                AppNavGraph(navController)
            }

        }
        // sum debugging shit for firebase error
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