package dev.rakrae.gameengine.graphics.rendering.shaders

import dev.rakrae.gameengine.graphics.Vertex
import dev.rakrae.gameengine.graphics.rendering.pipeline.VertexShader
import dev.rakrae.gameengine.graphics.rendering.pipeline.VertexShaderInputs
import dev.rakrae.gameengine.graphics.rendering.pipeline.VertexShaderOutputs

class DefaultVertexShader : VertexShader {

    override fun process(vertex: Vertex, inputs: VertexShaderInputs): VertexShaderOutputs {
        // The default vertex shader does the minimum necessary operation, which is mapping from
        // object space to clip space.
        return VertexShaderOutputs(position = inputs.projection * inputs.modelView * vertex.position)
    }
}
