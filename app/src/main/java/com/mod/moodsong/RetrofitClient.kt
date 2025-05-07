package com.mod.moodsong

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://33079bfe-ffc9-4faa-a4c6-ae43166fe7e6-00-1xg6lxg4hz275.sisko.replit.dev/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: GPTService = retrofit.create(GPTService::class.java)
}
