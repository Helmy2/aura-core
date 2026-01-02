package com.example.aura

import com.example.aura.domain.repository.FavoritesRepository
import com.example.aura.domain.repository.SettingsRepository
import com.example.aura.domain.repository.VideoRepository
import com.example.aura.domain.repository.WallpaperRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object DependenciesHelper : KoinComponent {
    val wallpaperRepository: WallpaperRepository by inject()
    val settingsRepository: SettingsRepository by inject()
    val videoRepository: VideoRepository by inject()
    val favoritesRepository: FavoritesRepository by inject()
}