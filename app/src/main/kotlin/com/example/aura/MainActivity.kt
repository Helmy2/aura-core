package com.example.aura

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.aura.shared.navigation.AppNavigator
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    val navigator: AppNavigator by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        navigator.attachToRegistry(this)

        setContent {
            MainApp()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        navigator.detachFromRegistry(this)
    }
}