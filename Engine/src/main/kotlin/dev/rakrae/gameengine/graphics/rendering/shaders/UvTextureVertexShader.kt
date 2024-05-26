package dev.rakrae.gameengine.graphics.rendering.shaders

import dev.rakrae.gameengine.graphics.Vertex
import dev.rakrae.gameengine.graphics.rendering.pipeline.VertexShader
import dev.rakrae.gameengine.graphics.rendering.pipeline.VertexShaderInputs
import dev.rakrae.gameengine.graphics.rendering.pipeline.VertexShaderOutputs
import dev.rakrae.gameengine.math.Mat4x4f

class UvTextureVertexShader : VertexShader {

    override fun process(vertex: Vertex, inputs: VertexShaderInputs): VertexShaderOutputs {
        return VertexShaderOutputs(
            position = inputs.projection * inputs.modelView * vertex.position,
            tbnMatrix = Mat4x4f.identity
        )
    }
}
