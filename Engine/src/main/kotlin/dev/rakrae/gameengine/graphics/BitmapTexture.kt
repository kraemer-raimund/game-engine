package dev.rakrae.gameengine.graphics

import javax.imageio.ImageIO

sealed class Texture

class BitmapTexture(filePath: String) : Texture() {

    val bitmap = loadBitmap(filePath)

    private fun loadBitmap(filePath: String): Bitmap {
        val imageUrl = javaClass.getResource(filePath)
        val bufferedImage = ImageIO.read(imageUrl)
        val width = bufferedImage.width
        val height = bufferedImage.height
        val pixels = IntArray(width * height)
        bufferedImage.getRGB(0, 0, width, height, pixels, 0, width)
        val bitmap = Bitmap(width, height)
        for (i in pixels.indices) {
            bitmap.pixels[i] = pixels[i] and 0x00FFFFFF
        }
        return bitmap
    }
}

data class RenderTexture(val index: Int) : Texture() {

    init {
        if (index !in 0..15) {
            throw IndexOutOfBoundsException(
                "Render texture index mus be in the range ${allowedIndices.first} " +
                        "to ${allowedIndices.last} (exclusive)."
            )
        }
    }

    companion object {
        val allowedIndices = 0..15
    }
}
