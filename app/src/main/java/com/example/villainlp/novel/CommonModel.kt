package com.example.villainlp.novel

import androidx.compose.ui.Modifier
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.SwipeableState

data class SwipeableParameters @OptIn(ExperimentalWearMaterialApi::class) constructor(
    val swipeableState: SwipeableState<Float>,
    val swipeableModifier: Modifier,
    val imageVisibility: Boolean,
)