package dev.rakrae.gameengine.graphics

import dev.rakrae.gameengine.graphics.pipeline.DefaultFragmentShader
import dev.rakrae.gameengine.graphics.pipeline.DefaultVertexShader
import dev.rakrae.gameengine.graphics.pipeline.FragmentShader
import dev.rakrae.gameengine.graphics.pipeline.VertexShader

class Material(
    val vertexShader: VertexShader,
    val fragmentShader: FragmentShader
) {

    companion object {
        val default = Material(DefaultVertexShader(), DefaultFragmentShader())
    }
}
