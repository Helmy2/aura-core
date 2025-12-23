package com.example.aura.feature.home

import androidx.lifecycle.viewModelScope
import com.example.aura.core.mvi.BaseViewModel
import com.example.aura.core.mvi.ReducerResult
import com.example.aura.domain.repository.WallpaperRepository
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: WallpaperRepository
) : BaseViewModel<HomeState, HomeIntent, Nothing>(
    initialState = HomeState()
) {

    init {
        // Trigger initial load
        sendIntent(HomeIntent.LoadCuratedWallpapers)
    }

    override fun reduce(
        oldState: HomeState,
        intent: HomeIntent
    ): ReducerResult<HomeState, Nothing> {
        return when (intent) {
            is HomeIntent.LoadCuratedWallpapers -> {
                // Side Effect: Fetch data asynchronously
                viewModelScope.launch {
                    try {
                        val wallpapers = repository.getCuratedWallpapers()
                        sendIntent(HomeIntent.OnWallpapersLoaded(wallpapers))
                    } catch (e: Exception) {
                        sendIntent(HomeIntent.OnError(e.message ?: "Unknown error"))
                    }
                }
                // Update State: Show Loading
                ReducerResult(oldState.copy(isLoading = true, error = null))
            }

            is HomeIntent.OnWallpapersLoaded -> {
                // Update State: Show Data, Hide Loading
                ReducerResult(oldState.copy(isLoading = false, wallpapers = intent.wallpapers))
            }

            is HomeIntent.OnError -> {
                // Update State: Show Error, Hide Loading
                ReducerResult(oldState.copy(isLoading = false, error = intent.message))
            }

            is HomeIntent.OnWallpaperClicked -> {
                // No state change, just a side effect (Navigation)
                // ToDo: Handle navigation here
                ReducerResult(
                    newState = oldState,
                )
            }
        }
    }
}
