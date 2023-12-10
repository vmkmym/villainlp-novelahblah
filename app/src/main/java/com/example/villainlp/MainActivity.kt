package com.example.villainlp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.villainlp.model.Screen
import com.example.villainlp.ui.theme.VillainlpTheme
import com.example.villainlp.view.ChatScreen
import com.example.villainlp.view.LoginScreen
import com.example.villainlp.view.HomeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VillainlpTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    VillainNavigation()
//                    Text(text ="Hello World!")
                }
            }
        }
    }
}

@Composable
fun VillainNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination =
    Screen.ChatbotScreen.route
    ) {
        composable(Screen.Screen1.route) {
            LoginScreen(navController)
        }
        composable(Screen.Screen2.route) {
            HomeScreen(navController)
        }
        composable(Screen.ChatbotScreen.route) {
            ChatScreen()
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    VillainlpTheme {
        VillainNavigation()
    }
}