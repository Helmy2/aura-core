package com.example.aura.feature.favorites

import com.example.aura.domain.model.MediaContent

data class FavoritesState(
    val items: List<MediaContent> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

sealed interface FavoritesIntent {
    data object LoadFavorites : FavoritesIntent
    data class FavoritesLoaded(val items: List<MediaContent>) : FavoritesIntent
    data class FavoritesLoadError(val message: String) : FavoritesIntent

    data class RemoveFormFavorite(val item: MediaContent) : FavoritesIntent
    data class RemoveError(val message: String) : FavoritesIntent
    data object ClearAllFavorites : FavoritesIntent
    data class OnItemClicked(val item: MediaContent) : FavoritesIntent
}

sealed interface FavoritesEffect {
    data class ShowError(val message: String) : FavoritesEffect
}
