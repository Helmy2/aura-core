package com.example.aura.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

// Todo add the api key
fun initKoin(apiKey: String = "" , appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(sharedModule(apiKey))
}