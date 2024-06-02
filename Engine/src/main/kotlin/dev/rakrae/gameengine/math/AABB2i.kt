package dev.rakrae.gameengine.math

import dev.rakrae.gameengine.graphics.Triangle
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * An axis-aligned bounding box (AABB) in 2 dimensions with integer coordinates, as used for
 * example for the rectangle of pixels around a set of points on the screen.
 */
data class AABB2i(val min: Vec2i, val max: Vec2i) {

    fun clampWithin(bounds: AABB2i): AABB2i {
        return AABB2i(
            min = Vec2i(
                x = clamp(this.min.x, bounds.min.x, bounds.max.x),
                y = clamp(this.min.y, bounds.min.y, bounds.max.y)
            ),
            max = Vec2i(
                x = clamp(this.max.x, bounds.min.x, bounds.max.x),
                y = clamp(this.max.y, bounds.min.y, bounds.max.y)
            )
        )
    }

    private fun clamp(value: Int, min: Int, max: Int): Int {
        return min(max, max(value, min))
    }

    companion object {
        fun calculateBoundingBox(triangle: Triangle): AABB2i {
            val points = listOf(triangle.vertexPos0, triangle.vertexPos1, triangle.vertexPos2)
                .map { Vec2i(it.x.roundToInt(), it.y.roundToInt()) }
            return calculateBoundingBox(points)
        }

        fun calculateBoundingBox(points: Iterable<Vec2i>): AABB2i {
            val xCoordinates = points.map { it.x }
            val yCoordinates = points.map { it.y }

            val minX = xCoordinates.minOrNull() ?: 0
            val maxX = xCoordinates.maxOrNull() ?: 0
            val minY = yCoordinates.minOrNull() ?: 0
            val maxY = yCoordinates.maxOrNull() ?: 0

            return AABB2i(
                min = Vec2i(minX, minY),
                max = Vec2i(maxX, maxY)
            )
        }
    }
}
