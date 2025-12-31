package com.example.aura.data.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.aura.database.AuraDatabase
import com.example.aura.domain.model.User
import com.example.aura.domain.model.Video
import com.example.aura.domain.util.TimeManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class VideoLocalDataSource(
    database: AuraDatabase,
    private val timeManager: TimeManager
) {
    private val queries = database.favoriteVideosQueries

    fun toggleFavorite(video: Video) {
        val exists = queries.existsVideo(video.id).executeAsOne()
        if (exists) {
            queries.deleteVideo(video.id)
        } else {
            queries.insertVideo(
                id = video.id,
                videoUrl = video.videoUrl,
                imageUrl = video.imageUrl,
                duration = video.duration.toLong(),
                width = video.width.toLong(),
                height = video.height.toLong(),
                userName = video.user.name,
                userUrl = video.user.url,
                timestamp = timeManager.currentTimeMillis()
            )
        }
    }

    fun removeFavorite(videoId: Long) {
        queries.deleteVideo(videoId)
    }

    fun isFavorite(videoId: Long): Boolean {
        return queries.existsVideo(videoId).executeAsOne()
    }

    fun observeFavorites(): Flow<List<Video>> {
        return queries.getAllVideos()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities ->
                entities.map { entity ->
                    Video(
                        id = entity.id,
                        width = entity.width.toInt(),
                        height = entity.height.toInt(),
                        url = entity.videoUrl,
                        image = entity.imageUrl,
                        duration = entity.duration.toInt(),
                        user = User(0, entity.userName, entity.userUrl),
                        videoFiles = emptyList(),
                        videoPictures = emptyList(),
                        addedAt = entity.timestamp,
                        isFavorite = true
                    )
                }
            }
    }
}