package com.calculator.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

fun productionAccent(index: Int): Color = when (index % 3) {
    0 -> GreenAccent
    1 -> PurpleAccent
    else -> OrangeAccent
}

fun textColorFor(background: Color): Color =
    if (background.luminance() > 0.45f) TextOnLight else Color.White
