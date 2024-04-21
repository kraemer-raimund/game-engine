package dev.rakrae.gameengine.graphics.pipeline

import dev.rakrae.gameengine.graphics.Color
import kotlin.math.pow
import kotlin.random.Random

/**
 * Used during development to get some visual feedback. Will be deleted in the future, and shaders
 * provided by the game will be used instead (or the engine's default shader).
 */
class DummyExampleFragmentShader : FragmentShader {

    override fun process(fragment: Fragment): Fragment {
        val glow = Color(
            glow(fragment.color.r, 0.4f, 2.8f),
            glow(fragment.color.g, Random.nextFloat() * 0.4f + 0.9f, 4.4f),
            glow(fragment.color.b, 0.4f, 2.8f),
            255u
        )
        return Fragment(
            screenPosition = fragment.screenPosition,
            color = glow,
            depth = fragment.depth
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
