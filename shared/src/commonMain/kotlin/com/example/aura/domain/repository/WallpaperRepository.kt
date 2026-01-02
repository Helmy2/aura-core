package com.example.aura.domain.repository

import com.example.aura.domain.model.Wallpaper

interface WallpaperRepository {
    suspend fun getCuratedWallpapers(page: Int = 1): List<Wallpaper>
    suspend fun searchWallpapers(query: String, page: Int = 1): List<Wallpaper>
    suspend fun getWallpaperById(id: Long): Wallpaper
}
