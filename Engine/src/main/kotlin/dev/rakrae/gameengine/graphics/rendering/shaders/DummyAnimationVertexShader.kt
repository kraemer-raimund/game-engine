package dev.rakrae.gameengine.graphics.rendering.shaders

import dev.rakrae.gameengine.core.GameTime
import dev.rakrae.gameengine.graphics.rendering.pipeline.VertexShader
import dev.rakrae.gameengine.graphics.rendering.pipeline.VertexShaderInputs
import dev.rakrae.gameengine.math.Vec3f
import dev.rakrae.gameengine.math.Vec4f
import kotlin.math.*

/**
 * Used during development to get some visual feedback. Will be deleted in the future, and shaders
 * provided by the game will be used instead (or the engine's default shader).
 */
class DummyAnimationVertexShader : VertexShader {

    override fun process(position: Vec3f, inputs: VertexShaderInputs): Vec4f {
        val rotatedPos = rotate(position, GameTime.frameTime)
        return inputs.projection * inputs.modelView * Vec4f(rotatedPos, 1f)
    }

    private fun rotate(vector: Vec3f, radians: Float): Vec3f {
        /*
        https://en.wikipedia.org/wiki/Atan2
        https://en.wikipedia.org/wiki/Polar_coordinate_system#Converting_between_polar_and_Cartesian_coordinates
        `atan2(y, x)` yields the angle measure in radians between the x-axis and the ray from (0, 0) to (x, y).
         */
        val newAngleRadians = atan2(vector.z, vector.x) + radians
        val distance = sqrt(vector.x.pow(2) + vector.z.pow(2))
        val x = distance * cos(newAngleRadians)
        val z = distance * sin(newAngleRadians)

        return Vec3f(x, vector.y, z)
    }
}
