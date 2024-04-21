package dev.rakrae.gameengine.graphics.pipeline

import dev.rakrae.gameengine.graphics.Bitmap
import dev.rakrae.gameengine.graphics.Rasterizer
import dev.rakrae.gameengine.scene.Node

class Renderer {

    private val rasterizer = Rasterizer()

    suspend fun render(node: Node, bitmap: Bitmap) {
        rasterizer.render(node, bitmap)
    }
}
