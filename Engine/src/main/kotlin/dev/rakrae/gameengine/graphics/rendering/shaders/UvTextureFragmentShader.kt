package dev.rakrae.gameengine.graphics.rendering.shaders

import dev.rakrae.gameengine.graphics.Bitmap
import dev.rakrae.gameengine.graphics.BitmapTexture
import dev.rakrae.gameengine.graphics.Color
import dev.rakrae.gameengine.graphics.TextureSampler
import dev.rakrae.gameengine.graphics.rendering.pipeline.FragmentShader
import dev.rakrae.gameengine.graphics.rendering.pipeline.InputFragment
import dev.rakrae.gameengine.graphics.rendering.pipeline.OutputFragment
import dev.rakrae.gameengine.math.Vec2f
import dev.rakrae.gameengine.math.Vec3f

class UvTextureFragmentShader : FragmentShader {

    override fun process(inputFragment: InputFragment): OutputFragment {
        val normalMap = inputFragment.material.normal?.bitmap
        val albedoTexture = (inputFragment.material.albedo as? BitmapTexture)?.bitmap ?: inputFragment.renderTexture

        val normalTangentSpace = normalVector(normalMap, inputFragment)
        val lightDirTangentSpace = inputFragment.shaderVariables.getVector("lightDirTangentSpace").value
        val brightness = lightingBrightness(normalTangentSpace, lightDirTangentSpace)

        val fragmentColor = if (albedoTexture != null) {
            color(albedoTexture, inputFragment) * brightness
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
            val uv = inputFragment.shaderVariables.getVector("uv").value
            val uvOffset = inputFragment.material.uvOffset
            val uvScale = inputFragment.material.uvScale

            val textureSampler = TextureSampler(TextureSampler.Filter.LINEAR, uvOffset, uvScale)
            val normalColor = textureSampler.sample(normalMap, Vec2f(uv.x, uv.y))
            return normalColor.toNormal()
        }
    }

    private fun lightingBrightness(
        normal: Vec3f,
        lightDir: Vec3f
    ): Float {
        val lightIntensity = 1f
        val illuminationAngleNormalized = (normal.normalized dot lightDir.normalized)
            .coerceIn(0f..1f)
        return (0.4f + illuminationAngleNormalized * lightIntensity).coerceIn(0f, 1f)
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

    private operator fun Color.times(value: Float): Color {
        return Color(
            (value * r.toInt()).toInt().toUByte(),
            (value * g.toInt()).toInt().toUByte(),
            (value * b.toInt()).toInt().toUByte(),
            255u
        )
    }
}
