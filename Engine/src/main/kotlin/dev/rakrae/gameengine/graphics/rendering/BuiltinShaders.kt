package dev.rakrae.gameengine.graphics.rendering

import dev.rakrae.gameengine.graphics.*
import dev.rakrae.gameengine.graphics.rendering.pipeline.*
import dev.rakrae.gameengine.math.*
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

object BuiltinShaders {
    object Material {
        val standardPBR: Shader = Shader(PBRVertexShader(), PBRFragmentShader())
        val unlit: Shader = Shader(UnlitVertexShader(), UnlitFragmentShader())
    }

    object PostProcessing {
        fun gammaCorrection(gamma: Float = 2.2f): PostProcessingShader {
            return GammaCorrectionPostProcessingShader(gamma)
        }

        fun depthOfField(effectStrength: Float = 1f): PostProcessingShader {
            return DepthOfFieldPostProcessingShader(effectStrength)
        }

        val depthDarkening: PostProcessingShader = DepthDarkeningPostProcessingShader()

        fun outline(thickness: Int, threshold: Float, outlineColor: Color): PostProcessingShader {
            return OutlinePostProcessingShader(thickness, threshold, outlineColor)
        }
    }

    object Deferred {
        fun outline(thickness: Int, outlineColor: Color): DeferredShader {
            return OutlineDeferredShader(thickness, outlineColor)
        }
    }
}

class Shader(val vertexShader: VertexShader, val fragmentShader: FragmentShader)

private class PBRVertexShader : VertexShader {

    override fun process(vertex: Mesh.Vertex, inputs: VertexShaderInput): VertexShaderOutput {
        val normalWorldSpace = inputs.model * vertex.normal.toVec4()
        val tangentWorldSpace = inputs.model * vertex.tangent.toVec4()
        val bitangentWorldSpace = inputs.model * vertex.bitangent.toVec4()
        val tbnMatrix = Mat4x4f(
            tangentWorldSpace,
            bitangentWorldSpace,
            normalWorldSpace,
            Vec4f(0f, 0f, 0f, 1f)
        )
        // For an orthogonal matrix the transpose is equivalent to the inverse, but much faster.
        val tbnMatrixInv = tbnMatrix.transpose
        val lightDirTangentSpace = (tbnMatrixInv * inputs.lightDirWorldSpace.toVec4()).toVec3f()

        return VertexShaderOutput(
            position = inputs.projection * inputs.modelView * vertex.position,
            shaderVariables = ShaderVariables().apply {
                setVector(
                    "uv", ShaderVariables.VectorVariable(
                        vertex.textureCoordinates,
                        ShaderVariables.Interpolation.PERSPECTIVE
                    )
                )
                setVector(
                    "lightDirTangentSpace", ShaderVariables.VectorVariable(
                        lightDirTangentSpace,
                        ShaderVariables.Interpolation.PERSPECTIVE
                    )
                )
            }
        )
    }

}

private class PBRFragmentShader : FragmentShader {

    override fun process(inputFragment: InputFragment): OutputFragment {
        val normalMap = inputFragment.material.normal?.bitmap
        val albedoTexture = (inputFragment.material.albedo as? BitmapTexture)?.bitmap ?: inputFragment.renderTexture

        val normalTangentSpace = normalVector(normalMap, inputFragment)
        val lightDirTangentSpace = inputFragment.shaderVariables.getVector("lightDirTangentSpace").value
        val ambientColor = inputFragment.shaderUniforms.getColor(ShaderUniforms.BuiltinKeys.ambientColor)
        val ambientLightNormalized =
            inputFragment.shaderUniforms.getFloat(ShaderUniforms.BuiltinKeys.ambientIntensityMultiplier)

        val unlitFragmentColor = albedoTexture
            ?.let { color(it, inputFragment) }
            ?: inputFragment.material.color

        val litFragmentColor = light(
            unlitFragmentColor,
            normalTangentSpace,
            lightDirTangentSpace,
            ambientColor,
            ambientLightNormalized
        )

        return OutputFragment(
            fragmentColor = litFragmentColor,
            depth = inputFragment.fragPos.z
        )
    }

