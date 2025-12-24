package com.example.aura.feature.detail

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.LocalPlatformContext
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.aura.core.extensions.shimmerEffect
import org.koin.compose.viewmodel.koinViewModel

@Suppress("ParamsComparedByRef")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    wallpaperId: Long, viewModel: DetailViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    DisposableEffect(Unit) {
        viewModel.sendIntent(DetailIntent.OnScreenOpened(wallpaperId))
        onDispose {

        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Details", color = Color.White
                    )
                }, navigationIcon = {
                    IconButton(onClick = {
                        viewModel.sendIntent(DetailIntent.OnBackCLicked)

                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }, colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalPlatformContext.current)
                    .data(state.wallpaper?.imageUrl).crossfade(true).build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                loading = {
                    Box(
                        modifier = Modifier.fillMaxSize().shimmerEffect()
                    )
                },
                error = {
                    AnimatedContent(
                        state.isLoading
                    ) {
                        if (it) Box(
                            modifier = Modifier.fillMaxSize().shimmerEffect()
                        )
                        else Box(
                            modifier = Modifier.fillMaxSize().background(Color.DarkGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Failed to load", color = Color.White)
                        }
                    }
                },
            )

            Box(
                modifier = Modifier.fillMaxWidth().height(120.dp).align(Alignment.TopCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.6f), Color.Transparent
                            )
                        )
                    )
            )

            Box(
                modifier = Modifier.fillMaxWidth().height(160.dp).align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent, Color.Black.copy(alpha = 0.8f)
                            )
                        )
                    )
            )

            AnimatedVisibility(state.wallpaper != null, modifier = Modifier.align(Alignment.BottomStart)) {
                Column(
                    modifier = Modifier.padding(16.dp)
                        .padding(bottom = padding.calculateBottomPadding())
                ) {
                    Text(
                        text = "Photo by",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Text(
                        text = state.wallpaper?.photographer ?: "Unknown",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White
                    )
                }
            }
        }
    }
}