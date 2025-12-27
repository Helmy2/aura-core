package com.example.aura.feature.detail

import androidx.lifecycle.viewModelScope
import com.example.aura.domain.repository.FavoritesRepository
import com.example.aura.domain.repository.WallpaperRepository
import com.example.aura.feature.detail.DetailEffect.ShowError
import com.example.aura.feature.detail.DetailIntent.DownloadError
import com.example.aura.feature.detail.DetailIntent.DownloadFinished
import com.example.aura.feature.detail.DetailIntent.DownloadWallpaper
import com.example.aura.feature.detail.DetailIntent.FavoriteStatusUpdated
import com.example.aura.feature.detail.DetailIntent.LoadError
import com.example.aura.feature.detail.DetailIntent.LoadWallpaper
import com.example.aura.feature.detail.DetailIntent.OnBackClicked
import com.example.aura.feature.detail.DetailIntent.ToggleFavorite
import com.example.aura.feature.detail.DetailIntent.WallpaperLoaded
import com.example.aura.shared.core.mvi.MviViewModel
import com.example.aura.shared.core.util.ImageDownloader
import com.example.aura.shared.model.toUi
import com.example.aura.shared.navigation.AppNavigator
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class DetailViewModel(
    private val wallpaperRepository: WallpaperRepository,
    private val favoritesRepository: FavoritesRepository,
    private val imageDownloader: ImageDownloader,
    private val navigator: AppNavigator
) : MviViewModel<DetailState, DetailIntent, DetailEffect>(
    initialState = DetailState()
) {

    override fun reduce(
        currentState: DetailState,
        intent: DetailIntent
    ): Pair<DetailState, DetailEffect?> {
        return when (intent) {
            is LoadWallpaper -> {
                loadWallpaper(intent.wallpaperId)
                currentState.copy(isLoading = true).only()
            }

            is OnBackClicked -> {
                navigator.back()
                currentState.only()
            }

            is WallpaperLoaded -> {
                observeFavoriteStatus(intent.wallpaper.id)
                currentState.copy(
                    wallpaper = intent.wallpaper,
                    isLoading = false,
                    error = null
                ).only()
            }

            is LoadError -> {
                currentState.copy(
                    isLoading = false,
                    error = intent.message
                ).with(ShowError(intent.message))
            }

            is ToggleFavorite -> {
                viewModelScope.launch {
                    try {
                        val wallpaper = wallpaperRepository.getWallpaperById(intent.wallpaper.id)
                        favoritesRepository.toggleFavorite(wallpaper)
                    } catch (e: Exception) {
                        sendIntent(DownloadError(e.message ?: "Failed to update favorite"))
                    }
                }
                currentState.only()
            }

            is FavoriteStatusUpdated -> {
                currentState.wallpaper?.let { wallpaper ->
                    currentState.copy(
                        wallpaper = wallpaper.copy(isFavorite = intent.isFavorite)
                    ).only()
                } ?: currentState.only()
            }

            is DownloadWallpaper -> {
                downloadWallpaper()
                currentState.copy(isDownloading = true).only()
            }

            is DownloadError -> {
                currentState.copy(isDownloading = false)
                    .with(ShowError(intent.message))
            }

            is DownloadFinished -> {
                currentState.copy(isDownloading = false).only()
            }
        }
    }

    private fun loadWallpaper(wallpaperId: Long) {
        viewModelScope.launch {
            try {
                val wallpaper = wallpaperRepository.getWallpaperById(wallpaperId)
                val isFavorite = favoritesRepository.isFavorite(wallpaperId)
                sendIntent(WallpaperLoaded(wallpaper.toUi(isFavorite)))
            } catch (e: Exception) {
                sendIntent(LoadError(e.message ?: "Failed to load wallpaper"))
            }
        }
    }

    private fun observeFavoriteStatus(wallpaperId: Long) {
        favoritesRepository.getAllFavorites()
            .map { favorites -> favorites.any { it.id == wallpaperId } }
            .onEach { isFavorite ->
                sendIntent(FavoriteStatusUpdated(isFavorite))
            }
            .launchIn(viewModelScope)
    }

    private fun downloadWallpaper() {
        val wallpaper = currentState.wallpaper ?: return

        viewModelScope.launch {
            val fileName = "aura_${wallpaper.id}"
            val success = imageDownloader.downloadImage(wallpaper.imageUrl, fileName)

            sendIntent(DownloadFinished(success))
        }
    }
}
