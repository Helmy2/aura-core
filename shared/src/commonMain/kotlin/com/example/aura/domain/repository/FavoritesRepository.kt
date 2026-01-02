package com.example.aura.domain.repository

import com.example.aura.domain.model.MediaContent
import com.example.aura.domain.model.Video
import com.example.aura.domain.model.Wallpaper
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    suspend fun toggleFavorite(mediaContent: MediaContent)
    suspend fun toggleFavorite(video: Video)
    suspend fun toggleFavorite(wallpaper: Wallpaper)

    fun observeFavorites(): Flow<List<MediaContent>>
    fun observeFavoriteVideos(): Flow<List<Video>>
    fun observeFavoritesWallpapers(): Flow<List<Wallpaper>>

    suspend fun removeFromFavorite(mediaContent: MediaContent)
    suspend fun isVideoFavorite(videoId: Long): Boolean
    suspend fun isWallpapersFavorite(wallpaperId: Long): Boolean
}