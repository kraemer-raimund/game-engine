package dev.rakrae.gameengine.graphics.rendering.pipeline

import dev.rakrae.gameengine.graphics.Bitmap
import dev.rakrae.gameengine.graphics.Material
import dev.rakrae.gameengine.graphics.Triangle
import dev.rakrae.gameengine.math.*

internal class Rasterizer {

    fun rasterize(
        triangle: Triangle,
        normalWorldSpace: Vec3f,
        vertexShaderOutputs: List<VertexShaderOutputs>,
        material: Material,
        renderTexture: Bitmap?,
        fragmentShader: FragmentShader,
        renderContext: RenderContext
    ) {
        with(renderContext) {
            val triangle2i = arrayOf(triangle.v0, triangle.v1, triangle.v2)
                .map { Vec2i(it.position.x.toInt(), it.position.y.toInt()) }
                .let { Triangle2i(it[0], it[1], it[2]) }
            val boundingBox = AABB2i
                .calculateBoundingBox(triangle2i)
                .clampWithin(framebuffer.imageBounds())

            // For each point within the triangle's AABB, render the point if it lies within the triangle.
            for (x in boundingBox.min.x..boundingBox.max.x) {
                for (y in boundingBox.min.y..boundingBox.max.y) {
                    val barycentricCoordinates = BarycentricCoordinates.of(Vec2i(x, y), triangle2i)
                    if (barycentricCoordinates.isWithinTriangle) {
                        val interpolatedDepth = interpolateDepth(triangle, barycentricCoordinates)
                        if (interpolatedDepth < zBuffer.get(x, y)) {
                            zBuffer.set(x, y, interpolatedDepth)
                            val inputFragment = InputFragment(
                                windowSpacePosition = Vec2i(x, y),
                                renderContext = renderContext,
                                interpolatedNormal = interpolateNormal(triangle, barycentricCoordinates),
                                faceNormalWorldSpace = normalWorldSpace,
                                depth = interpolatedDepth,
                                material = material,
                                renderTexture = renderTexture,
                                uv = interpolateUVs(triangle, barycentricCoordinates, renderContext),
                                lightDirTangentSpace = vertexShaderOutputs
                                    .map { it.lightDirTangentSpace ?: Vec3f.zero }
                                    .let { interpolateVector(it[0], it[1], it[2], barycentricCoordinates) }
                                    .also { it.normalized }
                            )
                            val outputFragment = fragmentShader.process(inputFragment)
                            framebuffer.setPixel(x, y, outputFragment.fragmentColor)
                        }
                    }
                }
            }
        }
    }

    private fun interpolateDepth(
        triangle: Triangle,
        barycentricCoordinates: BarycentricCoordinates
    ): Float {
        val z1 = triangle.v0.position.toVec3f().z.ndcToDepth()
        val z2 = triangle.v1.position.toVec3f().z.ndcToDepth()
        val z3 = triangle.v2.position.toVec3f().z.ndcToDepth()
        val b = barycentricCoordinates
        val interpolatedZ = z1 * b.a1 + z2 * b.a2 + z3 * b.a3
        return interpolatedZ
    }

    /**
     * Normalized device coordinates [-1; 1] to depth value [0; 1].
     */
    private fun Float.ndcToDepth(): Float {
        return (this + 1f) * 0.5f
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

    private fun interpolateVector(
        v1: Vec3f,
        v2: Vec3f,
        v3: Vec3f,
        barycentricCoordinates: BarycentricCoordinates
    ): Vec3f {
        val b = barycentricCoordinates
        return Vec3f(
            v1.x * b.a1 + v2.x * b.a2 + v3.x * b.a3,
            v1.y * b.a1 + v2.y * b.a2 + v3.y * b.a3,
            v1.z * b.a1 + v2.z * b.a2 + v3.z * b.a3
        )
    }

    private fun interpolateUVs(
        triangle: Triangle,
        barycentricCoordinates: BarycentricCoordinates,
        renderContext: RenderContext
    ): Vec2f {
        val uv1 = triangle.v0.textureCoordinates
        val uv2 = triangle.v1.textureCoordinates
        val uv3 = triangle.v2.textureCoordinates
        val b = barycentricCoordinates.toPerspectiveCorrectBarycentric(renderContext)
        return Vec2f(
            uv1.x * b.a1 + uv2.x * b.a2 + uv3.x * b.a3,
            uv1.y * b.a1 + uv2.y * b.a2 + uv3.y * b.a3
        )
    }

    private fun BarycentricCoordinates.toPerspectiveCorrectBarycentric(
        renderContext: RenderContext
    ): BarycentricCoordinates {
        // https://stackoverflow.com/a/24460895/3726133
        // https://stackoverflow.com/a/74630682/3726133
        val wc = renderContext.wComponents
        val w1 = wc.triangle1W
        val w2 = wc.triangle2W
        val w3 = wc.triangle3W
        val p = Vec3f(a1 / w1, a2 / w2, a3 / w3) *
                (1f / (a1 / w1 + a2 / w2 + a3 / w3))
        return BarycentricCoordinates(p.x, p.y, p.z)
    }

    private fun Bitmap.imageBounds(): AABB2i {
        return AABB2i(
            Vec2i(0, 0),
            Vec2i(this.width - 1, this.height - 1)
        )
    }
}
