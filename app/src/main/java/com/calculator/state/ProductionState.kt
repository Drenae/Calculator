package com.calculator.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class ProductionState {
    var paletteCount by mutableStateOf("")
    var quantityPerPalette by mutableStateOf("")
    var cycleTime by mutableStateOf("")
    var cavityCount by mutableIntStateOf(1)
    var alreadyProducedOnCurrentPalette by mutableStateOf("")
}
