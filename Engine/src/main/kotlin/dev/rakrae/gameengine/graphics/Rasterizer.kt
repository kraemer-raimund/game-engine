package dev.rakrae.gameengine.graphics

import dev.rakrae.gameengine.math.Vec2f
import dev.rakrae.gameengine.math.abs
import dev.rakrae.gameengine.math.signum

internal class Rasterizer {

    fun render(image: Bitmap) {
        drawLine(Vec2f(13f, 37f), Vec2f(150f, 550f), image)
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
            image.pixels[x + y * image.width] = 0xDDD
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
