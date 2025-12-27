package com.example.aura.feature.favorites

import com.example.aura.shared.model.WallpaperUi

data class FavoritesState(
    val favorites: List<WallpaperUi> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

sealed interface FavoritesIntent {
    data object LoadFavorites : FavoritesIntent

    data class FavoritesLoaded(val favorites: List<WallpaperUi>) : FavoritesIntent

    data class FavoritesLoadError(val message: String) : FavoritesIntent

    data class RemoveFavorite(val wallpaper: WallpaperUi) : FavoritesIntent

    data class RemoveFavoriteError(val message: String) : FavoritesIntent

    data object ClearAllFavorites : FavoritesIntent

    data class ClearFavoritesError(val message: String) : FavoritesIntent

    data class OnWallpaperClicked(val wallpaper: WallpaperUi) : FavoritesIntent
}

sealed interface FavoritesEffect {
    data class ShowError(val message: String) : FavoritesEffect
}
