package dev.rakrae.gameengine.graphics

import dev.rakrae.gameengine.math.Vec2f
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt

internal class TextureSampler(
    private val filter: Filter,
    private val offset: Vec2f = Vec2f(0f, 0f),
    private val scale: Vec2f = Vec2f(1f, 1f)
) {

    fun sample(texture: Bitmap, uv: Vec2f): Color {
        val u = offset.x + scale.x * uv.x
        val v = offset.y + scale.y * uv.y
        val x = u * (texture.width - 1)
        val y = v * (texture.height - 1)
        return when (filter) {
            Filter.LINEAR -> filterBilinear(x, y, texture)
            else -> filterNearest(x, y, texture)
        }
    }

    private fun filterNearest(
        x: Float,
        y: Float,
        texture: Bitmap
    ): Color {
        val xInterpolated = x.roundToInt().mod(texture.width - 1)
        val yInterpolated = y.roundToInt().mod(texture.height - 1)
        return texture.getPixel(xInterpolated, yInterpolated).copy(a = 255u)
    }

    private fun filterBilinear(
        x: Float,
        y: Float,
        texture: Bitmap
    ): Color {
        val xLow = floor(x).toInt()
        val xHigh = ceil(x).toInt()
        val yLow = floor(y).toInt()
        val yHigh = ceil(y).toInt()

        val colorBottomLeft = texture.getPixel(
            xLow.mod(texture.width - 1),
            yLow.mod(texture.height - 1)
        )
        val colorBottomRight = texture.getPixel(
            xHigh.mod(texture.width - 1),
            yLow.mod(texture.height - 1)
        )
        val colorTopLeft = texture.getPixel(
            xLow.mod(texture.width - 1),
            yHigh.mod(texture.height - 1)
        )
        val colorTopRight = texture.getPixel(
            xHigh.mod(texture.width - 1),
            yHigh.mod(texture.height - 1)
        )

        val weightX = x - xLow
        val weightY = y - yLow
        val biLerpedColor = Color.lerp(
            Color.lerp(colorBottomLeft, colorBottomRight, weightX),
            Color.lerp(colorTopLeft, colorTopRight, weightX),
            weightY
        )
        return biLerpedColor
    }

    enum class Filter {

        /**
         * Rounds the UV coordinate to the nearest texel within the texture.
         */
        NEAREST,

        /**
         * Linearly interpolates between the texels surrounding the current UV coordinates, using
         * the distance from each of those texels as the interpolation weights. For 2D textures,
         * the closest 4 texels are interpolated bilinearly.
         */
        LINEAR
    }
}
