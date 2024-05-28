package dev.rakrae.gameengine.graphics.rendering.shaders

import dev.rakrae.gameengine.graphics.Vertex
import dev.rakrae.gameengine.graphics.rendering.pipeline.VertexShader
import dev.rakrae.gameengine.graphics.rendering.pipeline.VertexShaderInput
import dev.rakrae.gameengine.graphics.rendering.pipeline.VertexShaderOutput

class DefaultVertexShader : VertexShader {

    override fun process(vertex: Vertex, inputs: VertexShaderInput): VertexShaderOutput {
        // The default vertex shader does the minimum necessary operation, which is mapping from
        // object space to clip space.
        return VertexShaderOutput(position = inputs.projection * inputs.modelView * vertex.position)
    }
}
