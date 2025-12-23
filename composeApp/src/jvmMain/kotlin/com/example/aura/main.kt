package com.example.aura

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.example.aura.di.appModule
import com.example.aura.di.initKoin

fun main() = application {
    initKoin {
        modules(appModule)
    }
    Window(
        onCloseRequest = ::exitApplication,
        title = "Aura",
    ) {
        App()
    }
}