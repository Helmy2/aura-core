package com.example.aura.feature.home

import androidx.lifecycle.viewModelScope
import com.example.aura.domain.repository.WallpaperRepository
import com.example.aura.shared.core.mvi.MviViewModel
import com.example.aura.shared.model.toUi
import com.example.aura.shared.navigation.AppNavigator
import com.example.aura.shared.navigation.Destination
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class HomeViewModel(
    private val wallpaperRepository: WallpaperRepository, private val navigator: AppNavigator
) : MviViewModel<HomeState, HomeIntent, Nothing>(
    initialState = HomeState()
) {

    init {
        sendIntent(HomeIntent.LoadCuratedWallpapers)
        observeFavorites()
    }

    override fun reduce(
        currentState: HomeState, intent: HomeIntent
    ): Pair<HomeState, Nothing?> {
        return when (intent) {
            is HomeIntent.LoadCuratedWallpapers -> {
                loadWallpapers()
                currentState.copy(
                    isSearchMode = false, searchQuery = "", isLoading = true
                ).only()
            }

            is HomeIntent.OnError -> {
                currentState.copy(isLoading = false, error = intent.message).only()
            }

            is HomeIntent.OnWallpaperClicked -> {
                navigator.navigate(Destination.Detail(id = intent.wallpaper.id))
                currentState.only()
            }

            is HomeIntent.LoadNextPage -> {
                if (!currentState.isPaginationLoading && !currentState.isEndReached) {
                    if (currentState.isSearchMode) {
                        performSearch(currentState.searchQuery, page = currentState.currentPage + 1)
                    } else {
                        loadWallpapers(page = currentState.currentPage + 1)
                    }
                    currentState.copy(isPaginationLoading = true).only()
                } else {
                    currentState.only()
                }
            }

            is HomeIntent.AppendWallpapers -> {
                if (currentState.isSearchMode) {
                    currentState.copy(
                        searchWallpapers = currentState.searchWallpapers + intent.newWallpapers,
                        isLoading = false,
                        isPaginationLoading = false,
                        currentPage = intent.page
                    ).only()
                } else {
                    currentState.copy(
                        wallpapers = currentState.wallpapers + intent.newWallpapers,
                        isLoading = false,
                        isPaginationLoading = false,
                        currentPage = intent.page
                    ).only()
                }
            }

            is HomeIntent.SetEndReached -> {
                currentState.copy(isEndReached = true).only()
            }

            is HomeIntent.OnSearchQueryChanged -> {
                currentState.copy(searchQuery = intent.query).only()
            }

            is HomeIntent.OnSearchTriggered -> {
                if (currentState.searchQuery.isBlank()) {
                    currentState.only()
                } else {
                    performSearch(query = currentState.searchQuery, page = 1)
                    currentState.copy(
                        isSearchMode = true,
                        isLoading = true,
                        isEndReached = false,
                        currentPage = 1,
                        searchWallpapers = emptyList()
                    ).only()
                }
            }

            is HomeIntent.OnClearSearch -> {
                currentState.copy(
                    isSearchMode = false,
                    searchQuery = "",
                    isEndReached = false,
                ).only()
            }

            is HomeIntent.ToggleFavorite -> {
                viewModelScope.launch {
                    try {
                        val wallpaper =
                            wallpaperRepository.getWallpaperById(id = intent.wallpaper.id)
                        wallpaperRepository.toggleFavorite(wallpaper)
                    } catch (e: Exception) {

                    }
                }
                currentState.only()
            }

            is HomeIntent.FavoriteStatusUpdated -> {
                val updatedWallpapers = currentState.wallpapers.map { wallpaper ->
                    wallpaper.copy(isFavorite = intent.favoriteIds.contains(wallpaper.id))
                }
                val updatedSearchWallpapers = currentState.searchWallpapers.map { wallpaper ->
                    wallpaper.copy(isFavorite = intent.favoriteIds.contains(wallpaper.id))
                }

                currentState.copy(
                    wallpapers = updatedWallpapers,
                    searchWallpapers = updatedSearchWallpapers,
                    favoriteIds = intent.favoriteIds
                ).only()
            }
        }
    }

    private fun loadWallpapers(page: Int = 1) {
        viewModelScope.launch {
            try {
                val wallpapers = wallpaperRepository.getCuratedWallpapers(page = page)

                if (wallpapers.isEmpty()) {
                    sendIntent(HomeIntent.SetEndReached)
                } else {
                    val uiWallpapers = wallpapers.map { it.toUi(isFavorite = it.isFavorite) }
                    sendIntent(HomeIntent.AppendWallpapers(uiWallpapers, page))
                }
            } catch (e: Exception) {
                sendIntent(HomeIntent.OnError(e.message.orEmpty()))
            }
        }
    }

    private fun performSearch(query: String, page: Int) {
        viewModelScope.launch {
            try {
                val results = wallpaperRepository.searchWallpapers(query, page)

                if (results.isEmpty()) {
                    sendIntent(HomeIntent.SetEndReached)
                } else {
                    val uiWallpapers = results.map { it.toUi(isFavorite = it.isFavorite) }
                    sendIntent(HomeIntent.AppendWallpapers(uiWallpapers, page))
                }
            } catch (e: Exception) {
                sendIntent(HomeIntent.OnError(e.message.orEmpty()))
            }
        }
    }

    private fun observeFavorites() {
        wallpaperRepository.observeFavorites().map { favorites -> favorites.map { it.id }.toSet() }
            .onEach { favoriteIds ->
                sendIntent(HomeIntent.FavoriteStatusUpdated(favoriteIds))
            }.launchIn(viewModelScope)
    }
}
