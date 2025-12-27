package com.example.aura.domain.repository

import com.example.aura.domain.model.Wallpaper
import kotlinx.coroutines.flow.Flow

interface WallpaperRepository {
    suspend fun getCuratedWallpapers(page: Int = 1): List<Wallpaper>
    suspend fun searchWallpapers(query: String, page: Int = 1): List<Wallpaper>
    suspend fun getWallpaperById(id: Long): Wallpaper

    fun observeFavorites(): Flow<List<Wallpaper>>
    suspend fun getFavorites(): List<Wallpaper>
    suspend fun isFavorite(wallpaperId: Long): Boolean
    suspend fun toggleFavorite(wallpaper: Wallpaper)
    suspend fun addFavorite(wallpaper: Wallpaper)
    suspend fun removeFavorite(wallpaperId: Long)
    fun observeFavoritesCount(wallpaperId: Long): Flow<Long>
    suspend fun getFavoritesCount(wallpaperId: Long): Long
}
