package dev.rakrae.gameengine.graphics

import dev.rakrae.gameengine.graphics.pipeline.DefaultFragmentShader
import dev.rakrae.gameengine.graphics.pipeline.DefaultVertexShader
import dev.rakrae.gameengine.graphics.pipeline.FragmentShader
import dev.rakrae.gameengine.graphics.pipeline.VertexShader

class Material(
    val vertexShader: VertexShader,
    val fragmentShader: FragmentShader,
    val color: Color = Color(255u, 255u, 255u, 255u)
) {

    companion object {
        val default = Material(
            DefaultVertexShader(),
            DefaultFragmentShader(),
            Color(255u, 255u, 255u, 255u)
        )
    }
}
