package dev.rakrae.gameengine.graphics

import javax.imageio.ImageIO
import kotlin.math.pow

sealed class Texture

class BitmapTexture(filePath: String, convertToLinearSpace: Boolean = false) : Texture() {

    val bitmap = loadBitmap(filePath, convertToLinearSpace)

    private fun loadBitmap(filePath: String, convertToLinearSpace: Boolean): Bitmap {
        val imageUrl = javaClass.getResource(filePath)
        val bufferedImage = ImageIO.read(imageUrl)
        val width = bufferedImage.width
        val height = bufferedImage.height
        val pixels = IntArray(width * height)
        bufferedImage.getRGB(0, 0, width, height, pixels, 0, width)
        val bitmap = Bitmap(width, height)
        for (i in pixels.indices) {
            val color = Color.fromIntARGB(pixels[i].toUInt())
            bitmap.pixels[i] = if (convertToLinearSpace) {
                color.toLinearSpace().asIntARGB.toInt()
            } else {
                color.asIntARGB.toInt()
            }
        }
        return bitmap
    }

    /**
     * Convert the color from sRGB to linear space, so that we can do lighting calculations in
     * linear space and apply gamma correction in the end to the final frame.
     */
    private fun Color.toLinearSpace(): Color {
        val gamma = 2.2f
        val toLinearSpace = { colorComponent: UByte ->
            ((colorComponent.toInt() / 255f).pow(1f / gamma) * 255).toUInt().toUByte()
        }
        return Color(
            toLinearSpace(r),
            toLinearSpace(g),
            toLinearSpace(b),
            a
        )
    }
}

data class RenderTexture(val index: Int) : Texture() {

    init {
        if (index !in allowedIndices) {
            throw IndexOutOfBoundsException(
                "Render texture index mus be in the range ${allowedIndices.first} " +
                        "to ${allowedIndices.last} (exclusive)."
            )
        }
    }

    companion object {
        val allowedIndices = 0..3
    }
}
