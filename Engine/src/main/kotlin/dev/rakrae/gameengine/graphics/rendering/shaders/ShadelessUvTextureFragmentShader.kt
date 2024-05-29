package dev.rakrae.gameengine.graphics.rendering.shaders

import dev.rakrae.gameengine.graphics.Bitmap
import dev.rakrae.gameengine.graphics.BitmapTexture
import dev.rakrae.gameengine.graphics.Color
import dev.rakrae.gameengine.graphics.rendering.pipeline.FragmentShader
import dev.rakrae.gameengine.graphics.rendering.pipeline.InputFragment
import dev.rakrae.gameengine.graphics.rendering.pipeline.OutputFragment

class ShadelessUvTextureFragmentShader : FragmentShader {

    override fun process(inputFragment: InputFragment): OutputFragment {
        val albedoTexture = (inputFragment.material.albedo as? BitmapTexture)?.bitmap ?: inputFragment.renderTexture
        val fragmentColor = if (albedoTexture != null) {
            color(albedoTexture, inputFragment)
        } else {
            inputFragment.material.color
        }

        return OutputFragment(
            fragmentColor = fragmentColor,
            depth = inputFragment.fragPos.z
        )
    }

    private fun color(
        texture: Bitmap?,
        inputFragment: InputFragment
    ): Color {
        return if (texture == null) {
            inputFragment.material.color
        } else {
            val uv = inputFragment.shaderVariables.getVector("uv").value
            val uvScale = inputFragment.material.uvScale
            val x = uvScale.x * uv.x * (texture.width - 1)
            val y = uvScale.y * uv.y * (texture.height - 1)
            texture.getPixel(
                x = x.toInt().mod(texture.width - 1),
                y = y.toInt().mod(texture.height - 1)
            )
        }
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
