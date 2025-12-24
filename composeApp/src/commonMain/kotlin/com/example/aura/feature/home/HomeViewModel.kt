package com.example.aura.feature.home

import androidx.lifecycle.viewModelScope
import com.example.aura.core.mvi.BaseViewModel
import com.example.aura.core.mvi.ReducerResult
import com.example.aura.domain.repository.WallpaperRepository
import com.example.aura.navigation.AppNavigator
import com.example.aura.navigation.DetailRoute
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: WallpaperRepository,
    private val navigator: AppNavigator
) : BaseViewModel<HomeState, HomeIntent, Nothing>(
    initialState = HomeState()
) {

    init {
        sendIntent(HomeIntent.LoadCuratedWallpapers)
    }

    override fun reduce(
        oldState: HomeState,
        intent: HomeIntent
    ): ReducerResult<HomeState, Nothing> {
        return when (intent) {
            is HomeIntent.LoadCuratedWallpapers -> {
                viewModelScope.launch {
                    try {
                        val wallpapers = repository.getCuratedWallpapers()
                        sendIntent(HomeIntent.OnWallpapersLoaded(wallpapers))
                    } catch (e: Exception) {
                        sendIntent(HomeIntent.OnError(e.message ?: "Unknown error"))
                    }
                }
                ReducerResult(oldState.copy(isLoading = true, error = null))
            }

            is HomeIntent.OnWallpapersLoaded -> {
                ReducerResult(oldState.copy(isLoading = false, wallpapers = intent.wallpapers))
            }

            is HomeIntent.OnError -> {
                ReducerResult(oldState.copy(isLoading = false, error = intent.message))
            }

            is HomeIntent.OnWallpaperClicked -> {
                navigator.navigate(DetailRoute(id = intent.wallpaperId))
                ReducerResult(
                    newState = oldState,
                )
            }
        }
    }
}
