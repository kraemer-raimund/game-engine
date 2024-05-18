package dev.rakrae.gameengine.graphics.rendering.pipeline

import dev.rakrae.gameengine.graphics.Triangle
import dev.rakrae.gameengine.graphics.Vertex
import dev.rakrae.gameengine.math.Vec2f
import dev.rakrae.gameengine.math.Vec2i
import dev.rakrae.gameengine.math.Vec3f
import dev.rakrae.gameengine.math.Vec4f

internal class VertexPostProcessing {

    /**
     * Converts clip space coordinates to viewport coordinates. In the case of back-face culling,
     * no triangle will be returned.
     */
    fun toViewport(triangleClipSpace: Triangle, viewportSize: Vec2i): Triangle? {
        return applyPerspectiveDivide(triangleClipSpace)
            .takeIf { isFrontFace(it) }
            ?.let { viewportTransform(it, viewportSize) }
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
    fun clip(triangleClipSpace: Triangle, clippingPlanes: ClippingPlanes): List<Triangle> {
        return when {
            isCompletelyInsideViewFrustum(triangleClipSpace, clippingPlanes.near) -> listOf(triangleClipSpace)
            shouldCull(triangleClipSpace) -> emptyList()
            else -> clipNear(triangleClipSpace, clippingPlanes.near)
        }
    }

    private fun shouldCull(triangleClipSpace: Triangle): Boolean {
        if (!isFrontFace(applyPerspectiveDivide(triangleClipSpace))) {
            return true
        }

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

    private fun isCompletelyInsideViewFrustum(triangleClipSpace: Triangle, nearClippingPlane: Float): Boolean {
        val vertices = with(triangleClipSpace) { listOf(v0, v1, v2) }
        val vertexPositions = vertices.map { it.position }
        return vertexPositions.all { pos ->
            listOf(pos.x, pos.y, pos.z)
                .all { it > -pos.w && it < pos.w } && pos.w >= nearClippingPlane
        }
    }

    /**
     * Simplified Sutherland-Hodgman algorithm clipping only at the near clipping plane.
     * Clipping at other planes may improve performance (although not necessarily), but at the
     * near clipping plane it is necessary in order to prevent spilling vertices from behind the
     * plane into visible coordinates during perspective divide.
     */
    private fun clipNear(triangleClipSpace: Triangle, nearClippingPlane: Float): List<Triangle> {
        val vertices = with(triangleClipSpace) { mutableListOf(v0, v1, v2) }
        val lines = listOf(
            Pair(vertices[0], vertices[1]),
            Pair(vertices[1], vertices[2]),
            Pair(vertices[2], vertices[0])
        )

        val originalVertices = linkedSetOf<Vertex>()
        val generatedVertices = linkedSetOf<Vertex>()
        lines.forEach { line ->
            val (v0, v1) = line
            val isV0InFront = v0.position.w >= nearClippingPlane
            val isV1InFront = v1.position.w >= nearClippingPlane
            when {
                isV0InFront && isV1InFront -> {
                    originalVertices.add(v0)
                    originalVertices.add(v1)
                }

                isV0InFront && !isV1InFront -> {
                    originalVertices.add(v0)
                    generatedVertices.add(clipLine(Pair(v0, v1), nearClippingPlane))
                }

                !isV0InFront && isV1InFront -> {
                    originalVertices.add(v1)
                    generatedVertices.add(clipLine(Pair(v1, v0), nearClippingPlane))
                }

                else -> {
                    // Both vertices are behind the near clipping plane.
                }
            }
        }

        return assembleTriangles(originalVertices, generatedVertices)
    }

    private fun clipLine(line: Pair<Vertex, Vertex>, nearClippingPlane: Float): Vertex {
        val (vertexInFront, vertexBehind) = line
        val w0 = vertexInFront.position.w
        val w1 = vertexBehind.position.w
        val weight = (w0 - nearClippingPlane) / (w0 - w1)

        val clippedVertex = Vertex(
            position = lerpPosition(line, weight, nearClippingPlane),
            textureCoordinates = lerpUVs(line, weight),
            normal = lerpNormal(line, weight)
        )
        return clippedVertex
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

    private fun assembleTriangles(
        originalVertices: LinkedHashSet<Vertex>,
        generatedVertices: LinkedHashSet<Vertex>
    ): List<Triangle> {
        val original = originalVertices.toList()
        val generated = generatedVertices.toList()
        return when {
            originalVertices.size == 3 && generatedVertices.isEmpty() -> {
                listOf(Triangle(original[0], original[1], original[2]))
            }

            originalVertices.size + generatedVertices.size < 3 -> {
                emptyList()
            }

            originalVertices.size == 1 && generatedVertices.size == 2 -> {
                listOf(Triangle(original[0], generated[0], generated[1]))
            }

            originalVertices.size == 2 && generatedVertices.size == 2 -> {
                listOf(
                    Triangle(original[0], generated[0], original[1]),
                    Triangle(generated[0], generated[1], original[1])
                )
            }

            else -> throw UnsupportedOperationException(
                "Expected 1 or 2 original vertices and exactly 2 newly generated ones. " +
                        "Actual: ${original.size} original vertices and ${generated.size} generated vertices."
            )
        }
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
        // We temporarily disable back-face culling until we get the winding order right
        // when generating triangles during clipping. This may hurt performance, but the
        // visual quality is better with near plane clipping but without back-face culling
        // than vice versa.
        return true

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
