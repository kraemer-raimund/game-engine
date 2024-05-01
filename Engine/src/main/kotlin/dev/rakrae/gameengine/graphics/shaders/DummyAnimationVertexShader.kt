package dev.rakrae.gameengine.graphics.shaders

import dev.rakrae.gameengine.core.GameTime
import dev.rakrae.gameengine.graphics.Vertex
import dev.rakrae.gameengine.graphics.pipeline.VertexShader
import dev.rakrae.gameengine.math.Vec4f
import kotlin.math.*

/**
 * Used during development to get some visual feedback. Will be deleted in the future, and shaders
 * provided by the game will be used instead (or the engine's default shader).
 */
class DummyAnimationVertexShader : VertexShader {

    override fun process(vertex: Vertex): Vertex {
        val rotatedPos = rotate(vertex.position, GameTime.tickTime)
        return Vertex(rotatedPos, vertex.textureCoordinates, vertex.normal)
    }

    private fun rotate(vector: Vec4f, radians: Float): Vec4f {
        /*
        https://en.wikipedia.org/wiki/Atan2
        https://en.wikipedia.org/wiki/Polar_coordinate_system#Converting_between_polar_and_Cartesian_coordinates
        `atan2(y, x)` yields the angle measure in radians between the x-axis and the ray from (0, 0) to (x, y).
         */
        val newAngleRadians = atan2(vector.z, vector.x) + radians
        val distance = sqrt(vector.x.pow(2) + vector.z.pow(2))
        val x = distance * cos(newAngleRadians)
        val z = distance * sin(newAngleRadians)

        return Vec4f(
            x,
            vector.y,
            z,
            vector.w
        )
    }
}
