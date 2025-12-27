package com.example.aura.di

import com.example.aura.data.local.WallpaperLocalDataSource
import com.example.aura.data.local.database.DatabaseDriverFactory
import com.example.aura.data.remote.PexelsRemoteDataSource
import com.example.aura.data.repository.WallpaperRepositoryImpl
import com.example.aura.data.util.TimeManagerImpl
import com.example.aura.database.AuraDatabase
import com.example.aura.domain.repository.WallpaperRepository
import com.example.aura.domain.util.TimeManager
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformModule: Module

fun sharedModule(apiKey: String) = module {
    includes(platformModule)

    // TimeManager
    single<TimeManager> { TimeManagerImpl() }

    // Database
    single {
        AuraDatabase(
            driver = get<DatabaseDriverFactory>().createDriver()
        )
    }

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

    singleOf(::PexelsRemoteDataSource)

    singleOf(::WallpaperLocalDataSource)
    singleOf(::WallpaperRepositoryImpl).bind<WallpaperRepository>()
}
