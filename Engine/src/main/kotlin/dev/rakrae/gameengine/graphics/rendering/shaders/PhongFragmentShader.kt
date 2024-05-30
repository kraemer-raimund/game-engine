package dev.rakrae.gameengine.graphics.rendering.shaders

import dev.rakrae.gameengine.graphics.Color
import dev.rakrae.gameengine.graphics.rendering.pipeline.FragmentShader
import dev.rakrae.gameengine.graphics.rendering.pipeline.InputFragment
import dev.rakrae.gameengine.graphics.rendering.pipeline.OutputFragment
import dev.rakrae.gameengine.math.Vec3f
import kotlin.math.pow

class PhongFragmentShader : FragmentShader {

    override fun process(inputFragment: InputFragment): OutputFragment {
        val normal = Vec3f(0f, 0f, 1f)
        val lightDir = inputFragment.shaderVariables.getVector("lightDirTangentSpace").value
        val lightIntensity = 0.9f
        val illuminationAngleNormalized = (normal dot lightDir.normalized)
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
            depth = inputFragment.fragPos.z
        )
    }
}
