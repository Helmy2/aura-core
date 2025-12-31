package com.example.aura.shared.model

import androidx.compose.runtime.Immutable
import com.example.aura.domain.model.Wallpaper
import kotlinx.serialization.Serializable


@Immutable
@Serializable
data class WallpaperUi(
    val id: Long,
    val imageUrl: String,
    val smallImageUrl: String,
    val photographerName: String,
    val contentDescription: String,
    val aspectRatio: Float,
    val averageColor: String,
    val isFavorite: Boolean
)

fun Wallpaper.toUi(): WallpaperUi {
    return WallpaperUi(
        id = id,
        imageUrl = imageUrl,
        smallImageUrl = smallImageUrl,
        photographerName = "Photo by ${photographer}",
        contentDescription = "Photo by ${photographer}",
        aspectRatio = if (height > 0) width.toFloat() / height.toFloat() else 0.7f,
        averageColor = averageColor,
        isFavorite = isFavorite
    )
}