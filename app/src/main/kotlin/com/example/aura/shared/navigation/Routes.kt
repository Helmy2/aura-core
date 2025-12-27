package com.example.aura.shared.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route : NavKey {
    @Serializable
    object Home : Route

    @Serializable
    data class Detail(val id: Long) : Route
}