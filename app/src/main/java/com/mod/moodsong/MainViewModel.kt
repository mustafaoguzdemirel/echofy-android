package com.mod.moodsong

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SongViewModel : ViewModel() {

    val rec1 = ObservableField<RecommendationModel>()
    val rec2 = ObservableField<RecommendationModel>()
    val rec3 = ObservableField<RecommendationModel>()
    val rec4 = ObservableField<RecommendationModel>()

    val enteredText = MutableLiveData<String>()

    val isLoading = ObservableBoolean()
    val isFound = ObservableBoolean()

    fun getSongs() {
        viewModelScope.launch {
            try {
                isLoading.set(true)
                val response =
                    RetrofitClient.api.generateSongs(MoodRequest(enteredText.value!!))
                if (response.success) {
                    val recList = response.data

                    rec1.set(recList[0])
                    rec2.set(recList[1])
                    rec3.set(recList[2])
                    rec4.set(recList[3])

                    isFound.set(true)
                }
                isLoading.set(false)
            } catch (e: Exception) {
                isLoading.set(false)
                e.printStackTrace()
            }
        }
    }
}
