package dev.rakrae.gameengine.graphics.pipeline

import dev.rakrae.gameengine.graphics.Color
import kotlin.random.Random
import kotlin.random.nextUInt

/**
 * Used during development to get some visual feedback. Will be deleted in the future, and shaders
 * provided by the game will be used instead (or the engine's default shader).
 */
class DummyExampleFragmentShader : FragmentShader {

    override fun process(fragment: Fragment): Fragment {
        return Fragment(
            screenPosition = fragment.screenPosition,
            color = Color.fromIntARGB(Random.nextUInt()),
            depth = fragment.depth
        )
    }
}
