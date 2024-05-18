package dev.rakrae.gameengine.graphics.rendering.pipeline

import dev.rakrae.gameengine.graphics.Triangle
import dev.rakrae.gameengine.graphics.Vertex
import dev.rakrae.gameengine.math.Vec2f
import dev.rakrae.gameengine.math.Vec2i
import dev.rakrae.gameengine.math.Vec3f
import dev.rakrae.gameengine.math.Vec4f

internal class VertexPostProcessing {

    /**
     * Converts clip space coordinates to viewport coordinates. Returns zero, one or multiple triangles
     * due to culling, clipping, and potentially geometry generation (via tesselation/geometry shaders).
     */
    fun postProcess(
        triangleClipSpace: Triangle,
        viewportSize: Vec2i,
        clippingPlanes: ClippingPlanes
    ): List<Triangle> {
        return clip(triangleClipSpace, clippingPlanes)
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
     * - [Clipping (Wikipedia)](https://en.wikipedia.org/wiki/Clipping_(computer_graphics))
     * - [Sutherland–Hodgman algorithm (Wikipedia)](https://en.wikipedia.org/wiki/Sutherland%E2%80%93Hodgman_algorithm)
     *
     * **Note:** View volume test (frustum culling) and clipping must happen in clip coordinates.
     * In NDC it would be more intuitive (simply check whether all 3 vertices of the triangle
     * are within -1..1 in all 3 coordinates), but it would project points from behind the
     * camera in front of it (and invert their coordinates, like a pinhole camera) due to
     * perspective divide with negative w.
     *
     * - [https://gamedev.stackexchange.com/a/158859/71768](https://gamedev.stackexchange.com/a/158859/71768)
     * - [https://stackoverflow.com/a/20180585/3726133](https://stackoverflow.com/a/20180585/3726133)
     * - [https://stackoverflow.com/a/76094339/3726133](https://stackoverflow.com/a/76094339/3726133)
     * - [https://stackoverflow.com/a/31687061/3726133](https://stackoverflow.com/a/31687061/3726133)
     * - [https://gamedev.stackexchange.com/a/65798/71768](https://gamedev.stackexchange.com/a/65798/71768)
     */
    private fun clip(triangleClipSpace: Triangle, clippingPlanes: ClippingPlanes): List<Triangle> {
        if (shouldCull(triangleClipSpace)) {
            return emptyList()
        }
        return clipNear(triangleClipSpace, clippingPlanes.near)
    }

    private fun shouldCull(triangleClipSpace: Triangle): Boolean {
        val vertices = with(triangleClipSpace) { listOf(v0, v1, v2) }
        val vertexPositions = vertices.map { it.position }

        // Note: If all vertices are outside the view frustum, its edges may still intersect the
        // view frustum (e.g., one vertex's x coordinate is outside the range, but another
        // vertex's y coordinate is outside the range). Therefore, we only cull a triangle if
        // all of its vertices are outside the view frustum relative to the same clip plane.
        return vertexPositions.all { pos -> pos.x < -pos.w }
                || vertexPositions.all { pos -> pos.x > pos.w }
                || vertexPositions.all { pos -> pos.y < -pos.w }
                || vertexPositions.all { pos -> pos.y > pos.w }
                || vertexPositions.all { pos -> pos.z < -pos.w }
                || vertexPositions.all { pos -> pos.z > pos.w }
    }

    /**
     * Simplified Sutherland-Hodgman algorithm clipping only at the near clipping plane.
     * Clipping at other planes may improve performance (although not necessarily), but at the
     * near clipping plane it is necessary in order to prevent spilling vertices from behind the
     * plane into visible coordinates during perspective divide.
     */
    private fun clipNear(triangleClipSpace: Triangle, nearClippingPlane: Float): List<Triangle> {
        val vertices = with(triangleClipSpace) { mutableListOf(v0, v1, v2) }
        val lines = (0..2).map { i -> Pair(vertices[i], vertices[(i + 1) % vertices.size]) }

        val clippedLines = lines.mapNotNull { line ->
            val (v0, v1) = line
            val isV0InFront = v0.position.w >= nearClippingPlane
            val isV1InFront = v1.position.w >= nearClippingPlane
            when {
                isV0InFront && isV1InFront -> line
                isV0InFront && !isV1InFront -> clipLine(line, nearClippingPlane)
                !isV0InFront && isV1InFront -> clipLine(Pair(v1, v0), nearClippingPlane)
                else -> null // Both vertices are behind the near clipping plane.
            }
        }

        val clippedVertices = clippedLines
            .flatMap { listOf(it.first, it.second) }
            .toSet()
            .toList()
        return assembleTriangles(clippedVertices)
    }

    private fun clipLine(line: Pair<Vertex, Vertex>, nearClippingPlane: Float): Pair<Vertex, Vertex> {
        val (vertexInFront, vertexBehind) = line
        val w0 = vertexInFront.position.w
        val w1 = vertexBehind.position.w
        val weight = (w0 - nearClippingPlane) / (w0 - w1)

        val clippedVertex = Vertex(
            position = lerpPosition(line, weight, nearClippingPlane),
            textureCoordinates = lerpUVs(line, weight),
            normal = lerpNormal(line, weight)
        )
        return Pair(clippedVertex, vertexInFront)
    }

    private fun lerpPosition(
        line: Pair<Vertex, Vertex>,
        weight: Float,
        nearClippingPlane: Float
    ): Vec4f {
        val (vertexInFront, vertexBehind) = line
        val posInFront = vertexInFront.position
        val posBehind = vertexBehind.position
        val clippedPos = Vec3f.lerp(posInFront.toVec3f(), posBehind.toVec3f(), weight)
        return Vec4f(clippedPos, w = nearClippingPlane)
    }

    private fun lerpUVs(line: Pair<Vertex, Vertex>, weight: Float): Vec3f {
        val (v0, v1) = line
        return Vec3f.lerp(v0.textureCoordinates, v1.textureCoordinates, weight)
    }

    private fun lerpNormal(line: Pair<Vertex, Vertex>, weight: Float): Vec3f {
        val (v0, v1) = line
        return Vec3f.lerp(v0.normal, v1.normal, weight)
    }

    private fun assembleTriangles(clippedVertices: List<Vertex>): List<Triangle> {
        clippedVertices.let {
            return when {
                clippedVertices.size < 3 -> emptyList()
                clippedVertices.size == 3 -> listOf(Triangle(it[0], it[1], it[2]))
                clippedVertices.size == 4 -> listOf(Triangle(it[0], it[1], it[2]), Triangle(it[2], it[1], it[3]))
                else -> throw UnsupportedOperationException(
                    "Clipping resulted in more than 4 unique vertices, but only up to 4 were expected."
                )
            }
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