    private fun normalVector(
        normalMap: Bitmap?,
        inputFragment: InputFragment
    ): Vec3f {
        return if (normalMap == null) {
            Vec3f(0f, 0f, 1f)
        } else {
            val uv = inputFragment.shaderVariables.getVector("uv").value
            val uvOffset = inputFragment.material.uvOffset
            val uvScale = inputFragment.material.uvScale

            val textureSampler = TextureSampler(TextureSampler.Filter.LINEAR, uvOffset, uvScale)
            val normalColor = textureSampler.sample(normalMap, Vec2f(uv.x, uv.y))
            return normalColor.toNormal()
        }
    }

    private fun light(
        unlitFragColor: Color,
        normal: Vec3f,
        lightDir: Vec3f,
        ambientColor: Color,
        ambientMultiplier: Float
    ): Color {
        val lambertian = (normal.normalized dot lightDir.normalized)
            .coerceIn(0f..1f)
        val litFragColor = (ambientColor + unlitFragColor * lambertian) * ambientMultiplier
        return litFragColor.copy(a = unlitFragColor.a)
    }

    private fun color(
        texture: Bitmap?,
        inputFragment: InputFragment
    ): Color {
        return if (texture == null) {
            inputFragment.material.color
        } else {
            val uv = inputFragment.shaderVariables.getVector("uv").value
            val uvOffset = inputFragment.material.uvOffset
            val uvScale = inputFragment.material.uvScale

            val textureSampler = TextureSampler(TextureSampler.Filter.LINEAR, uvOffset, uvScale)
            textureSampler.sample(texture, Vec2f(uv.x, uv.y))
        }
    }

    private fun Color.toNormal(): Vec3f {
        val colorAsVector = Vec3f(
            r.toInt() / Byte.MAX_VALUE.toFloat(),
            g.toInt() / Byte.MAX_VALUE.toFloat(),
            b.toInt() / Byte.MAX_VALUE.toFloat()
        )
        // Normal maps use values between -1 and 1.
        val remapped = colorAsVector * 2f - Vec3f.one
        return remapped.normalized
    }
}

private class UnlitVertexShader : VertexShader {

    override fun process(vertex: Mesh.Vertex, inputs: VertexShaderInput): VertexShaderOutput {
        return VertexShaderOutput(
            position = inputs.projection * inputs.modelView * vertex.position,
            shaderVariables = ShaderVariables()
        )
    }
}


private class UnlitFragmentShader : FragmentShader {

    override fun process(inputFragment: InputFragment): OutputFragment {
        return OutputFragment(
            fragmentColor = inputFragment.material.color,
            depth = inputFragment.fragPos.z
        )
    }
}

private class GammaCorrectionPostProcessingShader(private val gamma: Float = 2.2f) : PostProcessingShader {

    override fun postProcess(position: Vec2i, framebuffer: Bitmap, zBuffer: Buffer2f): Color {
        val (x, y) = position
        val color = framebuffer.getPixel(x, y)
        return Color(
            gammaCorrect(color.r),
            gammaCorrect(color.g),
            gammaCorrect(color.b),
            color.a
        )
    }

    private fun gammaCorrect(colorComponent: UByte): UByte {
        return ((colorComponent.toInt() / 255f).pow(gamma) * 255).toUInt().toUByte()
    }
}

