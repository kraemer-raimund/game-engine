package dev.rakrae.gameengine.graphics.rendering.shaders

import dev.rakrae.gameengine.graphics.Bitmap
import dev.rakrae.gameengine.graphics.Buffer2f
import dev.rakrae.gameengine.graphics.Color
import dev.rakrae.gameengine.graphics.rendering.pipeline.PostProcessingShader
import dev.rakrae.gameengine.math.Vec2i
import kotlin.math.pow

class GammaCorrectionPostProcessingShader(private val gamma: Float = 2.2f) : PostProcessingShader {

    override fun postProcess(position: Vec2i, framebuffer: Bitmap, zBuffer: Buffer2f): Color {
        val (x, y) = position
        val color = framebuffer.getPixel(x, y)
        return Color(
            gammaCorrect(color.r),
            gammaCorrect(color.g),
            gammaCorrect(color.b),
            color.a
        )
    }

    private fun gammaCorrect(colorComponent: UByte): UByte {
        return ((colorComponent.toInt() / 255f).pow(gamma) * 255).toUInt().toUByte()
    }
}
