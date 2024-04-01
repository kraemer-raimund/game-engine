package dev.rakrae.gameengine.math

fun min(v1: Float, v2: Float): Float {
    return if (v2 < v1) v2 else v1
}

fun max(v1: Float, v2: Float): Float {
    return if (v2 > v1) v2 else v1
}

fun clamp(value: Float, minValue: Float, maxValue: Float): Float {
    return min(maxValue, max(minValue, value))
}
