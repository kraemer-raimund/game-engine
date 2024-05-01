package dev.rakrae.gameengine.graphics.shaders

import dev.rakrae.gameengine.graphics.Vertex
import dev.rakrae.gameengine.graphics.pipeline.VertexShader

class DefaultVertexShader : VertexShader {

    override fun process(vertex: Vertex): Vertex {
        // Simple pass through in the default implementation.
        // User defined shaders can handle this differently.
        return vertex
    }
}
