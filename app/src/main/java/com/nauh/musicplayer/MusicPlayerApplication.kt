package com.nauh.musicplayer

import android.app.Application

/**
 * Application class for global initialization
 * Handles app-wide configuration and dependency injection setup
 */
class MusicPlayerApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize any global components here
        // For example: Crash reporting, Analytics, etc.
    }
}
