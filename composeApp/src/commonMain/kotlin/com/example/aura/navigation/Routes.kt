package com.example.aura.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable


@Serializable
object HomeRoute : NavKey

@Serializable
data class DetailRoute(val id: Long) : NavKey