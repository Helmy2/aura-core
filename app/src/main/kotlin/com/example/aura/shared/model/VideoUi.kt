package com.example.aura.shared.model

import androidx.compose.runtime.Immutable
import com.example.aura.domain.model.Video
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class VideoUi(
    val id: Long,
    val width: Int,
    val height: Int,
    val duration: Int,
    val imageUrl: String,
    val videoUrl: String,
    val photographer: String,
    val isFavorite: Boolean
)


fun Video.toUi(): VideoUi {
    return VideoUi(
        id = id,
        width = width,
        height = height,
        duration = duration,
        imageUrl = image,
        videoUrl = videoUrl,
        photographer = user.name,
        isFavorite = isFavorite
    )
}

