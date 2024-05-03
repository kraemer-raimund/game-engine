package dev.rakrae.gameengine.graphics

class Buffer2f(val width: Int, val height: Int, initValue: Float = 0.0f) {

    private val values: FloatArray = FloatArray(width * height) { initValue }

    fun get(x: Int, y: Int): Float {
        return values[x + y * width]
    }

    fun set(x: Int, y: Int, value: Float) {
        values[x + y * width] = value
    }

    fun clear(value: Float = 0f) {
        for (i in 0..<(width * height)) {
            values[i] = value
        }
    }
}
