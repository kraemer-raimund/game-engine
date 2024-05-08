package dev.rakrae.gameengine.graphics.rendering.pipeline

import dev.rakrae.gameengine.graphics.Triangle
import dev.rakrae.gameengine.math.Vec2f
import dev.rakrae.gameengine.math.Vec2i
import dev.rakrae.gameengine.math.Vec4f

internal class VertexPostProcessing {

    /**
     * Converts clip space coordinates to viewport coordinates. Returns zero, one or multiple triangles
     * due to culling, clipping, and potentially geometry generation (via tesselation/geometry shaders).
     */
    fun postProcess(triangleClipSpace: Triangle, viewportSize: Vec2i): List<Triangle> {
        return clip(triangleClipSpace)
            .map(::applyPerspectiveDivide)
            .filter(::isFrontFace)
            .map { viewportTransform(it, viewportSize) }
    }

    /**
     * Clip the triangle into multiple triangles if it intersects one or multiple of the view
     * frustums planes. If the triangle lies completely inside the view frustum, it remains as is.
     * If it lies completely outside the view frustum, it is culled (i.e., no triangles are
     * returned).
     *
     * @see <a href="https://en.wikipedia.org/wiki/Clipping_(computer_graphics)">https://en.wikipedia.org/wiki/Clipping_(computer_graphics)<a/>
     * @see <a href="https://en.wikipedia.org/wiki/Sutherland%E2%80%93Hodgman_algorithm">https://en.wikipedia.org/wiki/Sutherland%E2%80%93Hodgman_algorithm<a/>
     */
    private fun clip(triangleClipSpace: Triangle): List<Triangle> {
        // Frustum culling.
        if (isCompletelyOutsideViewFrustum(triangleClipSpace)) {
            return emptyList()
        }
        // Clipping not yet implemented. For now, we do a simple check against the viewing volume
        // and cull the triangle if necessary.
        return listOf(triangleClipSpace)
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

    /**
     * True if the polygon's front face is oriented towards the camera.
     * If back face culling is desired/enabled, the polygon will only be rendered if this is true.
     */
    private fun isFrontFace(triangleNormalizedDeviceCoordinates: Triangle): Boolean {
        val windingOrder = determineWindingOrder(triangleNormalizedDeviceCoordinates)
        return windingOrder == WindingOrder.CounterClockwise
    }

    /**
     * See section 14.6.1 of the OpenGL specification (as of version 4.6.)
     *
     * @see <a href="https://registry.khronos.org/OpenGL/specs/gl/glspec46.core.pdf">https://registry.khronos.org/OpenGL/specs/gl/glspec46.core.pdf</a>
     */
    private fun determineWindingOrder(triangleNormalizedDeviceCoordinates: Triangle): WindingOrder {
        val v0 = with(triangleNormalizedDeviceCoordinates.v0.position) { Vec2f(x, y) }
        val v1 = with(triangleNormalizedDeviceCoordinates.v1.position) { Vec2f(x, y) }
        val v2 = with(triangleNormalizedDeviceCoordinates.v2.position) { Vec2f(x, y) }

        val signedArea = 0.5f * (
                listOf(
                    (v0.x * v1.y) - (v1.x * v0.y),
                    (v1.x * v2.y) - (v2.x * v1.y),
                    (v2.x * v0.y) - (v0.x * v2.y)
                ).sum())

        return when {
            signedArea >= 0 -> WindingOrder.Clockwise
            else -> WindingOrder.CounterClockwise
        }
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
