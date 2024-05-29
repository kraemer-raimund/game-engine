package dev.rakrae.gameengine.graphics.rendering.shaders

import dev.rakrae.gameengine.graphics.Color
import dev.rakrae.gameengine.graphics.rendering.pipeline.FragmentShader
import dev.rakrae.gameengine.graphics.rendering.pipeline.InputFragment
import dev.rakrae.gameengine.graphics.rendering.pipeline.OutputFragment
import dev.rakrae.gameengine.math.Vec3f

class DefaultFragmentShader : FragmentShader {

    override fun process(inputFragment: InputFragment): OutputFragment {
        val lightDirection = Vec3f(1f, -1f, 1f)
        val illuminationAngleNormalized = (inputFragment.faceNormalWorldSpace.normalized dot lightDirection.normalized)
            .coerceIn(0f..1f)
        val brightness = 0.4f + 0.4f * illuminationAngleNormalized
        val color = with(Color(255u, 255u, 255u, 255u)) {
            Color(
                (brightness * r.toInt()).toInt().toUByte(),
                (brightness * g.toInt()).toInt().toUByte(),
                (brightness * b.toInt()).toInt().toUByte(),
                255u
            )
        }

        return OutputFragment(
            fragmentColor = color,
            depth = inputFragment.fragPos.z
        )
    }
}
