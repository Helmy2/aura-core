package com.example.aura.data.repository

import com.example.aura.data.local.VideoLocalDataSource
import com.example.aura.data.mapper.toDomain
import com.example.aura.data.remote.PexelsRemoteDataSource
import com.example.aura.domain.model.Video
import com.example.aura.domain.repository.VideoRepository

class VideoRepositoryImpl(
    private val remoteDataSource: PexelsRemoteDataSource,
    private val localDataSource: VideoLocalDataSource
) : VideoRepository {

    override suspend fun getPopularVideos(page: Int): List<Video> {
        val response = remoteDataSource.getPopularVideos(page)
        val videos = response.videos.map {
            it.toDomain(
                isFavorite = localDataSource.isFavorite(it.id)
            )
        }
        return videos
    }

    override suspend fun searchVideos(query: String, page: Int): List<Video> {
        val response = remoteDataSource.searchVideos(query, page)
        val videos = response.videos.map {
            it.toDomain(
                isFavorite = localDataSource.isFavorite(it.id)
            )
        }
        return videos
    }

    override suspend fun getVideoById(id: Long): Video {
        val video = remoteDataSource.getVideoById(id).toDomain(
            isFavorite = localDataSource.isFavorite(id)
        )
        return video
    }
}