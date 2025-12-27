package com.example.aura.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import com.example.aura.database.AuraDatabase
import com.example.aura.domain.model.Wallpaper
import com.example.aura.domain.repository.FavoritesRepository
import com.example.aura.domain.util.TimeManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class FavoritesRepositoryImpl(
    database: AuraDatabase,
    private val timeManager: TimeManager
) : FavoritesRepository {

    private val queries = database.favoriteWallpaperQueries

    override fun getAllFavorites(): Flow<List<Wallpaper>> {
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
                    )
                }
            }
    }

    override suspend fun isFavorite(wallpaperId: Long): Boolean {
        return withContext(Dispatchers.Default) {
            queries.isFavorite(wallpaperId).executeAsOne()
        }
    }

    override suspend fun addFavorite(wallpaper: Wallpaper) {
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

    override suspend fun removeFavorite(wallpaperId: Long) {
        withContext(Dispatchers.Default) {
            queries.deleteFavorite(wallpaperId)
        }
    }

    override suspend fun toggleFavorite(wallpaper: Wallpaper) {
        withContext(Dispatchers.Default) {
            if (isFavorite(wallpaper.id)) {
                removeFavorite(wallpaper.id)
            } else {
                addFavorite(wallpaper)
            }
        }
    }

    override fun getFavoritesCount(): Flow<Long> {
        return queries.getFavoritesCount()
            .asFlow()
            .mapToOne(Dispatchers.Default)
    }
}
