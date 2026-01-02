package com.example.aura.domain.model

sealed class MediaContent {
    abstract val id: Long
    abstract val addedAt: Long

    data class WallpaperContent(val wallpaper: Wallpaper) : MediaContent() {
        override val id: Long = wallpaper.id
        override val addedAt: Long = wallpaper.addedAt
    }

    data class VideoContent(val video: Video) : MediaContent() {
        override val id: Long = video.id
        override val addedAt: Long = video.addedAt
    }
}
