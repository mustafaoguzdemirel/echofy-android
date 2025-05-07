package com.mod.moodsong

import com.google.gson.annotations.SerializedName

data class RecommendationModel(
    @SerializedName("title") val title: String,
    @SerializedName("artist") val artist: String,
    @SerializedName("spotifyUrl") val spotifyUrl: String,
    @SerializedName("coverImageUrl") val coverImageUrl: String,
)
