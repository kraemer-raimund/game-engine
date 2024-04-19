package dev.rakrae.gameengine.graphics

class Bitmap(val width: Int, val height: Int) {

    val pixels: IntArray = IntArray(width * height)

    fun setPixel(x: Int, y: Int, color: Color) {
        pixels[x + y * width] = color.intValue.toInt()
    }

    fun setPixelIfInBounds(x: Int, y: Int, color: Color) {
        if (!isInBounds(x, y)) return
        pixels[x + y * width] = color.intValue.toInt()
    }

    private fun isInBounds(x: Int, y: Int): Boolean {
        return x in 0..<width && y in 0..<height
    }

    fun getPixel(x: Int, y: Int): Color {
        val intValue = pixels[x + y * width].toUInt()
        return Color.from(intValue)
    }

    fun clear() {
        for (i in 0 until width * height) {
            pixels[i] = 0
        }
    }
}
