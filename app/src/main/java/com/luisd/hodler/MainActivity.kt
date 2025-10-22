package com.luisd.hodler

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.luisd.hodler.presentation.navigation.HodlerNavGraph
import com.luisd.hodler.presentation.theme.HodlerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HodlerTheme {
                val navController = rememberNavController()
                HodlerNavGraph(navController = navController)
            }
        }
    }
}
