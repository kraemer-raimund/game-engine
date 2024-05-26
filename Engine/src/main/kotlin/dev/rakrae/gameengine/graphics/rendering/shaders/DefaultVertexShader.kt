package dev.rakrae.gameengine.graphics.rendering.shaders

import dev.rakrae.gameengine.graphics.rendering.pipeline.VertexShader
import dev.rakrae.gameengine.graphics.rendering.pipeline.VertexShaderInputs
import dev.rakrae.gameengine.graphics.rendering.pipeline.VertexShaderOutputs
import dev.rakrae.gameengine.math.Mat4x4f
import dev.rakrae.gameengine.math.Vec3f
import dev.rakrae.gameengine.math.Vec4f

class DefaultVertexShader : VertexShader {

    override fun process(position: Vec3f, inputs: VertexShaderInputs): VertexShaderOutputs {
        // The default vertex shader does the minimum necessary operation, which is mapping from
        // object space to clip space.
        return VertexShaderOutputs(
            position = inputs.projection * inputs.modelView * Vec4f(position, 1f),
            tbnMatrix = Mat4x4f.identity
        )
    }
}
