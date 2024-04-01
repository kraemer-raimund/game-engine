package dev.rakrae.gameengine.math

import kotlin.math.max
import kotlin.math.min

fun min(v1: Float, v2: Float): Any {
    return if (v2 < v1) v2 else v1
}

fun max(v1: Float, v2: Float): Any {
    return if (v2 > v1) v2 else v1
}

fun clamp(value: Float, minValue: Float, maxValue:Float): Float {
    return min(maxValue, max(minValue, value))
}
