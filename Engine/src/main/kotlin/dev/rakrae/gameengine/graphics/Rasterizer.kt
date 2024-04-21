package dev.rakrae.gameengine.graphics

import dev.rakrae.gameengine.math.*
import dev.rakrae.gameengine.scene.Node
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Rasterizer {

    suspend fun render(node: Node, image: Bitmap) = withContext(Dispatchers.IO) {
        val screenSize = Vec2i(image.width, image.height)
        val zBuffer = Buffer2f(image.width, image.height)

        for (trianglesChunk in node.mesh.triangles.chunked(100)) {
            launch {
                for (triangle in trianglesChunk) {
                    renderTriangle(triangle, node.position, screenSize, image, zBuffer)
                }
            }
        }
    }

    private fun renderTriangle(
        triangle: Triangle,
        positionOffset: Vec3f,
        screenSize: Vec2i,
        image: Bitmap,
        zBuffer: Buffer2f
    ) {
        val lightDirection = Vec3f(0.2f, 0f, 0.6f).normalized
        val normal = triangle.normal
        val lightIntensity = normal.normalized dot lightDirection
        val triangleInScreenCoordinates = projectToScreen(triangle, positionOffset, screenSize)
        val color = Color(
            (lightIntensity * 255).toInt().toUByte(),
            (lightIntensity * 255).toInt().toUByte(),
            (lightIntensity * 255).toInt().toUByte(),
            255u
        )
        drawFilled(triangle, triangleInScreenCoordinates, color, image, zBuffer)
    }

    private fun projectToScreen(triangle: Triangle, offset: Vec3f, screenSize: Vec2i): Triangle2i {
        val screenCoordinates = arrayOf(triangle.v0, triangle.v1, triangle.v2)
            .map { projectToScreen(it.position.toVec3f(), offset, screenSize) }
        return Triangle2i(
            screenCoordinates[0],
            screenCoordinates[1],
            screenCoordinates[2]
        )
    }

    private fun projectToScreen(worldPos: Vec3f, offset: Vec3f, screenSize: Vec2i): Vec2i {
        val projectedX = ((worldPos.x + offset.x + 1.8f) * screenSize.x / 6f).toInt()
        val projectedY = ((worldPos.y + 1.5f) * screenSize.y / 6f).toInt()
        return Vec2i(projectedX, projectedY)
    }

    /**
     * For each point within the triangle's AABB, fill the point if it lies within the triangle.
     */
    private fun drawFilled(
        trianglePolygon: Triangle,
        triangleScreen: Triangle2i,
        color: Color,
        image: Bitmap,
        zBuffer: Buffer2f
    ) {
        val boundingBox = AABB2i
            .calculateBoundingBox(triangleScreen)
            .clampWithin(image.imageBounds())

        for (x in boundingBox.min.x..<boundingBox.max.x) {
            for (y in boundingBox.min.y..<boundingBox.max.y) {
                val barycentricCoordinates = BarycentricCoordinates.of(Vec2i(x, y), triangleScreen)
                if (barycentricCoordinates.isWithinTriangle) {
                    val interpolatedDepth = interpolateDepth(trianglePolygon, barycentricCoordinates)
                    if (interpolatedDepth < zBuffer.get(x, y)) {
                        zBuffer.set(x, y, interpolatedDepth)
                        image.setPixel(x, y, color)
                    }
                }
            }
        }
    }

    private fun interpolateDepth(
        triangle: Triangle,
        barycentricCoordinates: BarycentricCoordinates
    ): Float {
        val z1 = triangle.v0.position.toVec3f().z
        val z2 = triangle.v1.position.toVec3f().z
        val z3 = triangle.v2.position.toVec3f().z
        val b = barycentricCoordinates
        val interpolatedZ = z1 * b.a1 + z2 * b.a2 + z3 * b.a3

        // Temporary offset since we are using world coordinates for the depth instead of relative
        // to the camera.
        return interpolatedZ - 100f
    }

    private fun Bitmap.imageBounds(): AABB2i {
        return AABB2i(
            Vec2i(0, 0),
            Vec2i(this.width - 1, this.height - 1)
        )
    }
}
