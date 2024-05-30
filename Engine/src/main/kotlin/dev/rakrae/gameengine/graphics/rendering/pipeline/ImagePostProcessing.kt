package dev.rakrae.gameengine.graphics.rendering.pipeline

import dev.rakrae.gameengine.graphics.Bitmap
import dev.rakrae.gameengine.graphics.Buffer2f
import dev.rakrae.gameengine.math.Vec2i

internal class ImagePostProcessing {

    fun postProcess(
        postProcessingShader: PostProcessingShader,
        framebuffer: Bitmap,
        zBuffer: Buffer2f
    ) {
        val postProcessingBuffer = Bitmap(framebuffer.width, framebuffer.height)

        for (x in 0..<postProcessingBuffer.width) {
            for (y in (0..<postProcessingBuffer.height)) {
                val color = postProcessingShader.postProcess(Vec2i(x, y), framebuffer, zBuffer)
                    ?: framebuffer.getPixel(x, y)
                postProcessingBuffer.setPixel(x, y, color)
            }
        }

        for (x in 0..<framebuffer.width) {
            for (y in 0..<framebuffer.height) {
                framebuffer.setPixel(x, y, postProcessingBuffer.getPixel(x, y))
            }
        }
    }
}
