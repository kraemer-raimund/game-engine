package dev.rakrae.gameengine.graphics.rendering.shaders

import dev.rakrae.gameengine.graphics.Color
import dev.rakrae.gameengine.graphics.rendering.pipeline.FragmentShader
import dev.rakrae.gameengine.graphics.rendering.pipeline.InputFragment
import dev.rakrae.gameengine.graphics.rendering.pipeline.OutputFragment
import kotlin.math.pow
import kotlin.random.Random

/**
 * Used during development to get some visual feedback. Will be deleted in the future, and shaders
 * provided by the game will be used instead (or the engine's default shader).
 */
class DummyExampleFragmentShader : FragmentShader {

    override fun process(inputFragment: InputFragment): OutputFragment {
        val glow = Color(
            glow(inputFragment.material.color.r, 0.4f, 2.8f),
            glow(inputFragment.material.color.g, Random.nextFloat() * 0.4f + 0.9f, 4.4f),
            glow(inputFragment.material.color.b, 0.4f, 2.8f),
            255u
        )
        return OutputFragment(
            fragmentColor = glow,
            depth = inputFragment.depth
        )
    }

    private fun glow(
        value: UByte,
        factor: Float,
        exponent: Float
    ): UByte {
        val glowValueNormalized = (value.normalized() * factor)
            .pow(exponent)
            .coerceIn(0f..1.0f)
        return (glowValueNormalized * 255).toInt().toUByte()
    }

    private fun UByte.normalized(): Float {
        return toInt() / 255f
    }
}
