package dev.rakrae.gameengine.samplegame.chess.shaders

import dev.rakrae.gameengine.core.GameTime
import dev.rakrae.gameengine.graphics.Vertex
import dev.rakrae.gameengine.graphics.rendering.pipeline.VertexShader
import dev.rakrae.gameengine.graphics.rendering.pipeline.VertexShaderInputs
import dev.rakrae.gameengine.graphics.rendering.pipeline.VertexShaderOutputs
import dev.rakrae.gameengine.math.Mat4x4f
import dev.rakrae.gameengine.math.Vec3f
import dev.rakrae.gameengine.math.Vec4f
import kotlin.math.*

/**
 * Used during development to get some visual feedback. Will be deleted in the future, and shaders
 * provided by the game will be used instead (or the engine's default shader).
 */
class DummyAnimationVertexShader : VertexShader {

    override fun process(vertex: Vertex, inputs: VertexShaderInputs): VertexShaderOutputs {
        val rotationMatrix = rotationMatrix(Vec3f(0f, GameTime.frameTime, 0f))
        val rotatedPos = rotationMatrix * vertex.position

        val normalWorldSpace = inputs.model * rotationMatrix * vertex.normal.toVec4()
        val tangentWorldSpace = inputs.model * rotationMatrix * vertex.tangent.toVec4()
        val bitangentWorldSpace = inputs.model * rotationMatrix * vertex.bitangent.toVec4()
        val tbnMatrix = Mat4x4f(
            tangentWorldSpace,
            bitangentWorldSpace,
            normalWorldSpace,
            Vec4f(0f, 0f, 0f, 1f)
        )
        // For an orthogonal matrix the transpose is equivalent to the inverse, but much faster.
        val tbnMatrixInv = tbnMatrix.transpose
        val lightDirTangentSpace = (tbnMatrixInv * inputs.lightDirWorldSpace.toVec4()).toVec3f()

        return VertexShaderOutputs(
            position = inputs.projection * inputs.modelView * rotatedPos,
            lightDirTangentSpace = lightDirTangentSpace
        )
    }

    private fun rotate(vector: Vec4f, euler: Vec3f, radians: Float): Vec4f {
        /*
        https://en.wikipedia.org/wiki/Atan2
        https://en.wikipedia.org/wiki/Polar_coordinate_system#Converting_between_polar_and_Cartesian_coordinates
        `atan2(y, x)` yields the angle measure in radians between the x-axis and the ray from (0, 0) to (x, y).
         */
        val newAngleRadians = atan2(vector.z, vector.x) + radians
        val distance = sqrt(vector.x.pow(2) + vector.z.pow(2))
        val x = distance * cos(newAngleRadians)
        val z = distance * sin(newAngleRadians)

        val rotationMatrix = rotationMatrix(Vec3f(x, vector.y, z))

        return Vec4f(x, vector.y, z, 1f)
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
