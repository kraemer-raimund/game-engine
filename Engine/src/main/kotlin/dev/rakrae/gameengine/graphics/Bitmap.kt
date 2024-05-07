package dev.rakrae.gameengine.graphics

class Bitmap(val width: Int, val height: Int) {

    val pixels: IntArray = IntArray(width * height)

    fun setPixel(x: Int, y: Int, color: Color) {
        pixels[x + y * width] = color.asIntARGB.toInt()
    }

    private fun isInBounds(x: Int, y: Int): Boolean {
        return x in 0..<width && y in 0..<height
    }

    fun getPixel(x: Int, y: Int): Color {
        val intValue = pixels[x + y * width].toUInt()
        return Color.fromIntARGB(intValue)
    }

    fun clear(color: Color = Color(0u, 0u, 0u, 255u)) {
        for (i in 0 until width * height) {
            pixels[i] = color.asIntARGB.toInt()
        }
    }
}
