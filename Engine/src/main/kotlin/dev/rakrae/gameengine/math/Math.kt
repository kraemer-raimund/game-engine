package dev.rakrae.gameengine.math

import kotlin.math.max
import kotlin.math.min

fun clamp(value: Float, minValue: Float, maxValue:Float): Float {
    return min(maxValue, max(minValue, value))
}
