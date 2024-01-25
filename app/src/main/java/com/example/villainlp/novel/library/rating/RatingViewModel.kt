package com.example.villainlp.novel.library.rating

import androidx.lifecycle.ViewModel
import com.example.villainlp.server.FirebaseTools
import com.example.villainlp.server.FirebaseTools.updateBookRating
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RatingViewModel: ViewModel() {
    private val _ratingViewState = MutableStateFlow(
        RatingModel(0, 0, 0, 0, 0)
    )
    val ratingViewState: StateFlow<RatingModel> = _ratingViewState

    private val _artistryRate = MutableStateFlow(0)
    val artistryRate: StateFlow<Int> = _artistryRate

    private val _originalityRate = MutableStateFlow(0)
    val originalityRate: StateFlow<Int> = _originalityRate

    private val _commercialViabilityRate = MutableStateFlow(0)
    val commercialViabilityRate: StateFlow<Int> = _commercialViabilityRate

    private val _literaryMeritRate = MutableStateFlow(0)
    val literaryMeritRate: StateFlow<Int> = _literaryMeritRate

    private val _completenessRate = MutableStateFlow(0)
    val completenessRate: StateFlow<Int> = _completenessRate

    fun setArtistryRate(rate: Int){
        _artistryRate.value = rate
    }

    fun setOriginalityRate(rate: Int){
        _originalityRate.value = rate
    }

    fun setCommercialViabilityRate(rate: Int){
        _commercialViabilityRate.value = rate
    }

    fun setLiteraryMeritRate(rate: Int){
        _literaryMeritRate.value = rate
    }

    fun setCompletenessRate(rate: Int){
        _completenessRate.value = rate
    }

    suspend fun submitRating(documentId: String){
        val book = FirebaseTools.getLibraryBookFromFirestore(documentId)
        var averageRate = book[0].totalRate
        var starCount = book[0].starCount

        val currentState = ratingViewState.value.copy(
            artistryRate = artistryRate.value,
            originalityRate = originalityRate.value,
            commercialViabilityRate = commercialViabilityRate.value,
            literaryMeritRate = literaryMeritRate.value,
            completenessRate = completenessRate.value
        )
        averageRate += (currentState.artistryRate +
                currentState.originalityRate +
                currentState.commercialViabilityRate +
                currentState.literaryMeritRate +
                currentState.completenessRate) / 5.0f

        starCount += 1

        updateBookRating(documentId, averageRate, starCount)
    }
}

