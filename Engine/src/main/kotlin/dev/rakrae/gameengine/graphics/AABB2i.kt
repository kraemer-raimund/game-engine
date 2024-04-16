package dev.rakrae.gameengine.graphics

import dev.rakrae.gameengine.math.Vec2i

/**
 * An axis-aligned bounding box (AABB) in 2 dimensions with integer coordinates, as used for
 * example for the rectangle of pixels around a set of points on the screen.
 */
data class AABB2i(val min: Vec2i, val max: Vec2i) {

    companion object {
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
