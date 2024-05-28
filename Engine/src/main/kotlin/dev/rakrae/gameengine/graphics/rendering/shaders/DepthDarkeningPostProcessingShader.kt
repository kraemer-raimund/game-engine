package dev.rakrae.gameengine.graphics.rendering.shaders

import dev.rakrae.gameengine.graphics.Bitmap
import dev.rakrae.gameengine.graphics.Buffer2f
import dev.rakrae.gameengine.graphics.Color
import dev.rakrae.gameengine.graphics.rendering.pipeline.PostProcessingShader
import dev.rakrae.gameengine.math.Vec2i
import kotlin.math.pow

class DepthDarkeningPostProcessingShader : PostProcessingShader {

    override fun postProcess(position: Vec2i, framebuffer: Bitmap, zBuffer: Buffer2f): Color {
        val (x, y) = position
        val color = framebuffer.getPixel(x, y)
        // We want to darken objects that are far away from the camera, and brighten those
        // close to the camera.
        val zBufferDarkening = 4 * (1f - zBuffer.get(x, y)).pow(2)
        return color * zBufferDarkening.coerceIn(0f, 1f)
    }

    private operator fun Color.times(value: Float): Color {
        return Color(
            (value * r.toInt()).toInt().toUByte(),
            (value * g.toInt()).toInt().toUByte(),
            (value * b.toInt()).toInt().toUByte(),
            255u
        )
    }
}
