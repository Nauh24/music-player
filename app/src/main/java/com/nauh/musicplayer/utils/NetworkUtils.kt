package com.nauh.musicplayer.utils

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

/**
 * Utility class for network operations
 */
object NetworkUtils {
    private const val TAG = "NetworkUtils"
    
    /**
     * Test if a URL is accessible
     */
    suspend fun testUrl(urlString: String): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Testing URL: $urlString")
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "HEAD"
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            connection.setRequestProperty("User-Agent", "MusicPlayer/1.0")
            
            val responseCode = connection.responseCode
            Log.d(TAG, "URL $urlString response code: $responseCode")
            
            connection.disconnect()
            responseCode in 200..299
        } catch (e: Exception) {
            Log.e(TAG, "Error testing URL $urlString: ${e.message}", e)
            false
        }
    }
    
    /**
     * Test multiple URLs and return the working ones
     */
    suspend fun testUrls(urls: List<String>): List<String> = withContext(Dispatchers.IO) {
        urls.filter { testUrl(it) }
    }
}
