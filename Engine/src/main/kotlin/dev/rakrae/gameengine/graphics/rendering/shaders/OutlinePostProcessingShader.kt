package dev.rakrae.gameengine.graphics.rendering.shaders

import dev.rakrae.gameengine.graphics.Bitmap
import dev.rakrae.gameengine.graphics.Buffer2f
import dev.rakrae.gameengine.graphics.Color
import dev.rakrae.gameengine.graphics.rendering.pipeline.PostProcessingShader
import dev.rakrae.gameengine.math.Vec2i
import kotlin.math.abs

class OutlinePostProcessingShader(
    val thickness: Int,
    val threshold: Float,
    val outlineColor: Color
) : PostProcessingShader {

    override fun postProcess(position: Vec2i, framebuffer: Bitmap, zBuffer: Buffer2f): Color {
        val depth = zBuffer.get(position.x, position.y)
        for (x in position.x - thickness..position.x + thickness) {
            for (y in position.y - thickness..position.y + thickness) {
                if (x in 0..<zBuffer.width && y in 0..<zBuffer.height) {
                    val depthAtNeighbor = zBuffer.get(x, y)
                    if (abs(depth - depthAtNeighbor) > threshold) {
                        return outlineColor
                    }
                }
            }
        }
        return framebuffer.getPixel(position.x, position.y)
    }
}
