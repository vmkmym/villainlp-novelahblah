package com.villainlp.novlahvlah.novel.library.rating

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RatingViewModel : ViewModel() {
    private val ratingModel = RatingModel()

    private val _ratingViewState = MutableStateFlow(RatingOption(0, 0, 0, 0, 0))
    val ratingViewState: StateFlow<RatingOption> = _ratingViewState

    private val _artistryRate = MutableStateFlow(0)
    private val artistryRate: StateFlow<Int> = _artistryRate

    private val _originalityRate = MutableStateFlow(0)
    private val originalityRate: StateFlow<Int> = _originalityRate

    private val _commercialViabilityRate = MutableStateFlow(0)
    private val commercialViabilityRate: StateFlow<Int> = _commercialViabilityRate

    private val _literaryMeritRate = MutableStateFlow(0)
    private val literaryMeritRate: StateFlow<Int> = _literaryMeritRate

    private val _completenessRate = MutableStateFlow(0)
    private val completenessRate: StateFlow<Int> = _completenessRate

    fun setRatingByQuestion(question: String, rate: Int){
        when (question) {
            StarFactors.Artistry.factor -> _artistryRate.value = rate
            StarFactors.Original.factor -> _originalityRate.value = rate
            StarFactors.Commercial.factor -> _commercialViabilityRate.value = rate
            StarFactors.Literary.factor -> _literaryMeritRate.value = rate
            StarFactors.Complete.factor -> _completenessRate.value = rate
        }
    }

    fun submitRating(documentId: String) {
        viewModelScope.launch {
            val book = ratingModel.getBook(documentId)
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

            ratingModel.updateRating(documentId, averageRate, starCount)
        }
    }
}

