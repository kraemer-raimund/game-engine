package dev.rakrae.gameengine.graphics.rendering.shaders

import dev.rakrae.gameengine.graphics.Bitmap
import dev.rakrae.gameengine.graphics.Buffer2f
import dev.rakrae.gameengine.graphics.Color
import dev.rakrae.gameengine.graphics.rendering.pipeline.DeferredShader
import dev.rakrae.gameengine.math.Vec2i

class OutlineDeferredShader(
    private val thickness: Int,
    private val threshold: Float,
    private val outlineColor: Color
) : DeferredShader {

    override fun postProcess(
        position: Vec2i,
        framebuffer: Bitmap,
        zBuffer: Buffer2f,
        deferredFramebuffer: Bitmap,
        deferredZBuffer: Buffer2f
    ): Color? {
        val depthOriginal = zBuffer.get(position.x, position.y)
        val depthDeferred = deferredZBuffer.get(position.x, position.y)
        if (depthDeferred > depthOriginal) {
            return null
        }
        if (depthDeferred == Float.POSITIVE_INFINITY) {
            return null
        }

        for (x in position.x - thickness..position.x + thickness) {
            for (y in position.y - thickness..position.y + thickness) {
                if (x in 0..<zBuffer.width && y in 0..<zBuffer.height) {
                    val depthAtNeighbor = zBuffer.get(x, y)
                    if (depthAtNeighbor - depthDeferred > threshold) {
                        return outlineColor
                    }
                }
            }
        }
        return framebuffer.getPixel(position.x, position.y)
    }
}
