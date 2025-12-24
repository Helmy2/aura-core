package com.example.aura

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.example.aura.navigation.MainNavHost

@Composable
fun App() {
    MaterialTheme {
        MainNavHost()
    }
}