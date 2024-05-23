package dev.rakrae.gameengine.graphics

import dev.rakrae.gameengine.math.Vec2f
import kotlin.math.roundToInt

internal class TextureSampler(filter: Filter) {

    fun sample(bitmap: Bitmap, uv: Vec2f): Color {
        val x = uv.x * bitmap.width
        val y = uv.y * bitmap.height
        val xInterpolated = x.roundToInt().mod(bitmap.width).coerceIn(0, bitmap.width - 1)
        val yInterpolated = y.roundToInt().mod(bitmap.height).coerceIn(0, bitmap.height - 1)
        return bitmap.getPixel(xInterpolated, yInterpolated)
    }

    enum class Filter {

        /**
         * Rounds the UV coordinate to the nearest texel in the texture.
         */
        NEAREST
    }
}
