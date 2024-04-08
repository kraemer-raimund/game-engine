package dev.rakrae.gameengine.graphics

import dev.rakrae.gameengine.math.Vec2i
import dev.rakrae.gameengine.math.abs
import dev.rakrae.gameengine.math.signum

class Rasterizer {

    fun render(mesh: Mesh, image: Bitmap) {
        for (triangle in mesh.triangles) {
            drawWireframe(triangle, image)
        }
    }

    private fun drawWireframe(triangle: Triangle, image: Bitmap) {
        val vertices = listOf(triangle.v1, triangle.v2, triangle.v3)
        for (i in 0..2) {
            val lineStartWorld = vertices[i].position
            val lineEndWorld = vertices[(i + 1) % 3].position
            val lineStartScreen = Vec2i(
                ((lineStartWorld.z + 1) * image.width / 6f).toInt(),
                ((lineStartWorld.y + 1) * image.height / 6f).toInt()
            )
            val lineEndScreen = Vec2i(
                ((lineEndWorld.z + 1) * image.width / 6f).toInt(),
                ((lineEndWorld.y + 1) * image.height / 6f).toInt()
            )
            drawLine(lineStartScreen, lineEndScreen, image)
        }
    }

    private fun drawFilled(triangle: List<Vec2i>, image: Bitmap) {
        TODO("Not completely implemented, but not called yet.")
        // Line sweeping algorithm.

        // Sort vertices by y coordinate (i.e., vertically, lowest-to-highest).
        val pixels = triangle.sortedBy { it.y }

        // Determine height of the triangle: Difference between the highest and lowest points.
        val triangleHeight = pixels[2].y - pixels[0].y

        // Split the triangle horizontally into 2 segments, where the vertex between the lowest
        // and the highest determines the splitting line.
        val splittingHeight = pixels[1].y
        for (y in pixels[0].y..<splittingHeight) {

        }
    }

    /**
     * Draw a line between two points into the image
     * using [Bresenham's line algorithm](https://en.wikipedia.org/wiki/Bresenham%27s_line_algorithm).
     */
    private fun drawLine(from: Vec2i, to: Vec2i, image: Bitmap) {
        val distanceX = abs(to.x - from.x)
        val signX = signum(to.x - from.x)
        val distanceY = -abs(to.y - from.y)
        val signY = signum(to.y - from.y)
        var error = distanceX + distanceY

        var x = from.x
        var y = from.y

        while (true) {
            image.setPixelIfInBounds(x, y, 0xDDDDDD)
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
