package dev.rakrae.gameengine.graphics

import dev.rakrae.gameengine.core.GameTime
import dev.rakrae.gameengine.math.*
import dev.rakrae.gameengine.scene.Node
import kotlin.math.*

class Rasterizer {

    fun render(node: Node, image: Bitmap) {
        val screenSize = Vec2i(image.width, image.height)
        val elapsedTime = GameTime.elapsedTime

        // Very rough approximation of painter's algorithm.
        val trianglesSortedByDepth = node.mesh.triangles
            .sortedBy { rotate(it.v0.position.toVec3f(), elapsedTime).z }

        for (triangle in trianglesSortedByDepth) {
            val lightDirection = rotate(Vec3f(0.2f, 0f, 0.6f).normalized, elapsedTime * -1f)
            val normal = triangle.normal
            val lightIntensity = normal.normalized dot lightDirection
            val triangleInScreenCoordinates = projectToScreenCoordinates(triangle, node.position, screenSize)
            val color = Color(
                (lightIntensity * 255).toInt().toUByte(),
                (lightIntensity * 255).toInt().toUByte(),
                (lightIntensity * 255).toInt().toUByte(),
                255u
            )
            drawFilled(triangleInScreenCoordinates, color, image)
        }
    }

    private fun projectToScreenCoordinates(triangle: Triangle, offset: Vec3f, screenSize: Vec2i): Triangle2i {
        val screenCoordinates = arrayOf(triangle.v0, triangle.v1, triangle.v2)
            .map {
                val rotated = rotate(it.position.toVec3f(), GameTime.elapsedTime)
                Vec2i(
                    ((rotated.x + offset.x + 1.8f) * screenSize.x / 6f).toInt(),
                    ((it.position.y + 1.5f) * screenSize.y / 6f).toInt()
                )
            }
        return Triangle2i(
            screenCoordinates[0],
            screenCoordinates[1],
            screenCoordinates[2]
        )
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

    /**
     * For each point within the triangle's AABB, fill the point if it lies within the triangle.
     */
    private fun drawFilled(triangle: Triangle2i, color: Color, image: Bitmap) {
        val boundingBox = AABB2i
            .calculateBoundingBox(triangle)
            .clampWithin(image.imageBounds())

        for (x in boundingBox.min.x..<boundingBox.max.x) {
            for (y in boundingBox.min.y..<boundingBox.max.y) {
                val barycentricCoordinates = BarycentricCoordinates.of(Vec2i(x, y), triangle)
                if (barycentricCoordinates.isWithinTriangle) {
                    image.setPixel(x, y, color)
                }
            }
        }
    }

    private fun Bitmap.imageBounds(): AABB2i {
        return AABB2i(
            Vec2i(0, 0),
            Vec2i(this.width - 1, this.height - 1)
        )
    }
}
