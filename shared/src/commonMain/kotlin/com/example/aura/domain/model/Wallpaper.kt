package com.example.aura.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Wallpaper(
    val id: Long,
    val imageUrl: String,
    val smallImageUrl: String,
    val photographer: String,
    val photographerUrl: String,
    val averageColor: String,
    val height: Int,
    val width: Int,
    val isFavorite: Boolean,
)
