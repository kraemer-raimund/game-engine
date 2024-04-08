package dev.rakrae.gameengine.math

fun abs(v: Int): Int {
    return if (v < 0) -v else v
}

fun abs(v: Float): Float {
    return if (v < 0) -v else v
}

fun signum(v: Float): Float {
    return when {
        v == 0f -> 0f
        v.isNaN() -> Float.NaN
        v > 0 -> 1f
        else -> -1f
    }
}

fun signum(v: Int): Int {
    return when {
        v == 0 -> 0
        v > 0 -> 1
        else -> -1
    }
}

fun min(v1: Float, v2: Float): Float {
    return if (v2 < v1) v2 else v1
}

fun max(v1: Float, v2: Float): Float {
    return if (v2 > v1) v2 else v1
}

fun clamp(value: Float, minValue: Float, maxValue: Float): Float {
    return min(maxValue, max(minValue, value))
}
