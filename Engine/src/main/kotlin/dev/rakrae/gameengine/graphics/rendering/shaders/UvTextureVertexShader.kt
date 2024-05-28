package dev.rakrae.gameengine.graphics.rendering.shaders

import dev.rakrae.gameengine.graphics.Vertex
import dev.rakrae.gameengine.graphics.rendering.pipeline.ShaderVariables
import dev.rakrae.gameengine.graphics.rendering.pipeline.VertexShader
import dev.rakrae.gameengine.graphics.rendering.pipeline.VertexShaderInput
import dev.rakrae.gameengine.graphics.rendering.pipeline.VertexShaderOutput
import dev.rakrae.gameengine.math.Mat4x4f
import dev.rakrae.gameengine.math.Vec4f

class UvTextureVertexShader : VertexShader {

    override fun process(vertex: Vertex, inputs: VertexShaderInput): VertexShaderOutput {
        val normalWorldSpace = inputs.model * vertex.normal.toVec4()
        val tangentWorldSpace = inputs.model * vertex.tangent.toVec4()
        val bitangentWorldSpace = inputs.model * vertex.bitangent.toVec4()
        val tbnMatrix = Mat4x4f(
            tangentWorldSpace,
            bitangentWorldSpace,
            normalWorldSpace,
            Vec4f(0f, 0f, 0f, 1f)
        )
        // For an orthogonal matrix the transpose is equivalent to the inverse, but much faster.
        val tbnMatrixInv = tbnMatrix.transpose
        val lightDirTangentSpace = (tbnMatrixInv * inputs.lightDirWorldSpace.toVec4()).toVec3f()

        return VertexShaderOutput(
            position = inputs.projection * inputs.modelView * vertex.position,
            lightDirTangentSpace = lightDirTangentSpace,
            shaderVariables = ShaderVariables().apply {
                setVector(
                    "lightDirTangentSpace", ShaderVariables.VectorVariable(
                        lightDirTangentSpace,
                        ShaderVariables.Interpolation.LINEAR
                    )
                )
            }
        )
    }
}
