package com.example.aura.di

import com.example.aura.domain.repository.WallpaperRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class KoinHelper : KoinComponent {
    val wallpaperRepository: WallpaperRepository by inject()
}

fun doInitKoin() {
    initKoin()
}