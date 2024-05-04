package dev.rakrae.gameengine.graphics.shaders

import dev.rakrae.gameengine.graphics.pipeline.FragmentShader
import dev.rakrae.gameengine.graphics.pipeline.InputFragment
import dev.rakrae.gameengine.graphics.pipeline.OutputFragment

class UvTextureFragmentShader : FragmentShader {

    override fun process(inputFragment: InputFragment): OutputFragment {
        val texture = inputFragment.material.texture?.bitmap
        val color = if (texture == null) {
            inputFragment.material.color
        } else {
            val uv = inputFragment.uv
            texture.getPixel(
                x = (uv.x * texture.width).toInt(),
                y = (uv.y * texture.height).toInt()
            )
        }
        return OutputFragment(
            fragmentColor = color,
            depth = inputFragment.depth
        )
    }
}
