package dev.rakrae.gameengine.graphics.rendering.shaders

import dev.rakrae.gameengine.graphics.Bitmap
import dev.rakrae.gameengine.graphics.Color
import dev.rakrae.gameengine.graphics.rendering.pipeline.FragmentShader
import dev.rakrae.gameengine.graphics.rendering.pipeline.InputFragment
import dev.rakrae.gameengine.graphics.rendering.pipeline.OutputFragment
import dev.rakrae.gameengine.math.Vec3f
import dev.rakrae.gameengine.math.Vec4f
import kotlin.math.pow

class UvTextureFragmentShader : FragmentShader {

    override fun process(inputFragment: InputFragment): OutputFragment {
        val normalMap = inputFragment.material.normal?.bitmap
        val texture = inputFragment.material.albedo?.bitmap

        val normal =
            (inputFragment.renderContext.projectionViewModelMatrix * Vec4f(
                normalVector(normalMap, inputFragment),
                1f
            )).toVec3f().normalized
        val fragmentColor = color(texture, inputFragment) * lightingBrightness(normal, inputFragment)

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
            val x = 10f * uv.x * (normalMap.width - 1)
            val y = 10f * uv.y * (normalMap.height - 1)
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
            val x = 10f * uv.x * (texture.width - 1)
            val y = 10f * uv.y * (texture.height - 1)
            texture.getPixel(
                x = x.toInt().mod(texture.width - 1),
                y = y.toInt().mod(texture.height - 1)
            )
        }
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
}