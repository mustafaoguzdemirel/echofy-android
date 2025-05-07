package com.mod.moodsong

import retrofit2.http.Body
import retrofit2.http.POST

interface GPTService {
    @POST("generate-songs")
    suspend fun generateSongs(@Body request: MoodRequest): SongResponse
}

data class MoodRequest(val mood: String)
