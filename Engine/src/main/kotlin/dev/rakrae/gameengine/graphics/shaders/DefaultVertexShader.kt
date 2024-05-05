package dev.rakrae.gameengine.graphics.shaders

import dev.rakrae.gameengine.graphics.pipeline.VertexShader
import dev.rakrae.gameengine.graphics.pipeline.VertexShaderInputs
import dev.rakrae.gameengine.math.Vec3f
import dev.rakrae.gameengine.math.Vec4f

class DefaultVertexShader : VertexShader {

    override fun process(position: Vec3f, inputs: VertexShaderInputs): Vec4f {
        // The default vertex shader does the minimum necessary operation, which is mapping from
        // object space to clip space.
        return inputs.projection * inputs.modelView * Vec4f(position, 1f)
    }
}
