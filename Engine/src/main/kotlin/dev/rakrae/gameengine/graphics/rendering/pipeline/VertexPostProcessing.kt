package dev.rakrae.gameengine.graphics.rendering.pipeline

import dev.rakrae.gameengine.graphics.Triangle
import dev.rakrae.gameengine.math.*

internal class VertexPostProcessing {

    /**
     * Converts clip space coordinates to viewport coordinates. In the case of back-face culling,
     * no triangle will be returned.
     */
    fun toViewport(
        triangleClipSpace: Triangle,
        viewportMatrix: Mat4x4f
    ): Triangle? {
        return applyPerspectiveDivide(triangleClipSpace)
            .takeIf { isFrontFace(it) }
            ?.let { viewportTransform(it, viewportMatrix) }
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
    fun clip(
        triangleClipSpace: Triangle,
        triangleShaderVariables: TriangleShaderVariables,
        clippingPlanes: ClippingPlanes
    ): List<Pair<Triangle, TriangleShaderVariables>> {
        val triangle = triangleClipSpace
        val variables = triangleShaderVariables
        val nearPlane = clippingPlanes.near
        return when {
            isCompletelyInsideViewFrustum(triangle, nearPlane) -> listOf(Pair(triangle, variables))
            shouldCull(triangle) -> emptyList()
            else -> clipNear(triangle, variables, nearPlane)
        }
    }

    private fun shouldCull(triangleClipSpace: Triangle): Boolean {
        val vertexPositions = with(triangleClipSpace) { listOf(vertexPos0, vertexPos1, vertexPos2) }

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
        val vertexPositions = with(triangleClipSpace) { listOf(vertexPos0, vertexPos1, vertexPos2) }
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
    private fun clipNear(
        triangleClipSpace: Triangle,
        triangleShaderVariables: TriangleShaderVariables,
        nearClippingPlane: Float
    ): List<Pair<Triangle, TriangleShaderVariables>> {
        val (v0, v1, v2) = with(triangleClipSpace) { listOf(vertexPos0, vertexPos1, vertexPos2) }
        val (sv0, sv1, sv2) = triangleShaderVariables

        val isV0InFront = v0.w >= nearClippingPlane
        val isV1InFront = v1.w >= nearClippingPlane
        val isV2InFront = v2.w >= nearClippingPlane

        /*
        Imagine we go along the triangle's edges in counter-clockwise order, and when we cross
        the near clipping plane, we replace the vertex behind the near clipping plane with a
        new one right on the near clipping plane.
        Example: v1 is in front of the near plane, v0 and v2 are behind it. We now replace v0
        by interpolating between v1 and v0, replace v2 by doing the same with v1 and v2, and
        then we generate the new triangle using the new vertices newV0 and newV2, and the
        original vertex v1, but in the order (newV0, v1, newV2).
        The winding order is important so that back-face culling will work correctly for the
        newly generated triangles.
        */
        return when {
            isV0InFront && !isV1InFront && !isV2InFront -> {
                val (newV1, newSV1) = clipLine(v0, v1, sv0, sv1, nearClippingPlane)
                val (newV2, newSV2) = clipLine(v0, v2, sv0, sv2, nearClippingPlane)
                listOf(
                    Pair(
                        Triangle(v0, newV1, newV2),
                        TriangleShaderVariables(sv0, newSV1, newSV2)
                    )
                )
            }

            !isV0InFront && isV1InFront && !isV2InFront -> {
                val (newV0, newSV0) = clipLine(v1, v0, sv1, sv0, nearClippingPlane)
                val (newV2, newSV2) = clipLine(v1, v2, sv1, sv2, nearClippingPlane)
                listOf(
                    Pair(
                        Triangle(newV0, v1, newV2),
                        TriangleShaderVariables(newSV0, sv1, newSV2)
                    )
                )
            }

            !isV0InFront && !isV1InFront && isV2InFront -> {
                val (newV0, newSV0) = clipLine(v2, v0, sv2, sv0, nearClippingPlane)
                val (newV1, newSV1) = clipLine(v2, v1, sv2, sv1, nearClippingPlane)
                listOf(
                    Pair(
                        Triangle(newV0, newV1, v2),
                        TriangleShaderVariables(newSV0, newSV1, sv2)
                    )
                )
            }

            isV0InFront && isV1InFront && !isV2InFront -> {
                val (newV2, newSV2) = clipLine(v1, v2, sv1, sv2, nearClippingPlane)
                val (newV3, newSV3) = clipLine(v0, v2, sv0, sv2, nearClippingPlane)
                listOf(
                    Pair(
                        Triangle(v0, v1, newV2),
                        TriangleShaderVariables(sv0, sv1, newSV2)
                    ),
                    Pair(
                        Triangle(newV2, newV3, v0),
                        TriangleShaderVariables(newSV2, newSV3, sv0)
                    )
                )
            }

            isV0InFront && !isV1InFront && isV2InFront -> {
                val (newV1, newSV1) = clipLine(v0, v1, sv0, sv1, nearClippingPlane)
                val (newV3, newSV3) = clipLine(v2, v1, sv2, sv1, nearClippingPlane)
                listOf(
                    Pair(
                        Triangle(v0, newV1, v2),
                        TriangleShaderVariables(sv0, newSV1, sv2)
                    ),
                    Pair(
                        Triangle(v2, newV1, newV3),
                        TriangleShaderVariables(sv2, newSV1, newSV3)
                    )
                )
            }

            !isV0InFront && isV1InFront && isV2InFront -> {
                val (newV0, newSV0) = clipLine(v1, v0, sv1, sv0, nearClippingPlane)
                val (newV3, newSV3) = clipLine(v2, v0, sv2, sv0, nearClippingPlane)
                listOf(
                    Pair(
                        Triangle(newV0, v1, v2),
                        TriangleShaderVariables(newSV0, sv1, sv2)
                    ),
                    Pair(
                        Triangle(v2, newV3, newV0),
                        TriangleShaderVariables(sv2, newSV3, newSV0)
                    )
                )
            }

            isV0InFront && isV1InFront && isV2InFront -> {
                listOf(
                    Pair(
                        Triangle(v0, v1, v2),
                        TriangleShaderVariables(sv0, sv1, sv2)
                    )
                )
            }

            else -> {
                // All vertices behind the near clipping plane.
                emptyList()
            }
        }
    }

    private fun clipLine(
        vertexInFront: Vec4f,
        vertexBehind: Vec4f,
        shaderVariablesVertexInFront: ShaderVariables,
        shaderVariablesVertexBehind: ShaderVariables,
        nearClippingPlane: Float
    ): Pair<Vec4f, ShaderVariables> {
        val w0 = vertexInFront.w
        val w1 = vertexBehind.w
        val weight = (w0 - nearClippingPlane) / (w0 - w1)
        val clippedVertex = lerpPosition(vertexInFront, vertexBehind, weight, nearClippingPlane)
        val interpolatedShaderVariables = interpolateShaderVariables(
            shaderVariablesVertexInFront,
            shaderVariablesVertexBehind,
            weight
        )
        return Pair(clippedVertex, interpolatedShaderVariables)
    }

    private fun lerpPosition(
        vertexInFront: Vec4f,
        vertexBehind: Vec4f,
        weight: Float,
        nearClippingPlane: Float
    ): Vec4f {
        val clippedPos = Vec3f.lerp(vertexInFront.toVec3f(), vertexBehind.toVec3f(), weight)
        return Vec4f(clippedPos, w = nearClippingPlane)
    }

    private fun interpolateShaderVariables(
        shaderVariablesVertexInFront: ShaderVariables,
        shaderVariablesVertexBehind: ShaderVariables,
        weight: Float
    ): ShaderVariables {
        val interpolatedShaderVariables = ShaderVariables()
        shaderVariablesVertexInFront.floatKeys.forEach { key ->
            val v0 = shaderVariablesVertexInFront.getFloat(key).value
            val v1 = shaderVariablesVertexBehind.getFloat(key).value
            val vLerped = Math.lerp(v0, v1, weight)
            interpolatedShaderVariables.setFloat(
                key,
                ShaderVariables.FloatVariable(vLerped, shaderVariablesVertexInFront.getFloat(key).interpolation)
            )
        }
        shaderVariablesVertexInFront.vectorKeys.forEach { key ->
            val v0 = shaderVariablesVertexInFront.getVector(key).value
            val v1 = shaderVariablesVertexBehind.getVector(key).value
            val vLerped = Vec3f.lerp(v0, v1, weight)
            interpolatedShaderVariables.setVector(
                key,
                ShaderVariables.VectorVariable(vLerped, shaderVariablesVertexInFront.getVector(key).interpolation)
            )
        }
        return interpolatedShaderVariables
    }

    private fun applyPerspectiveDivide(triangle: Triangle): Triangle {
        return Triangle(
            applyPerspectiveDivide(triangle.vertexPos0),
            applyPerspectiveDivide(triangle.vertexPos1),
            applyPerspectiveDivide(triangle.vertexPos2)
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
     * [https://registry.khronos.org/OpenGL/specs/gl/glspec46.core.pdf](https://registry.khronos.org/OpenGL/specs/gl/glspec46.core.pdf)
     */
    private fun determineWindingOrder(triangleNormalizedDeviceCoordinates: Triangle): WindingOrder {
        val v0 = with(triangleNormalizedDeviceCoordinates.vertexPos0) { Vec2f(x, y) }
        val v1 = with(triangleNormalizedDeviceCoordinates.vertexPos1) { Vec2f(x, y) }
        val v2 = with(triangleNormalizedDeviceCoordinates.vertexPos2) { Vec2f(x, y) }

        val (x0, y0) = v0
        val (x1, y1) = v1
        val (x2, y2) = v2

        // https://en.wikipedia.org/wiki/Shoelace_formula#Other_formulas
        val signedArea = 0.5f * (x0 * (y1 - y2) + x1 * (y2 - y0) + x2 * (y0 - y1));

        return when {
            // Note: Normally these should be the other way around. A positive signed area
            // corresponds to counter-clockwise winding order, and a negative signed area
            // corresponds to clockwise winding. There is probably a mistake somewhere else
            // causing the winding order (or the signed area) to be flipped, and causing it
            // to be rendered correctly this way.
            // It's possible that the 3D models are using the wrong winding order, which
            // should be standardized by the asset importer to only use CCW internally,
            // but after a first look it seems like the 3D model assets are correct.
            // We'll accept it as is for now since the rendered result works as expected.
            signedArea >= 0 -> WindingOrder.Clockwise
            else -> WindingOrder.CounterClockwise
        }
    }

    private fun viewportTransform(
        triangle: Triangle,
        viewportMatrix: Mat4x4f
    ): Triangle {
        with(triangle) {
            return Triangle(
                viewportMatrix * vertexPos0,
                viewportMatrix * vertexPos1,
                viewportMatrix * vertexPos2
            )
        }
    }
}
