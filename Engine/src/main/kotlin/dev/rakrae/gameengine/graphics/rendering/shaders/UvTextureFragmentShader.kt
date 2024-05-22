package dev.rakrae.gameengine.graphics.rendering.shaders

import dev.rakrae.gameengine.graphics.Bitmap
import dev.rakrae.gameengine.graphics.BitmapTexture
import dev.rakrae.gameengine.graphics.Color
import dev.rakrae.gameengine.graphics.rendering.pipeline.FragmentShader
import dev.rakrae.gameengine.graphics.rendering.pipeline.InputFragment
import dev.rakrae.gameengine.graphics.rendering.pipeline.OutputFragment
import dev.rakrae.gameengine.math.Vec3f
import dev.rakrae.gameengine.math.Vec4f
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.round

class UvTextureFragmentShader : FragmentShader {

    override fun process(inputFragment: InputFragment): OutputFragment {
        val normalMap = inputFragment.material.normal?.bitmap
        val albedoTexture = (inputFragment.material.albedo as? BitmapTexture)?.bitmap ?: inputFragment.renderTexture

        val normal =
            (inputFragment.renderContext.projectionViewModelMatrix * Vec4f(
                normalVector(normalMap, inputFragment),
                1f
            )).toVec3f().normalized
        val fragmentColor = if (albedoTexture != null) {
            color(albedoTexture, inputFragment) * lightingBrightness(normal, inputFragment)
        } else {
            inputFragment.material.color
        }

        return OutputFragment(
            fragmentColor = fragmentColor,
            depth = inputFragment.depth
        )
    }

    private fun normalVector(
        normalMap: Bitmap?,
        inputFragment: InputFragment
    ): Vec3f {
        return if (normalMap == null) {
            inputFragment.interpolatedNormal
        } else {
            val uv = inputFragment.uv
            val uvScale = inputFragment.material.uvScale
            val x = uvScale.x * uv.x * (normalMap.width - 1)
            val y = uvScale.y * uv.y * (normalMap.height - 1)
            val normalColor = normalMap.getPixel(
                x = x.toInt().coerceIn(0, normalMap.width - 1),
                y = y.toInt().coerceIn(0, normalMap.height - 1)
            )
            val projectionViewModelMatrix = inputFragment.renderContext.projectionViewModelMatrix
            return (projectionViewModelMatrix * Vec4f(normalColor.toNormal(), 1f)).toVec3f().normalized
        }
    }

    private fun lightingBrightness(normal: Vec3f, inputFragment: InputFragment): Float {
        val projectionViewModelMatrix = inputFragment.renderContext.projectionViewModelMatrix
        val lightDirection = (projectionViewModelMatrix * Vec4f(-6f, 0f, 0.6f, 1f)).toVec3f().normalized
        val lightIntensity = 2f
        val illuminationAngleNormalized = (normal.normalized dot lightDirection.normalized)
            .coerceIn(0f..1f)
        return 0.6f + 0.4f * illuminationAngleNormalized.pow(inputFragment.material.glossiness) * lightIntensity
    }

    private fun color(
        texture: Bitmap?,
        inputFragment: InputFragment
    ): Color {
        return if (texture == null) {
            inputFragment.material.color
        } else {
            val uv = inputFragment.uv
            val uvScale = inputFragment.material.uvScale
            val x = uvScale.x * uv.x * (texture.width - 1)
            val y = uvScale.y * uv.y * (texture.height - 1)
            when (interpolation) {
                Interpolation.BILINEAR -> interpolateBilinear(x, y, texture)
                else -> interpolateNearest(x, y, texture)
            }
        }
    }

    private fun interpolateNearest(
        x: Float,
        y: Float,
        texture: Bitmap
    ): Color {
        val xInterpolated = round(x).toInt().mod(texture.width - 1)
        val yInterpolated = round(y).toInt().mod(texture.height - 1)
        return texture.getPixel(xInterpolated, yInterpolated)
    }

    private fun interpolateBilinear(
        x: Float,
        y: Float,
        texture: Bitmap
    ): Color {
        val xLow = floor(x).toInt()
        val xHigh = ceil(x).toInt()
        val yLow = floor(y).toInt()
        val yHigh = ceil(y).toInt()

        val colorBottomLeft = texture.getPixel(
            xLow.mod(texture.width - 1),
            yLow.mod(texture.height - 1)
        )
        val colorBottomRight = texture.getPixel(
            xHigh.mod(texture.width - 1),
            yLow.mod(texture.height - 1)
        )
        val colorTopLeft = texture.getPixel(
            xLow.mod(texture.width - 1),
            yHigh.mod(texture.height - 1)
        )
        val colorTopRight = texture.getPixel(
            xHigh.mod(texture.width - 1),
            yHigh.mod(texture.height - 1)
        )

        val weightX = x - xLow
        val weightY = y - yLow
        val biLerpedColor = lerp(
            lerp(colorBottomLeft, colorBottomRight, weightX),
            lerp(colorTopLeft, colorTopRight, weightX),
            weightY
        )
        return biLerpedColor
    }

    private fun Color.toNormal(): Vec3f {
        return Vec3f(
            r.toInt() / Byte.MAX_VALUE.toFloat(),
            g.toInt() / Byte.MAX_VALUE.toFloat(),
            b.toInt() / Byte.MAX_VALUE.toFloat()
        )
    }

    private operator fun Color.times(value: Float): Color {
        return Color(
            (value * r.toInt()).toInt().toUByte(),
            (value * g.toInt()).toInt().toUByte(),
            (value * b.toInt()).toInt().toUByte(),
            255u
        )
    }

    private fun lerp(color1: Color, color2: Color, t: Float): Color {
        val r = (1 - t) * color1.r.toInt() + t * color2.r.toInt()
        val g = (1 - t) * color1.g.toInt() + t * color2.g.toInt()
        val b = (1 - t) * color1.b.toInt() + t * color2.b.toInt()
        return Color(
            (r.toInt().coerceIn(0, 255)).toUByte(),
            (g.toInt().coerceIn(0, 255)).toUByte(),
            (b.toInt().coerceIn(0, 255)).toUByte(),
            255u
        )
    }

    companion object {
        val interpolation = Interpolation.BILINEAR
    }

    enum class Interpolation {
        NEAREST,
        BILINEAR
    }
}
