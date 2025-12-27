package com.example.aura

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.aura.shared.designsystem.theme.AuraTheme
import com.example.aura.shared.navigation.AppNavigator
import com.example.aura.shared.navigation.MainNavHost
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    val navigator: AppNavigator by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                Color.Transparent.toArgb(),
            ),
            navigationBarStyle = SystemBarStyle.dark(
                Color.Transparent.toArgb(),
            )
        )
        super.onCreate(savedInstanceState)

        navigator.attachToRegistry(this)

        setContent {
            AuraTheme {
                MainNavHost()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        navigator.detachFromRegistry(this)
    }
}