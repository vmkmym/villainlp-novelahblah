package com.example.villainlp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.villainlp.shared.Screen
import com.example.villainlp.ui.theme.VillainlpTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : ComponentActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        setupContent()
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
//            reload()
        }
    }

    private fun setupContent() {
        setContent {
            VillainlpTheme {
                val navController = rememberNavController()
                val launcher =
                    rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.StartActivityForResult()
                    ) { result ->
                        handleSignInResult(result.data, navController)
                    }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    VillainNavigation(
                        signInClicked = { signInWithGoogle(launcher) },
                        signOutClicked = { signOut(navController) },
                        signUpClicked = { email, password -> createAccount(email, password, navController) },
                        signIn = { email, password -> signIn(email, password, navController) },
                        navController, mAuth
                    )
                }
            }
        }
    }

    private fun signInWithGoogle(launcher: ActivityResultLauncher<Intent>) {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private fun handleSignInResult(data: Intent?, navController: NavController) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            firebaseAuthWithGoogle(account.idToken!!)
            navController.popBackStack()
            navController.navigate(Screen.CreativeYard.route)
        } catch (e: ApiException) {
            Log.w("SignIn", "Google sign in failed", e)
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
                    // 로그인 실패 시 사용자에게 알림
                    if (!task.isSuccessful) {
                        val message = task.exception?.message
                        Toast.makeText(this, "로그인 실패: $message", Toast.LENGTH_LONG).show()
                    }
                }
            }
    }

    private fun signOut(navController: NavController) {
        mAuth.signOut()
        googleSignInClient.signOut().addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(this, "로그아웃 성공", Toast.LENGTH_SHORT).show()
                navController.navigate(Screen.Login.route)
            } else {
                Toast.makeText(this, "로그아웃 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createAccount(
        email: String,
        password: String,
        navController: NavHostController,
    ) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = mAuth.currentUser
                    if (user != null) {
                        navController.navigate(Screen.Login.route)
                    }
                } else {
                    Toast.makeText(this, "Please sign up to continue.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // 이메일, 비번 로그인
    private fun signIn(
        email: String,
        password: String,
        navController: NavHostController,
    ) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = mAuth.currentUser
                    if (user != null) {
                        navController.navigate(Screen.CreativeYard.route)
                    }
                } else {
                    Toast.makeText(baseContext, "가입된 계정이 아닙니다", Toast.LENGTH_SHORT,).show()
                }
            }
    }

    // 이메일 인증
    private fun sendEmailVerification() {
        val user = mAuth.currentUser!!
        user.sendEmailVerification()
            .addOnCompleteListener(this) { task ->
                // Email Verification sent
            }
    }
}