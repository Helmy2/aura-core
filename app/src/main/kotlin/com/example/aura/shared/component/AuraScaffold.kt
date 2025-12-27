package com.example.aura.shared.component

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat

@Composable
fun AuraScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable (PaddingValues) -> Unit = {},
    bottomBar: @Composable BoxScope.(PaddingValues) -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    isStatusBarOnDark: Boolean = false,
    windowInsets: WindowInsets = WindowInsets.safeDrawing,
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    val activity = LocalActivity.current

    LaunchedEffect(isStatusBarOnDark) {
        activity?.let {
            WindowCompat.getInsetsController(activity.window, activity.window.decorView).apply {
                isAppearanceLightStatusBars = !isStatusBarOnDark
            }
        }
    }

    Scaffold(
        topBar = { topBar(WindowInsets.statusBars.asPaddingValues()) },
        bottomBar = {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth()
            ) { bottomBar(WindowInsets.navigationBars.asPaddingValues()) }
        },
        snackbarHost = snackbarHost,
        modifier = modifier,
        content = content,
        contentWindowInsets = windowInsets,
        floatingActionButton = floatingActionButton
    )
}
