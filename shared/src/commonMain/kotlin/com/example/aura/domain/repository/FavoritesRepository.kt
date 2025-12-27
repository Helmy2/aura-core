package com.example.aura.domain.repository

import com.example.aura.domain.model.Wallpaper
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun getAllFavorites(): Flow<List<Wallpaper>>
    suspend fun isFavorite(wallpaperId: Long): Boolean
    suspend fun addFavorite(wallpaper: Wallpaper)
    suspend fun removeFavorite(wallpaperId: Long)
    suspend fun toggleFavorite(wallpaper: Wallpaper)
    fun getFavoritesCount(): Flow<Long>
}
