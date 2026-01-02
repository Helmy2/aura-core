package com.example.aura.feature.videos.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.aura.shared.component.AuraScaffold
import com.example.aura.shared.component.AuraSearchBar
import com.example.aura.shared.component.AuraTransparentTopBar
import com.example.aura.shared.component.VideoGallery
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun VideosScreen(
    viewModel: VideosViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val listState = rememberLazyStaggeredGridState()

    val shouldLoadMore by remember {
        derivedStateOf {
            val totalItems = listState.layoutInfo.totalItemsCount
            val lastVisibleIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            totalItems > 0 && lastVisibleIndex >= (totalItems - 4) && !state.isLoading && !state.isPaginationLoading && !state.isEndReached
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            viewModel.sendIntent(VideosIntent.LoadNextPage)
        }
    }

    AuraScaffold(
        topBar = {
            AuraTransparentTopBar(
                title = "Videos", onBackClick = {
                    viewModel.sendIntent(VideosIntent.OnNavigateBack)
                })
        },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (state.error != null) {
                Text(
                    text = "Error: ${state.error}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                VideoGallery(
                    contentPadding = padding,
                    videos = if (state.isSearchMode) state.searchVideos else state.popularVideos,
                    onVideoClick = { viewModel.sendIntent(VideosIntent.OnVideoClicked(it)) },
                    onFavoriteClick = { viewModel.sendIntent(VideosIntent.OnFavoriteClicked(it)) },
                    isLoading = state.isLoading,
                    isPaginationLoading = state.isPaginationLoading,
                    searchAppBar = {
                        AuraSearchBar(
                            query = state.searchQuery,
                            onQueryChange = {
                                viewModel.sendIntent(
                                    VideosIntent.OnSearchQueryChanged(
                                        it
                                    )
                                )
                            },
                            onSearch = { viewModel.sendIntent(VideosIntent.OnSearchTriggered) },
                            onClearSearch = { viewModel.sendIntent(VideosIntent.OnClearSearch) },
                            isSearchActive = state.isSearchMode,
                        )
                    },
                    emptyContent = {
                        if (state.isSearchMode) {
                            Text(text = "No results found")
                        } else {
                            Text(text = "No videos found")
                        }
                    })
            }
        }
    }
}