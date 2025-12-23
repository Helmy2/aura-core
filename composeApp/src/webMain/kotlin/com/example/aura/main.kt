package com.example.aura

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.example.aura.di.appModule
import com.example.aura.di.initKoin

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    initKoin {
        modules(appModule)
    }
    ComposeViewport {
        App()
    }
}