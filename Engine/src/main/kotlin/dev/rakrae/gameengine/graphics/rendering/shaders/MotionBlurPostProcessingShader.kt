package dev.rakrae.gameengine.graphics.rendering.shaders

import dev.rakrae.gameengine.graphics.Bitmap
import dev.rakrae.gameengine.graphics.Buffer2f
import dev.rakrae.gameengine.graphics.Color
import dev.rakrae.gameengine.graphics.rendering.pipeline.PostProcessingShader
import dev.rakrae.gameengine.math.Vec2i

class MotionBlurPostProcessingShader : PostProcessingShader {

    override fun postProcess(position: Vec2i, framebuffer: Bitmap, zBuffer: Buffer2f): Color {
        val (x, y) = position
        return framebuffer.getPixel(x, y).copy(a = 128u)
    }
}
