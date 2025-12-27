package com.example.aura.feature.home

import androidx.lifecycle.viewModelScope
import com.example.aura.domain.repository.FavoritesRepository
import com.example.aura.domain.repository.WallpaperRepository
import com.example.aura.shared.core.mvi.MviViewModel
import com.example.aura.shared.model.toUi
import com.example.aura.shared.navigation.AppNavigator
import com.example.aura.shared.navigation.Destination
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class HomeViewModel(
    private val wallpaperRepository: WallpaperRepository,
    private val navigator: AppNavigator,
    private val favoritesRepository: FavoritesRepository
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
                )
            }

            is HomeIntent.OnError -> {
                currentState.copy(isLoading = false, error = intent.message)
            }

            is HomeIntent.OnWallpaperClicked -> {
                navigator.navigate(Destination.Detail(id = intent.wallpaper.id))
                currentState
            }

            is HomeIntent.LoadNextPage -> {
                if (!currentState.isPaginationLoading && !currentState.isEndReached) {
                    if (currentState.isSearchMode) {
                        performSearch(currentState.searchQuery, page = currentState.currentPage + 1)
                    } else {
                        loadWallpapers(page = currentState.currentPage + 1)
                    }
                    currentState.copy(
                        isPaginationLoading = true
                    )
                } else {
                    currentState
                }
            }

            is HomeIntent.AppendWallpapers -> {
                if (currentState.isSearchMode) {
                    currentState.copy(
                        searchWallpapers = currentState.searchWallpapers + intent.newWallpapers,
                        isLoading = false,
                        isPaginationLoading = false,
                        currentPage = intent.page
                    )
                } else {
                    currentState.copy(
                        wallpapers = currentState.wallpapers + intent.newWallpapers,
                        isLoading = false,
                        isPaginationLoading = false,
                        currentPage = intent.page
                    )
                }
            }

            is HomeIntent.SetEndReached -> {
                currentState.copy(
                    isEndReached = true
                )
            }

            is HomeIntent.OnSearchQueryChanged -> {
                currentState.copy(searchQuery = intent.query)
            }

            is HomeIntent.OnSearchTriggered -> {
                if (currentState.searchQuery.isBlank()) {
                    currentState
                } else {
                    performSearch(query = currentState.searchQuery, page = 1)

                    currentState.copy(
                        isSearchMode = true,
                        isLoading = true,
                        isEndReached = false,
                        currentPage = 1,
                        searchWallpapers = emptyList()
                    )
                }
            }

            is HomeIntent.OnClearSearch -> {
                currentState.copy(
                    isSearchMode = false,
                    searchQuery = "",
                    isEndReached = false,
                )
            }

            is HomeIntent.ToggleFavorite -> {
                viewModelScope.launch {
                    try {
                        val isFavorite =
                            wallpaperRepository.getWallpaperById(id = intent.wallpaper.id)
                        favoritesRepository.toggleFavorite(isFavorite)
                    } catch (e: Exception) {

                    }
                }
                currentState
            }

            is HomeIntent.FavoriteStatusUpdated -> {
                val updatedWallpapers = currentState.wallpapers.map { wallpaper ->
                    wallpaper.copy(isFavorite = intent.favoriteIds.contains(wallpaper.id))
                }
                currentState.copy(
                    wallpapers = updatedWallpapers, favoriteIds = intent.favoriteIds
                )
            }
        }.only()
    }

    private fun loadWallpapers(page: Int = 1) {
        viewModelScope.launch {
            try {
                val newWallpapers = wallpaperRepository.getCuratedWallpapers(page = page)

                if (newWallpapers.isEmpty()) {
                    sendIntent(HomeIntent.SetEndReached)
                } else {
                    val favoriteIds = favoritesRepository.getAllFavorites()
                        .first()
                        .map { it.id }
                        .toSet()

                    val uiWallpapers = newWallpapers.map {
                        it.toUi(
                            isFavorite = favoriteIds.contains(it.id)
                        )
                    }
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
                    val favoriteIds = favoritesRepository.getAllFavorites()
                        .first()
                        .map { it.id }
                        .toSet()

                    val uiWallpapers = results.map {
                        it.toUi(
                            isFavorite = favoriteIds.contains(it.id)
                        )
                    }
                    sendIntent(HomeIntent.AppendWallpapers(uiWallpapers, page))
                }
            } catch (e: Exception) {
                sendIntent(HomeIntent.OnError(e.message.orEmpty()))
            }
        }
    }

    private fun observeFavorites() {
        favoritesRepository.getAllFavorites().map { favorites -> favorites.map { it.id }.toSet() }
            .onEach { favoriteIds ->
                sendIntent(HomeIntent.FavoriteStatusUpdated(favoriteIds))
            }.launchIn(viewModelScope)
    }
}
