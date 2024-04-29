package dev.rakrae.gameengine.graphics

import dev.rakrae.gameengine.graphics.pipeline.FragmentShader
import dev.rakrae.gameengine.graphics.pipeline.InputFragment
import dev.rakrae.gameengine.math.*
import dev.rakrae.gameengine.scene.Node
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Rasterizer {

    suspend fun rasterize(
        node: Node,
        framebuffer: Bitmap,
        zBuffer: Buffer2f,
        fragmentShader: FragmentShader
    ) = withContext(Dispatchers.IO) {
        for (trianglesChunk in node.mesh.triangles.chunked(100)) {
            launch {
                for (triangle in trianglesChunk) {
                    rasterizeTriangle(triangle, framebuffer, zBuffer, fragmentShader)
                }
            }
        }
    }

    private fun rasterizeTriangle(
        triangle: Triangle,
        framebuffer: Bitmap,
        zBuffer: Buffer2f,
        fragmentShader: FragmentShader
    ) {
        val screenSize = Vec2i(framebuffer.width, framebuffer.height)
        val triangleInScreenCoordinates = projectToScreen(triangle, screenSize)
        val color = Color(255u, 255u, 255u, 255u)
        drawFilled(triangle, triangleInScreenCoordinates, color, framebuffer, zBuffer, fragmentShader)
    }

    private fun projectToScreen(triangle: Triangle, screenSize: Vec2i): Triangle2i {
        val screenCoordinates = arrayOf(triangle.v0, triangle.v1, triangle.v2)
            .map { projectToScreen(it.position.toVec3f(), screenSize) }
        return Triangle2i(
            screenCoordinates[0],
            screenCoordinates[1],
            screenCoordinates[2]
        )
    }

    private fun projectToScreen(worldPos: Vec3f, screenSize: Vec2i): Vec2i {
        val projectedX = (worldPos.x * screenSize.x / 6f).toInt()
        val projectedY = (worldPos.y * screenSize.y / 6f).toInt()
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
        zBuffer: Buffer2f,
        fragmentShader: FragmentShader
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
                        val inputFragment = InputFragment(
                            windowSpacePosition = Vec2i(x, y),
                            interpolatedVertexColor = color,
                            interpolatedNormal = interpolateNormal(trianglePolygon, barycentricCoordinates),
                            faceNormal = trianglePolygon.normal,
                            depth = interpolatedDepth
                        )
                        val outputFragment = fragmentShader.process(inputFragment)
                        image.setPixel(x, y, outputFragment.fragmentColor)
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

    private fun interpolateNormal(
        triangle: Triangle,
        barycentricCoordinates: BarycentricCoordinates
    ): Vec3f {
        val n1 = triangle.v0.normal
        val n2 = triangle.v1.normal
        val n3 = triangle.v2.normal
        val b = barycentricCoordinates
        return Vec3f(
            n1.x * b.a1 + n2.x * b.a2 + n3.x * b.a3,
            n1.y * b.a1 + n2.y * b.a2 + n3.y * b.a3,
            n1.z * b.a1 + n2.z * b.a2 + n3.z * b.a3
        )
    }

    private fun Bitmap.imageBounds(): AABB2i {
        return AABB2i(
            Vec2i(0, 0),
            Vec2i(this.width - 1, this.height - 1)
        )
    }
}
