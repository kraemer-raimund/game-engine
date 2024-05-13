package dev.rakrae.gameengine.graphics.rendering.shaders

import dev.rakrae.gameengine.graphics.Bitmap
import dev.rakrae.gameengine.graphics.Buffer2f
import dev.rakrae.gameengine.graphics.Color
import dev.rakrae.gameengine.graphics.rendering.pipeline.PostProcessingShader
import dev.rakrae.gameengine.math.Vec2i
import kotlin.math.abs
import kotlin.math.sqrt

class DepthOfFieldPostProcessingShader(
    private val effectStrength: Float = 1f,
) : PostProcessingShader {

    override fun postProcess(position: Vec2i, framebuffer: Bitmap, zBuffer: Buffer2f): Color {
        val (x, y) = position
        val depth = zBuffer.get(x, y)

        val depthInFocus = zBuffer.get(zBuffer.width / 2, zBuffer.height / 2).coerceIn(0f, 1f)
        val zBufferDifference = abs(depth - depthInFocus)
        // Constant 32 has been chosen experimentally.
        val resolutionIndependentBlurDistance = (framebuffer.height.toFloat() / 1080) * 16
        // The further away from the camera, the weaker the blur effect.
        val blur = (effectStrength * zBufferDifference * (1f - depth) * resolutionIndependentBlurDistance).toInt()

        // For performance reasons, we only let every n-th pixel influence the blurred pixel color.
        // This results in roughly 3 pixels per dimension influencing the final result.
        val step = (blur / 2).coerceAtLeast(1)
        var blurredColor = framebuffer.getPixel(x, y)

        for (ix in -blur..blur step step) {
            for (iy in -blur..blur step step) {
                if (x + ix in 0..<framebuffer.width && y + iy in 0..<framebuffer.height) {
                    val posX = x + ix
                    val posY = y + iy
                    val color = if (zBuffer.get(posX, posY) == Float.POSITIVE_INFINITY) {
                        Color(0u, 0u, 0u, 255u)
                    } else {
                        framebuffer.getPixel(posX, posY)
                    }
                    val distance = sqrt((ix * ix + iy * iy).toDouble()).toFloat()

                    blurredColor = weightedAverageOf(blurredColor, distance, color, 1f)
                }
            }
        }
        return blurredColor.copy(a = 255u)
    }

    private fun weightedAverageOf(c1: Color, weight1: Float, c2: Color, weight2: Float): Color {
        val r = (c1.r.toFloat() * weight1 + c2.r.toFloat() * weight2) / (weight1 + weight2)
        val g = (c1.g.toFloat() * weight1 + c2.g.toFloat() * weight2) / (weight1 + weight2)
        val b = (c1.b.toFloat() * weight1 + c2.b.toFloat() * weight2) / (weight1 + weight2)
        val a = (c1.a.toFloat() * weight1 + c2.a.toFloat() * weight2) / (weight1 + weight2)
        return Color(
            r.toInt().toUByte(),
            g.toInt().toUByte(),
            b.toInt().toUByte(),
            a.toInt().toUByte()
        )
    }

}
