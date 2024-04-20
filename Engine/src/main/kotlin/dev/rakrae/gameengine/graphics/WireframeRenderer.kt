package dev.rakrae.gameengine.graphics

import dev.rakrae.gameengine.core.GameTime
import dev.rakrae.gameengine.math.Vec2i
import dev.rakrae.gameengine.scene.Node
import java.lang.Integer.signum
import kotlin.math.*

class WireframeRenderer {

    fun render(node: Node, image: Bitmap) {
        val screenSize = Vec2i(image.width, image.height)
        for (triangle in node.mesh.triangles) {
            val triangleInScreenCoordinates = projectToScreenCoordinates(triangle, screenSize)
            val triangleColor = Color.fromIntARGB(triangle.hashCode().toUInt())
            drawWireframe(triangleInScreenCoordinates, triangleColor, image)
        }
    }

    private fun projectToScreenCoordinates(triangle: Triangle, screenSize: Vec2i): List<Vec2i> {
        val vertices = listOf(triangle.v0, triangle.v1, triangle.v2)
        return vertices.map {
            /*
            https://en.wikipedia.org/wiki/Atan2
            https://en.wikipedia.org/wiki/Polar_coordinate_system#Converting_between_polar_and_Cartesian_coordinates
            `atan2(y, x)` yields the angle measure in radians between the x-axis and the ray from (0, 0) to (x, y).
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
