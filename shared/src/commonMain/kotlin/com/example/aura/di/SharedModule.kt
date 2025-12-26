package com.example.aura.di

import com.example.aura.data.remote.PexelsApi
import com.example.aura.data.repository.WallpaperRepositoryImpl
import com.example.aura.domain.repository.WallpaperRepository
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun sharedModule(apiKey: String) = module {
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    isLenient = true
                })
            }
            defaultRequest {
                header("Authorization", apiKey)
                url("https://api.pexels.com/v1/")
            }
        }
    }

    singleOf(::PexelsApi)

    singleOf(::WallpaperRepositoryImpl).bind<WallpaperRepository>()
}
