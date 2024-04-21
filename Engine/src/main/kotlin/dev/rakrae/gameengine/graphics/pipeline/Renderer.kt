package dev.rakrae.gameengine.graphics.pipeline

import dev.rakrae.gameengine.graphics.Bitmap
import dev.rakrae.gameengine.graphics.Rasterizer
import dev.rakrae.gameengine.scene.Scene
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Renderer {

    private val rasterizer = Rasterizer()

    suspend fun render(scene: Scene, bitmap: Bitmap) {
        withContext(Dispatchers.Default) {
            for (node in scene.nodes) {
                launch {
                    rasterizer.render(node, bitmap)
                }
            }
        }
    }
}
