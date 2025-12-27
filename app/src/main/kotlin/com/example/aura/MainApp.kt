package com.example.aura

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.aura.navigation.bottomNavItems
import com.example.aura.shared.navigation.AppNavigator
import com.example.aura.shared.navigation.MainNavHost
import com.example.aura.shared.theme.AuraTheme
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun MainApp(modifier: Modifier = Modifier) {
    val navController: AppNavigator = koinInject()

    val windowAdaptiveInfo = currentWindowAdaptiveInfo()

    val currentDestination by remember {
        derivedStateOf { navController.backStack.lastOrNull() }
    }
    val baseNavigationSuiteType = remember(windowAdaptiveInfo) {
        NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(windowAdaptiveInfo)
    }

    val shouldShowNavigationBar = remember(baseNavigationSuiteType, currentDestination) {
        if (baseNavigationSuiteType == NavigationSuiteType.NavigationBar) {
            bottomNavItems.any { it.destination == currentDestination }
        } else {
            true
        }
    }

    AuraTheme {
        MainNavHost(shouldShowNavigationBar, navController, modifier)
    }
}

