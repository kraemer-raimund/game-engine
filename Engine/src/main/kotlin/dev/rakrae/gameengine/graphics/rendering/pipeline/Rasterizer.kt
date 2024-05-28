package dev.rakrae.gameengine.graphics.rendering.pipeline

import dev.rakrae.gameengine.graphics.Bitmap
import dev.rakrae.gameengine.graphics.Material
import dev.rakrae.gameengine.graphics.Triangle
import dev.rakrae.gameengine.math.*

internal class Rasterizer {

    fun rasterize(
        triangle: Triangle,
        shaderVariables: List<ShaderVariables>,
        normalWorldSpace: Vec3f,
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
            boundingBox.forEach { pixelPosition ->
                val (x, y) = pixelPosition
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
                            shaderVariables = interpolate(
                                shaderVariables[0],
                                shaderVariables[1],
                                shaderVariables[2],
                                renderContext.wComponents,
                                barycentricCoordinates
                            )
                        )
                        val outputFragment = fragmentShader.process(inputFragment)
                        framebuffer.setPixel(x, y, outputFragment.fragmentColor)
                    }
                }
            }
        }
    }

    private fun interpolate(
        shaderVariables0: ShaderVariables,
        shaderVariables1: ShaderVariables,
        shaderVariables2: ShaderVariables,
        wComponents: RenderContext.WComponents,
        barycentricCoordinates: BarycentricCoordinates
    ): ShaderVariables {
        val interpolatedShaderVariables = ShaderVariables()
        for (key in shaderVariables0.floatKeys) {
            val variable0 = shaderVariables0.getFloat(key)
            val variable1 = shaderVariables1.getFloat(key)
            val variable2 = shaderVariables2.getFloat(key)
            val interpolatedValue = interpolate(
                variable0.value,
                variable1.value,
                variable2.value,
                variable0.interpolation,
                wComponents,
                barycentricCoordinates
            )
            interpolatedShaderVariables.setFloat(key, interpolatedValue)
        }
        for (key in shaderVariables0.vectorKeys) {
            val variable0 = shaderVariables0.getVector(key)
            val variable1 = shaderVariables1.getVector(key)
            val variable2 = shaderVariables2.getVector(key)
            val interpolatedValue = interpolate(
                variable0.value,
                variable1.value,
                variable2.value,
                variable0.interpolation,
                wComponents,
                barycentricCoordinates
            )
            interpolatedShaderVariables.setVector(key, interpolatedValue)
        }
        return interpolatedShaderVariables
    }

    private fun interpolate(
        f0: Float,
        f1: Float,
        f2: Float,
        interpolation: ShaderVariables.Interpolation,
        wComponents: RenderContext.WComponents,
        barycentricCoordinates: BarycentricCoordinates
    ): ShaderVariables.FloatVariable {
        val b = when (interpolation) {
            ShaderVariables.Interpolation.FLAT -> BarycentricCoordinates(1f, 0f, 0f)
            ShaderVariables.Interpolation.LINEAR -> barycentricCoordinates
            ShaderVariables.Interpolation.PERSPECTIVE ->
                barycentricCoordinates.toPerspectiveCorrectBarycentric(wComponents)
        }
        val interpolated = f0 * b.a1 + f1 * b.a2 + f2 * b.a3
        return ShaderVariables.FloatVariable(interpolated, interpolation)
    }

    private fun interpolate(
        v0: Vec3f,
        v1: Vec3f,
        v2: Vec3f,
        interpolation: ShaderVariables.Interpolation,
        wComponents: RenderContext.WComponents,
        barycentricCoordinates: BarycentricCoordinates
    ): ShaderVariables.VectorVariable {
        val b = when (interpolation) {
            ShaderVariables.Interpolation.FLAT -> BarycentricCoordinates(1f, 0f, 0f)
            ShaderVariables.Interpolation.LINEAR -> barycentricCoordinates
            ShaderVariables.Interpolation.PERSPECTIVE ->
                barycentricCoordinates.toPerspectiveCorrectBarycentric(wComponents)
        }
        val interpolated = Vec3f(
            v0.x * b.a1 + v1.x * b.a2 + v2.x * b.a3,
            v0.y * b.a1 + v1.y * b.a2 + v2.y * b.a3,
            v0.z * b.a1 + v1.z * b.a2 + v2.z * b.a3
        )
        return ShaderVariables.VectorVariable(interpolated, interpolation)
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
        val b = barycentricCoordinates.toPerspectiveCorrectBarycentric(renderContext.wComponents)
        return Vec2f(
            uv1.x * b.a1 + uv2.x * b.a2 + uv3.x * b.a3,
            uv1.y * b.a1 + uv2.y * b.a2 + uv3.y * b.a3
        )
    }

    private fun BarycentricCoordinates.toPerspectiveCorrectBarycentric(wComponents: RenderContext.WComponents): BarycentricCoordinates {
        // https://stackoverflow.com/a/24460895/3726133
        // https://stackoverflow.com/a/74630682/3726133
        val w1 = wComponents.triangle1W
        val w2 = wComponents.triangle2W
        val w3 = wComponents.triangle3W
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

    private fun AABB2i.forEach(actionForEachPixel: (Vec2i) -> Unit) {
        for (x in min.x..max.x) {
            for (y in min.y..max.y) {
                actionForEachPixel(Vec2i(x, y))
            }
        }
    }
}
