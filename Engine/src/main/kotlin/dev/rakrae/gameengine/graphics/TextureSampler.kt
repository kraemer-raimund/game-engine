package dev.rakrae.gameengine.graphics

import dev.rakrae.gameengine.math.Vec2f
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt

internal class TextureSampler(private val filter: Filter) {

    fun sample(texture: Bitmap, uv: Vec2f): Color {
        val x = uv.x * (texture.width - 1)
        val y = uv.y * (texture.height - 1)
        return when (filter) {
            Filter.LINEAR -> interpolateBilinear(x, y, texture)
            else -> interpolateNearest(x, y, texture)
        }
    }

    private fun interpolateNearest(
        x: Float,
        y: Float,
        texture: Bitmap
    ): Color {
        val xInterpolated = x.roundToInt().mod(texture.width - 1)
        val yInterpolated = y.roundToInt().mod(texture.height - 1)
        return texture.getPixel(xInterpolated, yInterpolated)
    }

    private fun interpolateBilinear(
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
        val biLerpedColor = lerp(
            lerp(colorBottomLeft, colorBottomRight, weightX),
            lerp(colorTopLeft, colorTopRight, weightX),
            weightY
        )
        return biLerpedColor
    }

    private fun lerp(color1: Color, color2: Color, t: Float): Color {
        val r = (1 - t) * color1.r.toInt() + t * color2.r.toInt()
        val g = (1 - t) * color1.g.toInt() + t * color2.g.toInt()
        val b = (1 - t) * color1.b.toInt() + t * color2.b.toInt()
        return Color(
            (r.toInt().coerceIn(0, 255)).toUByte(),
            (g.toInt().coerceIn(0, 255)).toUByte(),
            (b.toInt().coerceIn(0, 255)).toUByte(),
            255u
        )
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
