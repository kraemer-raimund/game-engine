package dev.rakrae.gameengine.samplegame.chess.shaders

import dev.rakrae.gameengine.core.GameTime
import dev.rakrae.gameengine.graphics.Mesh
import dev.rakrae.gameengine.graphics.rendering.pipeline.*
import dev.rakrae.gameengine.math.Mat4x4f
import dev.rakrae.gameengine.math.Vec3f
import dev.rakrae.gameengine.math.Vec4f
import kotlin.math.cos
import kotlin.math.sin

/**
 * Used during development to get some visual feedback. Will be deleted in the future, and shaders
 * provided by the game will be used instead (or the engine's default shader).
 */
class DummyAnimationVertexShader : VertexShader {

    override fun process(vertex: Mesh.Vertex, inputs: VertexShaderInput): VertexShaderOutput {
        val rotationMatrix = rotationMatrix(Vec3f(0f, GameTime.frameTime, 0f))
        val rotatedPos = rotationMatrix * vertex.position
        val modelMatrix = inputs.shaderUniforms.getMatrix(ShaderUniforms.BuiltinKeys.MATRIX_M)
        val mvpMatrix = inputs.shaderUniforms.getMatrix(ShaderUniforms.BuiltinKeys.MATRIX_MVP)

        val normalWorldSpace = modelMatrix * rotationMatrix * vertex.normal.toVec4()
        val tangentWorldSpace = modelMatrix * rotationMatrix * vertex.tangent.toVec4()
        val bitangentWorldSpace = modelMatrix * rotationMatrix * vertex.bitangent.toVec4()
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
            position = mvpMatrix * rotatedPos,
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

    private fun rotationMatrix(eulerAxes: Vec3f): Mat4x4f {
        val rot = eulerAxes
        val rotX = Mat4x4f(
            1f, 0f, 0f, 0f,
            0f, cos(rot.x), -sin(rot.x), 0f,
            0f, sin(rot.x), cos(rot.x), 0f,
            0f, 0f, 0f, 1f
        )
        val rotY = Mat4x4f(
            cos(rot.y), 0f, sin(rot.y), 0f,
            0f, 1f, 0f, 0f,
            -sin(rot.y), 0f, cos(rot.y), 0f,
            0f, 0f, 0f, 1f
        )
        val rotZ = Mat4x4f(
            cos(rot.z), -sin(rot.z), 0f, 0f,
            sin(rot.z), cos(rot.z), 0f, 0f,
            0f, 0f, 1f, 0f,
            0f, 0f, 0f, 1f
        )
        return rotX * rotY * rotZ
    }
}
