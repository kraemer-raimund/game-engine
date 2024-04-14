package dev.rakrae.gameengine.graphics

import dev.rakrae.gameengine.core.GameTime
import dev.rakrae.gameengine.math.Vec2i
import dev.rakrae.gameengine.math.abs
import dev.rakrae.gameengine.math.signum
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sqrt

class Rasterizer {

    fun render(mesh: Mesh, image: Bitmap) {
        val screenSize = Vec2i(image.width, image.height)
        for (triangle in mesh.triangles) {
            val triangleInScreenCoordinates = projectToScreenCoordinates(triangle, screenSize)
            val triangleColor = Color.from(triangle.hashCode().toUInt())
            drawFilled(triangleInScreenCoordinates, triangleColor, image)
        }
    }

    private fun projectToScreenCoordinates(triangle: Triangle, screenSize: Vec2i): List<Vec2i> {
        val vertices = listOf(triangle.v0, triangle.v1, triangle.v2)
        return vertices.map {
            /*
            https://en.wikipedia.org/wiki/Atan2
            https://en.wikipedia.org/wiki/Polar_coordinate_system#Converting_between_polar_and_Cartesian_coordinates
            `atan2(x, y)` yields the angle measure in radians between the x-axis and the ray from (0, 0) to (x, y).
             */
            val newAngleRadians = atan2(it.position.z, it.position.x) + GameTime.elapsedTime
            val distance = sqrt(it.position.x.pow(2) + it.position.z.pow(2))
            val x = distance * cos(newAngleRadians)
            Vec2i(
                ((x + 2.8f) * screenSize.x / 6f).toInt(),
                ((it.position.y + 1.5f) * screenSize.y / 6f).toInt()
            )
        }
    }

    private fun drawWireframe(triangleScreenCoordinates: List<Vec2i>, color: Color, image: Bitmap) {
        for (i in 0..2) {
            val lineStart = triangleScreenCoordinates[i]
            val lineEnd = triangleScreenCoordinates[(i + 1) % 3]
            drawLine(lineStart, lineEnd, color, image)
        }
    }

    private fun drawFilled(triangleScreenCoordinates: List<Vec2i>, color: Color, image: Bitmap) {
        // Line sweeping algorithm.

        // Sort vertices by y coordinate (i.e., vertically, lowest-to-highest).
        val pixels = triangleScreenCoordinates.sortedBy { it.y }
        val p0 = pixels[0]
        val p1 = pixels[1]
        val p2 = pixels[2]

        // Determine height of the triangle: Difference between the highest and lowest points.
        val triangleHeight = p2.y - p0.y

        // Split the triangle horizontally into 2 segments, where the vertex between the lowest
        // and the highest determines the splitting line.
        for (y in p0.y..p1.y) {
            val segmentHeight = p1.y - p0.y + 1

            /**
             * The ratio of the current y coordinate between the lowest and the highest points
             * of the whole triangle. We use this ratio to scale a vector along the **longer** side
             * of the triangle.
             */
            val a = (y - p0.y) / triangleHeight.toFloat()

            /**
             * The ratio of the current y coordinate between the lowest point of the triangle
             * and the "middle" corner on either side of the triangle. We use this ratio
             * to scale a vector along the **shorter** side of the triangle.
             */
            val b = (y - p0.y) / segmentHeight.toFloat()

            /**
             * The vector along the longer side of the triangle, pointing from the lowest corner
             * to the "current" y coordinate on the longer side, moving towards the top corner
             * of the triangle as we increment y.
             */
            val vecLong = p0 + Vec2i(
                ((p2 - p0).x * a).toInt(),
                ((p2 - p0).y * a).toInt()
            )

            /**
             * The vector along the shorter side of the triangle, pointing from the lowest corner
             * to the "current" y coordinate on the shorter side, moving towards the middle corner
             * of the triangle as we increment y.
             */
            val vecShort = p0 + Vec2i(
                ((p1 - p0).x * b).toInt(),
                ((p1 - p0).y * b).toInt()
            )

            drawLine(vecLong, vecShort, color, image)
        }

        // Do the same for the second half of the triangle.
        for (y in p1.y..p2.y) {
            val segmentHeight = p2.y - p1.y + 1

            /**
             * The ratio of the current y coordinate between the lowest and the highest points
             * of the whole triangle. We use this ratio to scale a vector along the **longer** side
             * of the triangle.
             */
            val a = (y - p0.y) / triangleHeight.toFloat()

            /**
             * The ratio of the current y coordinate between the middle corner of the triangle
             * and the highest point.
             */
            val b = (y - p1.y) / segmentHeight.toFloat()

            /**
             * The vector along the longer side of the triangle, pointing from the lowest corner
             * to the "current" y coordinate on the longer side, moving towards the top corner
             * of the triangle as we increment y.
             */
            val vecLong = p0 + Vec2i(
                ((p2 - p0).x * a).toInt(),
                ((p2 - p0).y * a).toInt()
            )

            /**
             * The vector along the shorter side of the triangle, pointing from the middle corner
             * to the "current" y coordinate on the shorter side, moving towards the highest corner
             * of the triangle as we increment y.
             */
            val vecShort = p1 + Vec2i(
                ((p2 - p1).x * b).toInt(),
                ((p2 - p1).y * b).toInt()
            )

            drawLine(vecLong, vecShort, color, image)
        }
    }

    /**
     * Draw a line between two points into the image
     * using [Bresenham's line algorithm](https://en.wikipedia.org/wiki/Bresenham%27s_line_algorithm).
     */
    private fun drawLine(from: Vec2i, to: Vec2i, color: Color, image: Bitmap) {
        val distanceX = abs(to.x - from.x)
        val signX = signum(to.x - from.x)
        val distanceY = -abs(to.y - from.y)
        val signY = signum(to.y - from.y)
        var error = distanceX + distanceY

        var x = from.x
        var y = from.y

        while (true) {
            image.setPixelIfInBounds(x, y, color)
            if (x == to.x && y == to.y) break
            // The threshold is at half a pixel. We can multiply everything by 2 to avoid the
            // expensive division since the sign of the accumulated error will remain the same.
            val errorTimesTwo = 2f * error
            if (errorTimesTwo >= distanceY) {
                if (x == to.x) break
                error += distanceY
                x += signX
            }
            if (errorTimesTwo <= distanceX) {
                if (y == to.y) break
                error += distanceX
                y += signY
            }
        }
    }
}
