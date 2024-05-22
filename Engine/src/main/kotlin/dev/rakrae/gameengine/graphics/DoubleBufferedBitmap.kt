package dev.rakrae.gameengine.graphics

/**
 * Behaves similar to a bitmap, but with separate read and write buffers. This prevents situations
 * where, e.g., one camera reads from a texture that is only partially rendered/loaded/cleared, etc.
 * Similarly, it can prevent race conditions in case of multi-threading, as long as the buffer swap
 * is synchronized.
 *
 * The *front buffer* is the one used for reading. The *back buffer* is used for writing.
 * The *buffer swap* makes the pixels previously written to the back buffer available for any
 * reading threads/cameras/etc.
 */
class DoubleBufferedBitmap(
    val width: Int,
    val height: Int,
    initialColor: Color = Color.black
) {

    private var frontBuffer = Bitmap(width, height, initialColor)
    private var backBuffer = Bitmap(width, height, initialColor)

    /**
     * Write a pixel to the back buffer.
     */
    fun writePixel(x: Int, y: Int, color: Color) {
        backBuffer.setPixel(x, y, color)
    }

    /**
     * Read a pixel from the front buffer.
     */
    fun readPixel(x: Int, y: Int): Color {
        return frontBuffer.getPixel(x, y)
    }

    /**
     * Swap front and back buffer.
     */
    fun swap() {
        frontBuffer = backBuffer.also { backBuffer = frontBuffer }
    }

    fun clearBackBuffer(color: Color = Color.black) {
        backBuffer.clear(color)
    }
}
