package dev.rakrae.gameengine.graphics

import dev.rakrae.gameengine.math.Vec2f
import dev.rakrae.gameengine.math.abs
import dev.rakrae.gameengine.math.signum

class Rasterizer {

    fun render(mesh: Mesh, image: Bitmap) {
        for (triangle in mesh.triangles) {
            val vertices = listOf(triangle.v1, triangle.v2, triangle.v3)
            for (i in 0..2) {
                val lineStart = vertices[i].position
                val lineEnd = vertices[(i + 1) % 3].position
                drawLine(
                    from = Vec2f(
                        (lineStart.z + 1) * image.width / 8f,
                        (lineStart.y + 1) * image.height / 8f
                    ),
                    to = Vec2f(
                        (lineEnd.z + 1) * image.width / 8f,
                        (lineEnd.y + 1) * image.height / 8f
                    ),
                    image = image
                )
            }
        }
    }

    /**
     * Draw a line between two points into the image
     * using [Bresenham's line algorithm](https://en.wikipedia.org/wiki/Bresenham%27s_line_algorithm).
     */
    private fun drawLine(from: Vec2f, to: Vec2f, image: Bitmap) {
        val distanceX = abs(to.x - from.x)
        val signX = signum(to.x - from.x)
        val distanceY = -abs(to.y - from.y)
        val signY = signum(to.y - from.y)
        var error = distanceX + distanceY

        var x = from.x.toInt()
        var y = from.y.toInt()

        while (true) {
            image.setPixelIfInBounds(x, y, 0xDDDDDD)
            if (x == to.x.toInt() && y == to.y.toInt()) break
            // The threshold is at half a pixel. We can multiply everything by 2 to avoid the
            // expensive division since the sign of the accumulated error will remain the same.
            val errorTimesTwo = 2f * error
            if (errorTimesTwo >= distanceY) {
                if (x == to.x.toInt()) break
                error += distanceY
                x += signX.toInt()
            }
            if (errorTimesTwo <= distanceX) {
                if (y == to.y.toInt()) break
                error += distanceX
                y += signY.toInt()
            }
        }
    }
}
