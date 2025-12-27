package com.example.aura.feature.home

import com.example.aura.shared.model.WallpaperUi

data class HomeState(
    val isLoading: Boolean = true,
    val isPaginationLoading: Boolean = false,
    val currentPage: Int = 1,
    val isEndReached: Boolean = false,
    val wallpapers: List<WallpaperUi> = emptyList(),
    val error: String? = null,
    val searchQuery: String = "",
    val isSearchMode: Boolean = false,
    val searchWallpapers: List<WallpaperUi> = emptyList(),
    val favoriteIds: Set<Long> = emptySet()
)

sealed interface HomeIntent {
    data object LoadCuratedWallpapers : HomeIntent
    data object LoadNextPage : HomeIntent
    data class OnError(val message: String) : HomeIntent
    data class OnWallpaperClicked(val wallpaper: WallpaperUi) : HomeIntent

    data class AppendWallpapers(
        val newWallpapers: List<WallpaperUi>,
        val page: Int
    ) : HomeIntent

    data object SetEndReached : HomeIntent

    data class OnSearchQueryChanged(val query: String) : HomeIntent
    data object OnSearchTriggered : HomeIntent
    data object OnClearSearch : HomeIntent

    data class ToggleFavorite(val wallpaper: WallpaperUi) : HomeIntent
    data class FavoriteStatusUpdated(val favoriteIds: Set<Long>) : HomeIntent
}