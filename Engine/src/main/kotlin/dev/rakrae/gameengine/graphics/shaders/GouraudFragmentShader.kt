package dev.rakrae.gameengine.graphics.shaders

import dev.rakrae.gameengine.graphics.Color
import dev.rakrae.gameengine.graphics.pipeline.FragmentShader
import dev.rakrae.gameengine.graphics.pipeline.InputFragment
import dev.rakrae.gameengine.graphics.pipeline.OutputFragment
import dev.rakrae.gameengine.math.Vec3f
import kotlin.math.pow

/**
 * https://en.wikipedia.org/wiki/Gouraud_shading
 */
class GouraudFragmentShader : FragmentShader {

    override fun process(inputFragment: InputFragment): OutputFragment {
        val lightDirection = Vec3f(0.2f, 0f, 0.6f)
        val lightIntensity = 0.9f
        val illuminationAngleNormalized = (inputFragment.interpolatedNormal.normalized dot lightDirection.normalized)
            .coerceIn(0f..1f)
        val brightness =
            0.2f + 0.8f * illuminationAngleNormalized.pow(inputFragment.material.glossiness) * lightIntensity
        val color = with(inputFragment.material.color) {
            Color(
                (brightness * r.toInt()).toInt().toUByte(),
                (brightness * g.toInt()).toInt().toUByte(),
                (brightness * b.toInt()).toInt().toUByte(),
                255u
            )
        }

        return OutputFragment(
            fragmentColor = color,
            depth = inputFragment.depth
        )
    }
}
