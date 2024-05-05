package dev.rakrae.gameengine.graphics.rendering.pipeline

import dev.rakrae.gameengine.graphics.Bitmap
import dev.rakrae.gameengine.graphics.Buffer2f
import dev.rakrae.gameengine.math.Vec2i
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

internal class DeferredRendering {

    suspend fun postProcess(
        deferredShader: DeferredShader,
        framebuffer: Bitmap,
        zBuffer: Buffer2f,
        deferredFramebuffer: Bitmap,
        deferredZBuffer: Buffer2f
    ) = coroutineScope {

        for (x in 0..<framebuffer.width) {
            launch {
                for (y in (0..<framebuffer.height)) {
                    val color = deferredShader.postProcess(
                        Vec2i(x, y),
                        framebuffer,
                        zBuffer,
                        deferredFramebuffer,
                        deferredZBuffer
                    )
                    if (color != null) {
                        framebuffer.setPixel(x, y, color)
                    }
                }
            }
        }
    }
}
