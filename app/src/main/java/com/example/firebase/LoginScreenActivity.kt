package com.example.firebase

import android.content.Context
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.material3.Button
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

@Composable
fun LoginScreen(navController: NavHostController) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val googleSignInClient = createGoogleSignInClient(context)
    if (currentUser != null) {
        LoggedInScreen(currentUser.displayName ?: "User",
            navController, auth, googleSignInClient)
    } else {
        RegularLoginScreen(navController, context, auth)
    }
}

@Composable
fun LoggedInScreen(userName: String, navController: NavHostController,
                   auth: FirebaseAuth, googleSignInClient: GoogleSignInClient) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Logged in as $userName",
            style = TextStyle(
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { navController.navigate("firebase/$userName") },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Continue")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                auth.signOut()
                googleSignInClient.signOut().addOnCompleteListener {
                    Log.d("LoginScreen", "User logged out from Google")
                }
                navController.navigate("login")
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Log out")
        }
    }
}

@Composable
fun RegularLoginScreen(navController: NavHostController, context: Context, auth: FirebaseAuth) {
    val googleSignInClient = createGoogleSignInClient(context)

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        handleSignInResult(task, auth, navController)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Login to Continue",
            style = TextStyle(
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { launcher.launch(googleSignInClient.signInIntent) },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Sign in with Google")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { navController.navigate("firebase/Guest") },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Continue without Sign-In")
        }
    }
}

fun handleSignInResult(
    task: Task<GoogleSignInAccount>,
    auth: FirebaseAuth,
    navController: NavHostController
) {
    try {
        val account = task.getResult(ApiException::class.java)
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)

        auth.signInWithCredential(credential)
            .addOnCompleteListener { authTask ->
                if (authTask.isSuccessful) {
                    val user = auth.currentUser
                    val userName = user?.displayName ?: "User"
                    navController.navigate("firebase/$userName")
                } else {
                    Log.e("LoginScreen", "Firebase Authentication failed", authTask.exception)
                }
            }
    } catch (e: ApiException) {
        Log.e("LoginScreen", "Google Sign-In failed", e)
    }
}


fun createGoogleSignInClient(context: Context): GoogleSignInClient {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken("your-token-id.apps.googleusercontent.com")
        .requestEmail()
        .build()
    return GoogleSignIn.getClient(context, gso)
}
