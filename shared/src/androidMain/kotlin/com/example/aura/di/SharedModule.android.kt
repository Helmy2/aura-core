package com.example.aura.di

import com.example.aura.data.local.database.DatabaseDriverFactory
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single {
        DatabaseDriverFactory(
            androidContext()
        )
    }
}