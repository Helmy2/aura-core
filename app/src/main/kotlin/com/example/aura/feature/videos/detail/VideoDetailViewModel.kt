package com.example.aura.feature.videos.detail

import androidx.lifecycle.viewModelScope
import com.example.aura.domain.model.Video
import com.example.aura.domain.repository.FavoritesRepository
import com.example.aura.domain.repository.VideoRepository
import com.example.aura.shared.core.mvi.MviViewModel
import com.example.aura.shared.core.util.VideoDownloader
import com.example.aura.shared.navigation.AppNavigator
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class VideoDetailViewModel(
    private val videoRepository: VideoRepository,
    private val favoritesRepository: FavoritesRepository,
    private val navigator: AppNavigator,
    private val videoDownloader: VideoDownloader
) : MviViewModel<VideoDetailState, VideoDetailIntent, VideoDetailEffect>(VideoDetailState()) {

    override fun reduce(
        currentState: VideoDetailState,
        intent: VideoDetailIntent
    ): Pair<VideoDetailState, VideoDetailEffect?> {
        return when (intent) {
            is VideoDetailIntent.LoadVideo -> {
                loadVideo(intent.videoId)
                observeFavoriteStatus(intent.videoId)
                currentState.copy(isLoading = true).only()
            }

            is VideoDetailIntent.VideoLoaded -> {
                currentState.copy(video = intent.video, isLoading = false).only()
            }

            is VideoDetailIntent.LoadError -> {
                currentState.copy(isLoading = false, error = intent.message)
                    .with(VideoDetailEffect.ShowError(intent.message))
            }

            is VideoDetailIntent.OnBackClicked -> {
                navigator.back()
                currentState.only()
            }

            is VideoDetailIntent.DownloadVideo -> {
                if (currentState.video != null) {
                    downloadVideo(currentState.video)
                    currentState.copy(isDownloading = true).only()
                } else {
                    currentState.with(VideoDetailEffect.ShowError("Video not found"))
                }
            }

            is VideoDetailIntent.DownloadFinished -> {
                val message = if (intent.success) "Download started" else "Download failed"
                currentState.copy(isDownloading = false)
                    .with(VideoDetailEffect.ShowMessage(message))
            }

            is VideoDetailIntent.ToggleFavorite -> {
                val video = currentState.video
                if (video != null) {
                    viewModelScope.launch {
                        try {
                            favoritesRepository.toggleFavorite(video)
                        } catch (e: Exception) {
                            sendIntent(VideoDetailIntent.LoadError("Failed to update favorite"))
                        }
                    }
                }
                currentState.only()
            }

            is VideoDetailIntent.FavoriteStatusUpdated -> {
                val updatedVideo = currentState.video?.copy(isFavorite = intent.isFavorite)
                currentState.copy(video = updatedVideo).only()
            }
        }
    }

    private fun loadVideo(videoId: Long) {
        viewModelScope.launch {
            try {
                val video = videoRepository.getVideoById(videoId)
                sendIntent(VideoDetailIntent.VideoLoaded(video))
            } catch (e: Exception) {
                sendIntent(VideoDetailIntent.LoadError(e.message ?: "Unknown error"))
            }
        }
    }

    private fun observeFavoriteStatus(videoId: Long) {
        favoritesRepository.observeFavoriteVideos()
            .map { favorites -> favorites.any { it.id == videoId } }
            .onEach { isFavorite ->
                sendIntent(VideoDetailIntent.FavoriteStatusUpdated(isFavorite))
            }
            .launchIn(viewModelScope)
    }

    private fun downloadVideo(video: Video) {
        try {
            videoDownloader.downloadVideo(video.videoUrl, "aura_video_${video.id}")
            sendIntent(VideoDetailIntent.DownloadFinished(true))
        } catch (e: Exception) {
            sendIntent(VideoDetailIntent.DownloadFinished(false))
        }
    }
}