package com.mod.moodsong

import com.google.gson.annotations.SerializedName

data class SongResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("songs") val data: List<RecommendationModel>
)
