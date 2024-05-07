package dev.rakrae.gameengine.graphics.rendering.pipeline

import dev.rakrae.gameengine.graphics.Triangle
import dev.rakrae.gameengine.math.Vec2i
import dev.rakrae.gameengine.math.Vec3f
import dev.rakrae.gameengine.math.Vec4f
import kotlin.math.PI
import kotlin.math.acos

internal class VertexPostProcessing {

    /**
     * Converts clip space coordinates to viewport coordinates. Returns zero, one or multiple triangles
     * due to culling, clipping, and potentially geometry generation (via tesselation/geometry shaders).
     */
    fun postProcess(triangleClipSpace: Triangle, viewportSize: Vec2i): List<Triangle> {
        // Frustum culling.
        if (isCompletelyOutsideViewFrustum(triangleClipSpace)) {
            return emptyList()
        }

        val triangleNormalizedDeviceCoords = applyPerspectiveDivide(triangleClipSpace)

        // Back face culling.
        if (!isFrontFace(triangleNormalizedDeviceCoords)) {
            return emptyList()
        }

        val triangleViewportCoordinates = viewportTransform(triangleNormalizedDeviceCoords, viewportSize)
        return listOf(triangleViewportCoordinates)
    }

    private fun isCompletelyOutsideViewFrustum(triangleClipSpace: Triangle): Boolean {
        // View volume test in clip coordinates. In NDC it would be more intuitive (simply check
        // whether all 3 vertices of the triangle are within -1..1 in all 3 coordinates), but
        // it would project points from behind the camera in front of it (and invert their
        // coordinates, like a pinhole camera) due to perspective divide with negative w.
        // https://gamedev.stackexchange.com/a/158859/71768
        // https://stackoverflow.com/a/76094339/3726133
        // https://stackoverflow.com/a/31687061/3726133
        // https://gamedev.stackexchange.com/a/65798/71768
        val vertices = with(triangleClipSpace) { listOf(v0, v1, v2) }
        val vertexPositions = vertices.map { it.position }
        return vertexPositions.none { position ->
            position.x in -position.w..position.w
                    && position.y in -position.w..position.w
                    && position.z in -position.w..position.w
        }
    }

    /**
     * True if the polygon's front face is oriented towards the camera.
     * If back face culling is desired/enabled, the polygon will only be rendered if this is true.
     */
    private fun isFrontFace(triangleClipSpace: Triangle): Boolean {
        val n = triangleClipSpace.normal.normalized
        val view = Vec3f(0f, 0f, 1f)
        val angleRad = acos(view dot n)
        return angleRad < 0.5f * PI
    }

    private fun applyPerspectiveDivide(triangle: Triangle): Triangle {
        return Triangle(
            triangle.v0.copy(position = applyPerspectiveDivide(triangle.v0.position)),
            triangle.v1.copy(position = applyPerspectiveDivide(triangle.v1.position)),
            triangle.v2.copy(position = applyPerspectiveDivide(triangle.v2.position))
        )
    }

    private fun applyPerspectiveDivide(vector: Vec4f): Vec4f {
        return Vec4f(
            vector.x / vector.w,
            vector.y / vector.w,
            vector.z / vector.w,
            1f
        )
    }

    private fun viewportTransform(triangle: Triangle, screenSize: Vec2i): Triangle {
        return Triangle(
            triangle.v0.copy(position = viewportTransform(triangle.v0.position, screenSize)),
            triangle.v1.copy(position = viewportTransform(triangle.v1.position, screenSize)),
            triangle.v2.copy(position = viewportTransform(triangle.v2.position, screenSize))
        )
    }

    private fun viewportTransform(vector: Vec4f, screenSize: Vec2i): Vec4f {
        return Vec4f(
            0.5f * screenSize.x + (vector.x * 0.5f * screenSize.x),
            0.5f * screenSize.y + (vector.y * 0.5f * screenSize.y),
            vector.z,
            1f
        )
    }
}
