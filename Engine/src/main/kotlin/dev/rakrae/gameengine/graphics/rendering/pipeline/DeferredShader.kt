package dev.rakrae.gameengine.graphics.rendering.pipeline

import dev.rakrae.gameengine.graphics.Bitmap
import dev.rakrae.gameengine.graphics.Buffer2f
import dev.rakrae.gameengine.graphics.Color
import dev.rakrae.gameengine.math.Vec2i

interface DeferredShader {

    fun postProcess(
        position: Vec2i,
        framebuffer: Bitmap,
        zBuffer: Buffer2f,
        deferredFramebuffer: Bitmap,
        deferredZBuffer: Buffer2f
    ): Color?
}
