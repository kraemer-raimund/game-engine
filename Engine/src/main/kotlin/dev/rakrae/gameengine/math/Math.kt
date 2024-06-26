package dev.rakrae.gameengine.math

object Math {

    fun lerp(v1: Float, v2: Float, t: Float): Float {
        t.coerceIn(0f..1f).let { weight ->
            return v1 * (1f - weight) + v2 * weight
        }
    }
}
