package dev.rakrae.gameengine.graphics

import dev.rakrae.gameengine.core.GameTime
import dev.rakrae.gameengine.math.BarycentricCoordinates
import dev.rakrae.gameengine.math.Triangle2i
import dev.rakrae.gameengine.math.Vec2i
import dev.rakrae.gameengine.math.Vec3f
import dev.rakrae.gameengine.scene.Node
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sqrt

class Rasterizer {

    fun render(node: Node, image: Bitmap) {
        val screenSize = Vec2i(image.width, image.height)
        for (triangle in node.mesh.triangles) {
            val triangleInScreenCoordinates = projectToScreenCoordinates(triangle, node.position, screenSize)
            val triangleColor = Color.from(triangle.hashCode().toUInt())
            drawFilled(triangleInScreenCoordinates, triangleColor, image)
        }
    }

    private fun projectToScreenCoordinates(triangle: Triangle, offset: Vec3f, screenSize: Vec2i): List<Vec2i> {
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
                ((x + offset.x + 1.8f) * screenSize.x / 6f).toInt(),
                ((it.position.y + 1.5f) * screenSize.y / 6f).toInt()
            )
        }
    }

    /**
     * For each point within the triangle's AABB, fill the point if it lies within the triangle.
     */
    private fun drawFilled(triangleScreenCoordinates: List<Vec2i>, color: Color, image: Bitmap) {
        val boundingBox = AABB2i
            .calculateBoundingBox(triangleScreenCoordinates)
            .clampWithin(image.imageBounds())

        for (x in boundingBox.min.x..<boundingBox.max.x) {
            for (y in boundingBox.min.y..<boundingBox.max.y) {
                if (liesWithinTriangle(Vec2i(x, y), triangleScreenCoordinates)) {
                    image.setPixel(x, y, color)
                }
            }
        }
    }

    private fun liesWithinTriangle(point: Vec2i, triangleScreenCoordinates: List<Vec2i>): Boolean {
        val barycentricCoordinates = BarycentricCoordinates.of(
            point,
            Triangle2i(
                triangleScreenCoordinates[0],
                triangleScreenCoordinates[1],
                triangleScreenCoordinates[2]
            )
        )
        return barycentricCoordinates.isWithinTriangle
    }

    private fun Bitmap.imageBounds(): AABB2i {
        return AABB2i(
            Vec2i(0, 0),
            Vec2i(this.width - 1, this.height - 1)
        )
    }
}
