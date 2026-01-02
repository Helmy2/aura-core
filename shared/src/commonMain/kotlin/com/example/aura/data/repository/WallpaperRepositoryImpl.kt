package com.example.aura.data.repository

import com.example.aura.data.local.WallpaperLocalDataSource
import com.example.aura.data.mapper.toDomain
import com.example.aura.data.remote.PexelsRemoteDataSource
import com.example.aura.domain.model.Wallpaper
import com.example.aura.domain.repository.WallpaperRepository

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
}
