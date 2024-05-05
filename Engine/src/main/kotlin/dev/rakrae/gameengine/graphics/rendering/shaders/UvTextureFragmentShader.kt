package dev.rakrae.gameengine.graphics.rendering.shaders

import dev.rakrae.gameengine.graphics.rendering.pipeline.FragmentShader
import dev.rakrae.gameengine.graphics.rendering.pipeline.InputFragment
import dev.rakrae.gameengine.graphics.rendering.pipeline.OutputFragment

class UvTextureFragmentShader : FragmentShader {

    override fun process(inputFragment: InputFragment): OutputFragment {
        val texture = inputFragment.material.texture?.bitmap
        val color = if (texture == null) {
            inputFragment.material.color
        } else {
            val uv = inputFragment.uv
            texture.getPixel(
                x = (uv.x * (texture.width - 1)).toInt().coerceIn(0, texture.width - 1),
                y = (uv.y * (texture.height - 1)).toInt().coerceIn(0, texture.height - 1)
            )
        }
        return OutputFragment(
            fragmentColor = color,
            depth = inputFragment.depth
        )
    }
}
