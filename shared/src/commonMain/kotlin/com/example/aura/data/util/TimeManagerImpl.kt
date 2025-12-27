package com.example.aura.data.util

import com.example.aura.domain.util.TimeManager
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class TimeManagerImpl : TimeManager {
    @OptIn(ExperimentalTime::class)
    override fun currentTimeMillis(): Long {
        return Clock.System.now().toEpochMilliseconds()
    }
}
