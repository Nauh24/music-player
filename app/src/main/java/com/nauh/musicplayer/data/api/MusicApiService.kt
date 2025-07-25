package com.nauh.musicplayer.data

import com.nauh.musicplayer.model.Song
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    
    @GET("songs")
    suspend fun getSongs(): Response<List<Song>>
}
