package com.example.aura.data.repository

import com.example.aura.data.local.VideoLocalDataSource
import com.example.aura.data.local.WallpaperLocalDataSource
import com.example.aura.domain.model.MediaContent
import com.example.aura.domain.model.Video
import com.example.aura.domain.model.Wallpaper
import com.example.aura.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class FavoritesRepositoryImpl(
    private val wallpaperDao: WallpaperLocalDataSource,
    private val videoDao: VideoLocalDataSource,
) : FavoritesRepository {
    override fun observeFavorites(): Flow<List<MediaContent>> {
        return combine(
            wallpaperDao.observeFavorites(), videoDao.observeFavorites()
        ) { wallpapers, videos ->
            val wItems = wallpapers.map { MediaContent.WallpaperContent(it) }
            val vItems = videos.map { MediaContent.VideoContent(it) }
            (wItems + vItems).sortedByDescending { it.addedAt }
        }
    }

    override suspend fun toggleFavorite(mediaContent: MediaContent) {
        when (mediaContent) {
            is MediaContent.VideoContent -> videoDao.toggleFavorite(mediaContent.video)

            is MediaContent.WallpaperContent -> wallpaperDao.toggleFavorite(mediaContent.wallpaper)
        }
    }

    override suspend fun removeFromFavorite(mediaContent: MediaContent) {
        when (mediaContent) {
            is MediaContent.VideoContent -> videoDao.removeFavorite(mediaContent.video.id)

            is MediaContent.WallpaperContent -> wallpaperDao.removeFavorite(mediaContent.wallpaper.id)
        }
    }

    override suspend fun toggleFavorite(video: Video) {
        videoDao.toggleFavorite(video)
    }

    override suspend fun isVideoFavorite(videoId: Long): Boolean {
        return videoDao.isFavorite(videoId)
    }

    override fun observeFavoriteVideos(): Flow<List<Video>> {
        return videoDao.observeFavorites().map {
            it.map { video -> video.copy(isFavorite = true) }
        }
    }

    override fun observeFavoritesWallpapers(): Flow<List<Wallpaper>> {
        return wallpaperDao.observeFavorites()
    }


    override suspend fun isWallpapersFavorite(wallpaperId: Long): Boolean {
        return wallpaperDao.isFavorite(wallpaperId)
    }

    override suspend fun toggleFavorite(wallpaper: Wallpaper) {
        wallpaperDao.toggleFavorite(wallpaper)
    }
}
