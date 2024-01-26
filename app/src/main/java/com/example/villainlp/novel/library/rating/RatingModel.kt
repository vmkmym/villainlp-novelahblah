package com.example.villainlp.novel.library.rating

data class RatingModel(
    val artistryRate: Int,
    val originalityRate: Int,
    val commercialViabilityRate: Int,
    val literaryMeritRate: Int,
    val completenessRate: Int
)

enum class StarFactors(val factor: String){
    Artistry("작품성 (문장력과 구성력)"),
    Original("독창성 (창조성과 기발함)"),
    Commercial("상업성 (몰입도와 호소력)"),
    Literary("문학성 (철학과 감명)"),
    Complete("완성도 (문체의 가벼움과 진지함)")
}