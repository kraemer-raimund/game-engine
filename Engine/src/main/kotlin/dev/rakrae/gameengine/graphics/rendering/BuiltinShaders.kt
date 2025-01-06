package dev.rakrae.gameengine.graphics.rendering

import dev.rakrae.gameengine.core.GameTime
import dev.rakrae.gameengine.graphics.*
import dev.rakrae.gameengine.graphics.rendering.pipeline.*
import dev.rakrae.gameengine.math.*
import kotlin.math.*

object BuiltinShaders {
    object Material {
        val standardPBR: Shader = Shader(PBRVertexShader(), PBRFragmentShader())
        val unlitTextured: Shader = Shader(UnlitTexturedVertexShader(), UnlitTexturedFragmentShader())
        val unlit: Shader = Shader(UnlitVertexShader(), UnlitFragmentShader())
        val unlitSkybox: Shader = Shader(UnlitSkyboxVertexShader(), UnlitSkyboxFragmentShader())
    }

    object PostProcessing {
        fun gammaCorrection(gamma: Float = 2.2f): PostProcessingShader {
            return GammaCorrectionPostProcessingShader(gamma)
        }

        val denoise: PostProcessingShader = DenoisePostProcessingShader()

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

    override fun process(vertex: Mesh.Vertex, uniforms: ShaderUniforms): VertexShaderOutput {
        val mvpMatrix = uniforms.getMatrix(ShaderUniforms.BuiltinKeys.MATRIX_MVP)

        val modelMatrix = uniforms.getMatrix(ShaderUniforms.BuiltinKeys.MATRIX_M)
        val normalWorldSpace = modelMatrix * vertex.normal.normalized.toVec4(0f)
        val tangentWorldSpace = modelMatrix * vertex.tangent.normalized.toVec4(0f)
        val bitangentWorldSpace = modelMatrix * vertex.bitangent.normalized.toVec4(0f)

        val tbnMatrix = Mat4x4f(
            tangentWorldSpace,
            bitangentWorldSpace,
            normalWorldSpace,
            Vec4f(0f, 0f, 0f, 1f)
        )
        // For an orthogonal matrix the transpose is equivalent to the inverse, but much faster.
        val tbnMatrixInv = tbnMatrix.transpose
        val sunLightDirWorldSpace = uniforms.getVector(ShaderUniforms.BuiltinKeys.SUN_LIGHT_DIRECTION)
        val sunLightDirTangentSpace = (tbnMatrixInv * sunLightDirWorldSpace).toVec3f()

        val pointLightPosWorldSpace = uniforms.getVector(ShaderUniforms.BuiltinKeys.POINT_LIGHT_0_POSITION)
        val vertexPositionWorldSpace = modelMatrix * vertex.position
        val pointLightDistance = (pointLightPosWorldSpace.toVec3f() - vertexPositionWorldSpace.toVec3f()).magnitude
        // Strong attenuation for relatively low light range.
        // Later we want to parameterize these values.
        val constant = 1f
        val linear = 0.22f
        val quadratic = 0.20f
        val dist = pointLightDistance
        val pointLightBrightness = (1f / (constant + linear * dist + quadratic * dist * dist)).coerceIn(0f, 1f)

        return VertexShaderOutput(
            position = mvpMatrix * vertex.position,
            shaderVariables = ShaderVariables().apply {
                setVector(
                    "uv", ShaderVariables.VectorVariable(
                        vertex.textureCoordinates,
                        ShaderVariables.Interpolation.PERSPECTIVE
                    )
                )
                setVector(
                    "sunLightDirTangentSpace", ShaderVariables.VectorVariable(
                        sunLightDirTangentSpace,
                        ShaderVariables.Interpolation.PERSPECTIVE
                    )
                )
                setFloat(
                    "pointLightBrightness", ShaderVariables.FloatVariable(
                        pointLightBrightness,
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
        val sunLightDirTangentSpace = inputFragment.shaderVariables.getVector("sunLightDirTangentSpace").value
        val pointLightBrightness = inputFragment.shaderVariables.getFloat("pointLightBrightness").value
        val ambientColor = inputFragment.shaderUniforms.getColor(ShaderUniforms.BuiltinKeys.AMBIENT_COLOR)
        val ambientLightNormalized =
            inputFragment.shaderUniforms.getFloat(ShaderUniforms.BuiltinKeys.AMBIENT_INTENSITY_MULTIPLIER)

        val unlitFragmentColor = albedoTexture
            ?.let { color(it, inputFragment) }
            ?: inputFragment.material.color

        val litFragmentColor = light(
            unlitFragmentColor,
            normalTangentSpace,
            sunLightDirTangentSpace,
            pointLightBrightness,
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

            val textureSampler = TextureSampler(TextureSampler.Filter.NEAREST, uvOffset, uvScale)
            val normalColor = textureSampler.sample(normalMap, Vec2f(uv.x, uv.y))
            return normalColor.toNormal()
        }
    }

    private fun light(
        unlitFragColor: Color,
        normal: Vec3f,
        lightDir: Vec3f,
        pointLightBrightness: Float,
        ambientColor: Color,
        ambientMultiplier: Float
    ): Color {
        // Note: We negate the light direction because the lambertian reflection model expects the
        // vector go *from* the surface *to* the light source (in this case, since it is a
        // directional light, simply from the surface in the direction of the light).
        val sunIntensity = 0.8f
        val sunLambertian = (normal.normalized dot (lightDir * -1).normalized)
            .coerceIn(0f..1f)

        val ambient = ambientColor * pointLightBrightness
        val sun = unlitFragColor * sunLambertian * sunIntensity
        val pointLight = Color.lerp(unlitFragColor, Color.blue, 0.4f) * pointLightBrightness

        val litFragColor = (ambient + sun + pointLight) * ambientMultiplier
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

            val textureSampler = TextureSampler(TextureSampler.Filter.NEAREST, uvOffset, uvScale)
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

private class UnlitTexturedVertexShader : VertexShader {

    override fun process(vertex: Mesh.Vertex, uniforms: ShaderUniforms): VertexShaderOutput {
        val mvpMatrix = uniforms.getMatrix(ShaderUniforms.BuiltinKeys.MATRIX_MVP)
        return VertexShaderOutput(
            position = mvpMatrix * vertex.position,
            shaderVariables = ShaderVariables().apply {
                setVector(
                    "uv", ShaderVariables.VectorVariable(
                        vertex.textureCoordinates,
                        ShaderVariables.Interpolation.PERSPECTIVE
                    )
                )
            }
        )
    }
}


private class UnlitTexturedFragmentShader : FragmentShader {

    override fun process(inputFragment: InputFragment): OutputFragment {
        val textureBitmap = (inputFragment.material.albedo as? BitmapTexture)?.bitmap
            ?: Bitmap(1, 1, Color.black)

        val uv = inputFragment.shaderVariables.getVector("uv").value
        val uvOffset = inputFragment.material.uvOffset
        val uvScale = inputFragment.material.uvScale

        val textureSampler = TextureSampler(TextureSampler.Filter.NEAREST, uvOffset, uvScale)
        val color = textureSampler.sample(textureBitmap, Vec2f(uv.x, uv.y))

        return OutputFragment(
            fragmentColor = color,
            depth = inputFragment.fragPos.z
        )
    }
}

private class UnlitVertexShader : VertexShader {

    override fun process(vertex: Mesh.Vertex, uniforms: ShaderUniforms): VertexShaderOutput {
        val mvpMatrix = uniforms.getMatrix(ShaderUniforms.BuiltinKeys.MATRIX_MVP)
        return VertexShaderOutput(
            position = mvpMatrix * vertex.position,
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

private class UnlitSkyboxVertexShader : VertexShader {

    override fun process(vertex: Mesh.Vertex, uniforms: ShaderUniforms): VertexShaderOutput {
        val mvpMatrix = uniforms.getMatrix(ShaderUniforms.BuiltinKeys.MATRIX_MVP)
        return VertexShaderOutput(
            position = mvpMatrix * vertex.position,
            shaderVariables = ShaderVariables().apply {
                setVector(
                    "uv", ShaderVariables.VectorVariable(
                        vertex.textureCoordinates,
                        ShaderVariables.Interpolation.PERSPECTIVE
                    )
                )
                setVector(
                    "vertexPosObjectSpace", ShaderVariables.VectorVariable(
                        vertex.position.toVec3f(),
                        ShaderVariables.Interpolation.PERSPECTIVE
                    )
                )
            }
        )
    }
}


private class UnlitSkyboxFragmentShader : FragmentShader {

    override fun process(inputFragment: InputFragment): OutputFragment {
        val textureBitmap = (inputFragment.material.albedo as? BitmapTexture)?.bitmap
            ?: Bitmap(1, 1, Color.black)

        // Screen size/render resolution temporarily hardcoded. Should probably be shader uniforms.
        val screenSize = Vec2f(1280f, 720f)
        val camFov = 0.5f * PI.toFloat()

        val camRot = inputFragment.shaderUniforms.getVector(ShaderUniforms.BuiltinKeys.CAMERA_ROT_WORLD)
        val camRotHorizontal = camRot.y.mod(2 * PI.toFloat()) / (2 * PI.toFloat())
        val camRotVertical = camRot.x.mod(PI.toFloat()) / PI.toFloat()
        val uNormalized = inputFragment.fragPos.x / screenSize.x
        val vNormalized = inputFragment.fragPos.y / screenSize.y
        val fovNormalized = camFov / (2 * PI.toFloat())
        val uv = Vec2f(
            (-camRotHorizontal / fovNormalized + uNormalized) * fovNormalized + 0.15f,
            1f - (camRotVertical / fovNormalized + vNormalized) * fovNormalized - 0.35f
        )
        val uvOffset = inputFragment.material.uvOffset
        val uvScale = inputFragment.material.uvScale

        val textureSampler = TextureSampler(TextureSampler.Filter.LINEAR, uvOffset, uvScale)
        val color = textureSampler.sample(textureBitmap, uv)

        return OutputFragment(
            fragmentColor = color,
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

private class DenoisePostProcessingShader : PostProcessingShader {

    override fun postProcess(position: Vec2i, framebuffer: Bitmap, zBuffer: Buffer2f): Color? {
        // In case we rasterize only every second pixel, we can interpolate here on the pixel level
        // to fill the gaps.
        val (x, y) = position
        val xEven = x.mod(2) == 0
        val yEven = y.mod(2) == 0
        return when {
            xEven && !yEven -> return framebuffer.getPixelClamped(x - 1, y)
            !xEven && yEven -> return framebuffer.getPixelClamped(x, y - 1)
            xEven && yEven -> return framebuffer.getPixelClamped(x - 1, y - 1)
            else -> null
        }
    }

    private fun Bitmap.getPixelClamped(x: Int, y: Int): Color {
        return getPixel(
            x.coerceIn(0..<width),
            y.coerceIn(0..<height)
        )
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
}

class ReplaceColorPostProcessingShader : PostProcessingShader {

    override fun postProcess(
        position: Vec2i,
        framebuffer: Bitmap,
        zBuffer: Buffer2f
    ): Color {
        val (x, y) = position
        return when (framebuffer.getPixel(x, y)) {
            Color.red -> Color.blue
            Color.black -> Color.white
            else -> framebuffer.getPixel(x, y)
        }
    }
}

class GreyscalePostProcessingShader : PostProcessingShader {

    override fun postProcess(
        position: Vec2i,
        framebuffer: Bitmap,
        zBuffer: Buffer2f
    ): Color {
        val (x, y) = position
        val color = framebuffer.getPixel(x, y)
        val mean = (color.r.toUInt() + color.g.toUInt() + color.b.toUInt()) / 3u
        return Color(
            r = mean.toInt(),
            g = mean.toInt(),
            b = mean.toInt(),
            a = color.a.toInt()
        )
    }
}

class WavesPostProcessingShader : PostProcessingShader {

    override fun postProcess(
        position: Vec2i,
        framebuffer: Bitmap,
        zBuffer: Buffer2f
    ): Color {
        val (x, y) = position

        val periods = 2
        val frequency = periods * (2 * PI) * (1 / framebuffer.width.toDouble())
        val amplitude = 0.05 * framebuffer.height
        val offset = GameTime.frameTime
        val wave = sin(x.toDouble() * frequency + offset) * amplitude
        val sampledY = y + wave.toInt()

        fun clamp(value: Int, min: Int, max: Int) = min(max, max(min, value))
        val clampedY = clamp(sampledY, 0, framebuffer.height - 1)
        return framebuffer.getPixel(x, clampedY)
    }
}

class BlurPostProcessingShader : PostProcessingShader {

    override fun postProcess(
        position: Vec2i,
        framebuffer: Bitmap,
        zBuffer: Buffer2f
    ): Color {
        fun clampInBuffer(position: Vec2i) = Vec2i(
            min(framebuffer.width - 1, max(0, position.x)),
            min(framebuffer.height - 1, max(0, position.y)),
        )

        val p1 = clampInBuffer(position + Vec2i(-2, -2))
        val p2 = clampInBuffer(position + Vec2i(2, -2))
        val p3 = clampInBuffer(position + Vec2i(2, -2))
        val p4 = clampInBuffer(position + Vec2i(2, 2))

        return Color.lerp(
            Color.lerp(framebuffer.getPixel(p1.x, p1.y), framebuffer.getPixel(p2.x, p2.y), 0.5f),
            Color.lerp(framebuffer.getPixel(p3.x, p3.y), framebuffer.getPixel(p4.x, p4.y), 0.5f),
            0.5f
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
