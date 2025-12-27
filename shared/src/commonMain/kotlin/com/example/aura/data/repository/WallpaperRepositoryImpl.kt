package com.example.aura.data.repository

import com.example.aura.data.local.WallpaperLocalDataSource
import com.example.aura.data.mapper.toDomain
import com.example.aura.data.remote.PexelsRemoteDataSource
import com.example.aura.domain.model.Wallpaper
import com.example.aura.domain.repository.WallpaperRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class WallpaperRepositoryImpl(
    private val remoteDataSource: PexelsRemoteDataSource,
    private val localDataSource: WallpaperLocalDataSource
) : WallpaperRepository {

    override suspend fun getCuratedWallpapers(page: Int): List<Wallpaper> {
        val remoteWallpapers =
            remoteDataSource.getCuratedWallpapers(page).photos

        val favoriteIds = localDataSource.getAllFavorites().map { it.id }.toSet()

        return remoteWallpapers.map { it.toDomain(favoriteIds.contains(it.id)) }
    }

    override suspend fun searchWallpapers(query: String, page: Int): List<Wallpaper> {
        val response = remoteDataSource.searchWallpapers(query = query, page = page)

        val favoriteIds = localDataSource.getAllFavorites().map { it.id }.toSet()

        return response.photos.map { it.toDomain(favoriteIds.contains(it.id)) }
    }

    override suspend fun getWallpaperById(id: Long): Wallpaper {
        val response = remoteDataSource.getWallpaperById(id = id)
        return response.toDomain(localDataSource.isFavorite(id))
    }

    override fun observeFavorites(): Flow<List<Wallpaper>> {
        return localDataSource.observeAllFavorites()
    }

    override suspend fun getFavorites(): List<Wallpaper> {
        return localDataSource.getAllFavorites()
    }

    override suspend fun isFavorite(wallpaperId: Long): Boolean {
        return localDataSource.isFavorite(wallpaperId)
    }

    override suspend fun toggleFavorite(wallpaper: Wallpaper) {
        localDataSource.toggleFavorite(wallpaper)
    }

    override suspend fun addFavorite(wallpaper: Wallpaper) {
        localDataSource.addFavorite(wallpaper)
    }

    override suspend fun removeFavorite(wallpaperId: Long) {
        localDataSource.removeFavorite(wallpaperId)
    }

    override fun observeFavoritesCount(wallpaperId: Long): Flow<Long> {
        return localDataSource.getFavoritesCount()
    }

    override suspend fun getFavoritesCount(wallpaperId: Long): Long {
        return localDataSource.getFavoritesCount().first()
    }
}
