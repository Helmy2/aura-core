package com.example.aura.feature.detail

import com.example.aura.shared.model.WallpaperUi

data class DetailState(
    val wallpaper: WallpaperUi? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val isDownloading: Boolean = false
)

sealed interface DetailIntent {
    data class LoadWallpaper(val wallpaperId: Long) : DetailIntent
    data class WallpaperLoaded(val wallpaper: WallpaperUi) : DetailIntent
    data class LoadError(val message: String) : DetailIntent

    data class ToggleFavorite(val wallpaper: WallpaperUi) : DetailIntent
    data class FavoriteStatusUpdated(val isFavorite: Boolean) : DetailIntent

    data object DownloadWallpaper : DetailIntent
    data class DownloadError(val message: String) : DetailIntent
    data object OnBackClicked : DetailIntent
    data class DownloadFinished(val success: Boolean) : DetailIntent
}

sealed interface DetailEffect {
    data class ShowError(val message: String) : DetailEffect
}