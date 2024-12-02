package com.example.firebase

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.firebase.ui.theme.FirebaseTheme
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val firebaseDatabase = FirebaseDatabase
            .getInstance("your-firebase-api")
        val databaseReference = firebaseDatabase.getReference("StudentInfo")

        setContent {
            FirebaseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    AppNavigation(navController, databaseReference)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(navController: NavHostController, databaseReference: DatabaseReference) {
    NavHost(navController = navController, startDestination = "starter") {
        composable("starter") { StarterScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("firebase/{userName}") { backStackEntry ->
            val userName = backStackEntry.arguments?.getString("userName") ?: "Guest"
            FirebaseUI(databaseReference = databaseReference, userName = userName)
        }
    }
}