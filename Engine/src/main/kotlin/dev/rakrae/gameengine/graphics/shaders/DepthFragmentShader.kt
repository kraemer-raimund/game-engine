package dev.rakrae.gameengine.graphics.shaders

import dev.rakrae.gameengine.graphics.Color
import dev.rakrae.gameengine.graphics.pipeline.FragmentShader
import dev.rakrae.gameengine.graphics.pipeline.InputFragment
import dev.rakrae.gameengine.graphics.pipeline.OutputFragment

/**
 * Visualizes the depth buffer (z buffer) by assigning to each pixel a greyscale value based on
 * the depth (brighter means greater distance, darker means closer to the camera).
 */
class DepthFragmentShader : FragmentShader {

    override fun process(inputFragment: InputFragment): OutputFragment {
        val brightness = (inputFragment.depth * 255)
            .coerceIn(0f, 255f)
            .toInt().toUByte()
        val color = Color(brightness, brightness, brightness, 255u)

        return OutputFragment(
            fragmentColor = color,
            depth = inputFragment.depth
        )
    }
}
