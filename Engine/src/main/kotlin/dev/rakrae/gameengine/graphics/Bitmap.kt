package dev.rakrae.gameengine.graphics

class Bitmap(val width: Int, val height: Int) {

    val pixels: IntArray = IntArray(width * height)

    fun setPixelIfInBounds(x: Int, y: Int, color: Color) {
        val pixelIndex = x + y * width
        if (pixelIndex in 0..<pixels.lastIndex) {
            pixels[pixelIndex] = color.intValue.toInt()
        }
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
