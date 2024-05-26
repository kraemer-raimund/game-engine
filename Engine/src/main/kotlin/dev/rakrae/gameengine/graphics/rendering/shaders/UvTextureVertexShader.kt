package dev.rakrae.gameengine.graphics.rendering.shaders

import dev.rakrae.gameengine.graphics.rendering.pipeline.VertexShader
import dev.rakrae.gameengine.graphics.rendering.pipeline.VertexShaderInputs
import dev.rakrae.gameengine.graphics.rendering.pipeline.VertexShaderOutputs
import dev.rakrae.gameengine.math.Mat4x4f
import dev.rakrae.gameengine.math.Vec3f
import dev.rakrae.gameengine.math.Vec4f

class UvTextureVertexShader : VertexShader {

    override fun process(position: Vec3f, inputs: VertexShaderInputs): VertexShaderOutputs {
        return VertexShaderOutputs(
            position = inputs.projection * inputs.modelView * Vec4f(position, 1f),
            tbnMatrix = Mat4x4f.identity
        )
    }
}