private class DepthOfFieldPostProcessingShader(
    private val effectStrength: Float = 1f,
) : PostProcessingShader {

    override fun postProcess(position: Vec2i, framebuffer: Bitmap, zBuffer: Buffer2f): Color {
        val (x, y) = position
        val depth = zBuffer.get(x, y)

        val depthInFocus = zBuffer.get(zBuffer.width / 2, zBuffer.height / 2).coerceIn(0f, 1f)
        val zBufferDifference = abs(depth - depthInFocus)
        // Constant 32 has been chosen experimentally.
        val resolutionIndependentBlurDistance = (framebuffer.height.toFloat() / 1080) * 16
        // The further away from the camera, the weaker the blur effect.
        val blur = (effectStrength * zBufferDifference * (1f - depth) * resolutionIndependentBlurDistance).toInt()

        // For performance reasons, we only let every n-th pixel influence the blurred pixel color.
        // This results in roughly 3 pixels per dimension influencing the final result.
        val step = (blur / 2).coerceAtLeast(1)
        var blurredColor = framebuffer.getPixel(x, y)

        for (ix in -blur..blur step step) {
            for (iy in -blur..blur step step) {
                if (x + ix in 0..<framebuffer.width && y + iy in 0..<framebuffer.height) {
                    val posX = x + ix
                    val posY = y + iy
                    val color = if (zBuffer.get(posX, posY) == Float.POSITIVE_INFINITY) {
                        Color(0u, 0u, 0u, 255u)
                    } else {
                        framebuffer.getPixel(posX, posY)
                    }
                    val distance = sqrt((ix * ix + iy * iy).toDouble()).toFloat()

                    blurredColor = weightedAverageOf(blurredColor, distance, color, 1f)
                }
            }
        }
        return blurredColor.copy(a = 255u)
    }

    private fun weightedAverageOf(c1: Color, weight1: Float, c2: Color, weight2: Float): Color {
        val r = (c1.r.toFloat() * weight1 + c2.r.toFloat() * weight2) / (weight1 + weight2)
        val g = (c1.g.toFloat() * weight1 + c2.g.toFloat() * weight2) / (weight1 + weight2)
        val b = (c1.b.toFloat() * weight1 + c2.b.toFloat() * weight2) / (weight1 + weight2)
        val a = (c1.a.toFloat() * weight1 + c2.a.toFloat() * weight2) / (weight1 + weight2)
        return Color(
            r.toInt().toUByte(),
            g.toInt().toUByte(),
            b.toInt().toUByte(),
            a.toInt().toUByte()
        )
    }

}

private class DepthDarkeningPostProcessingShader : PostProcessingShader {

    override fun postProcess(position: Vec2i, framebuffer: Bitmap, zBuffer: Buffer2f): Color {
        val (x, y) = position
        val color = framebuffer.getPixel(x, y)
        // We want to darken objects that are far away from the camera, and brighten those
        // close to the camera.
        val zBufferDarkening = 4 * (1f - zBuffer.get(x, y)).pow(2)
        return color * zBufferDarkening.coerceIn(0f, 1f)
    }

    private operator fun Color.times(value: Float): Color {
        return Color(
            (value * r.toInt()).toInt().toUByte(),
            (value * g.toInt()).toInt().toUByte(),
            (value * b.toInt()).toInt().toUByte(),
            255u
        )
    }
}

private class OutlinePostProcessingShader(
    private val thickness: Int,
    private val threshold: Float,
    private val outlineColor: Color
) : PostProcessingShader {

    override fun postProcess(position: Vec2i, framebuffer: Bitmap, zBuffer: Buffer2f): Color? {
        val depth = zBuffer.get(position.x, position.y)
        for (x in position.x - thickness..position.x + thickness) {
            for (y in position.y - thickness..position.y + thickness) {
                if (x in 0..<zBuffer.width && y in 0..<zBuffer.height) {
                    val depthAtNeighbor = zBuffer.get(x, y)
                    if (abs(depth - depthAtNeighbor) > threshold) {
                        return outlineColor
                    }
                }
            }
        }
        return null
    }
}

private class OutlineDeferredShader(
    private val thickness: Int,
    private val outlineColor: Color
) : DeferredShader {

    override fun postProcess(
        position: Vec2i,
        framebuffer: Bitmap,
        zBuffer: Buffer2f,
        deferredFramebuffer: Bitmap,
        deferredZBuffer: Buffer2f
    ): Color? {
        val depthDeferred = deferredZBuffer.get(position.x, position.y)
        if (depthDeferred == Float.POSITIVE_INFINITY) {
            return null
        }
        val depthOriginal = zBuffer.get(position.x, position.y)
        if (depthDeferred > depthOriginal) {
            return null
        }

        for (x in position.x - thickness..position.x + thickness) {
            for (y in position.y - thickness..position.y + thickness) {
                if (x in 0..<zBuffer.width && y in 0..<zBuffer.height) {
                    if (deferredZBuffer.get(x, y) == Float.POSITIVE_INFINITY) {
                        return outlineColor
                    }
                }
            }
        }
        return null
    }
}
