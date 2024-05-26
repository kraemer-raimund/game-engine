package dev.rakrae.gameengine.assets

import dev.rakrae.gameengine.graphics.Mesh
import dev.rakrae.gameengine.graphics.Triangle
import dev.rakrae.gameengine.math.Vec3f

internal object MeshTangentSpaceBaking {

    fun bakeTangentSpace(mesh: Mesh): Mesh {
        return Mesh(mesh.triangles.map(::bakeTangentSpace))
    }

    /**
     * The tangent and bitangent are required for [tangent space](https://en.wikipedia.org/wiki/Tangent_space)
     * [normal mapping](https://en.wikipedia.org/wiki/Normal_mapping) and lighting calculations.
     * It is perpendicular to the normal vector and tangential to the object's surface, while also
     * being aligned with the UV-space's U-axis.
     *
     * - [https://learnopengl.com/Advanced-Lighting/Normal-Mapping](https://learnopengl.com/Advanced-Lighting/Normal-Mapping)
     * - [https://en.wikipedia.org/wiki/Normal_mapping#Calculating_tangent_space](https://en.wikipedia.org/wiki/Normal_mapping#Calculating_tangent_space)
     * - [https://www.opengl-tutorial.org/intermediate-tutorials/tutorial-13-normal-mapping/](https://www.opengl-tutorial.org/intermediate-tutorials/tutorial-13-normal-mapping/)
     * - [https://stackoverflow.com/a/35723775/3726133](https://stackoverflow.com/a/35723775/3726133)
     * - [https://web.archive.org/web/20110708081637/http://www.terathon.com/code/tangent.html](https://web.archive.org/web/20110708081637/http://www.terathon.com/code/tangent.html)
     * - [https://gamedev.stackexchange.com/a/68617/71768](https://gamedev.stackexchange.com/a/68617/71768)
     */
    private fun bakeTangentSpace(triangle: Triangle): Triangle {
        val (v0, v1, v2) = triangle

        val p0 = v0.position.toVec3f()
        val p1 = v1.position.toVec3f()
        val p2 = v2.position.toVec3f()

        val uv0 = v0.textureCoordinates
        val uv1 = v1.textureCoordinates
        val uv2 = v2.textureCoordinates

        // Position deltas.
        val dp1 = p1 - p0
        val dp2 = p2 - p0

        // UV deltas.
        val duv1 = uv1 - uv0
        val duv2 = uv2 - uv0

        // r is the reciprocal (a.k.a. multiplicative inverse) of the determinant of the UV matrix.
        // It is used for scaling the object space coordinates so that they align with the UV
        // coordinates.
        val detUV = duv1.x * duv2.y - duv1.y * duv2.x
        val r = 1f / detUV

        // The triangle's tangent, with the triangle aligned with the UV coordinates.
        val t = (dp1 * duv2.y - dp2 * duv1.y) * r;

        // Per vertex tangent and bitangent. For smooth surfaces, the normal, tangent and bitangent
        // are not necessarily the same as for the flat triangle, so we orthogonalize the triangle's
        // tangent and bitangent with each vertex's normal to get the tangent and bitangent for that
        // vertex.
        val t0 = orthogonalize(t, v0.normal).normalized
        val t1 = orthogonalize(t, v1.normal).normalized
        val t2 = orthogonalize(t, v2.normal).normalized

        // Per vertex bitangent, simply calculated as the cross product of the vertex's normal and
        // tangent.
        val b0 = (v0.normal cross t0).normalized
        val b1 = (v1.normal cross t1).normalized
        val b2 = (v2.normal cross t2).normalized

        return Triangle(
            v0.copy(
                normal = v0.normal.normalized,
                tangent = t0,
                bitangent = b0
            ),
            v1.copy(
                normal = v1.normal.normalized,
                tangent = t1,
                bitangent = b1
            ),
            v2.copy(
                normal = v2.normal.normalized,
                tangent = t2,
                bitangent = b2
            )
        )
    }

    /**
     * - [https://en.wikipedia.org/wiki/Gram%E2%80%93Schmidt_process](https://en.wikipedia.org/wiki/Gram%E2%80%93Schmidt_process)
     * - [https://en.wikipedia.org/wiki/Orthogonalization](https://en.wikipedia.org/wiki/Orthogonalization)
     */
    private fun orthogonalize(
        triangleTangent: Vec3f,
        vertexNormal: Vec3f
    ): Vec3f {
        val t = triangleTangent
        val n = vertexNormal.normalized
        return t - n * (n dot t)
    }
}
