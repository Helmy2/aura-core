package com.example.aura.data.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import com.example.aura.database.AuraDatabase
import com.example.aura.domain.model.Wallpaper
import com.example.aura.domain.util.TimeManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class WallpaperLocalDataSource(
    database: AuraDatabase,
    private val timeManager: TimeManager
) {
    private val queries = database.favoriteWallpaperQueries

    fun observeFavorites(): Flow<List<Wallpaper>> {
        return queries.getAllFavorites()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { favorites ->
                favorites.map { favorite ->
                    Wallpaper(
                        id = favorite.id,
                        imageUrl = favorite.imageUrl,
                        smallImageUrl = favorite.smallImageUrl,
                        photographer = favorite.photographer,
                        photographerUrl = favorite.photographerUrl,
                        averageColor = favorite.averageColor,
                        height = favorite.height.toInt(),
                        width = favorite.width.toInt(),
                        addedAt = favorite.addedAt,
                        isFavorite = true
                    )
                }
            }
    }

    suspend fun getAllFavorites(): List<Wallpaper> = withContext(Dispatchers.Default) {
        queries.getAllFavorites().executeAsList().map { favorite ->
            Wallpaper(
                id = favorite.id,
                imageUrl = favorite.imageUrl,
                smallImageUrl = favorite.smallImageUrl,
                photographer = favorite.photographer,
                photographerUrl = favorite.photographerUrl,
                averageColor = favorite.averageColor,
                height = favorite.height.toInt(),
                width = favorite.width.toInt(),
                addedAt = favorite.addedAt,
                isFavorite = true,
            )
        }
    }

    suspend fun isFavorite(wallpaperId: Long): Boolean {
        return withContext(Dispatchers.Default) {
            queries.isFavorite(wallpaperId).executeAsOne()
        }
    }

    suspend fun addFavorite(wallpaper: Wallpaper) {
        withContext(Dispatchers.Default) {
            queries.insertFavorite(
                id = wallpaper.id,
                imageUrl = wallpaper.imageUrl,
                smallImageUrl = wallpaper.smallImageUrl,
                photographer = wallpaper.photographer,
                photographerUrl = wallpaper.photographerUrl,
                averageColor = wallpaper.averageColor,
                height = wallpaper.height.toLong(),
                width = wallpaper.width.toLong(),
                addedAt = timeManager.currentTimeMillis()
            )
        }
    }

    suspend fun removeFavorite(wallpaperId: Long) {
        withContext(Dispatchers.Default) {
            queries.deleteFavorite(wallpaperId)
        }
    }

    suspend fun toggleFavorite(wallpaper: Wallpaper) {
        withContext(Dispatchers.Default) {
            if (isFavorite(wallpaper.id)) {
                removeFavorite(wallpaper.id)
            } else {
                addFavorite(wallpaper)
            }
        }
    }

    fun getFavoritesCount(): Flow<Long> {
        return queries.getFavoritesCount()
            .asFlow()
            .mapToOne(Dispatchers.Default)
    }
}