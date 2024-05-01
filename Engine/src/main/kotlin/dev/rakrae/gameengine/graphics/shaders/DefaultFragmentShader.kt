package dev.rakrae.gameengine.graphics.shaders

import dev.rakrae.gameengine.graphics.pipeline.FragmentShader
import dev.rakrae.gameengine.graphics.pipeline.InputFragment
import dev.rakrae.gameengine.graphics.pipeline.OutputFragment

class DefaultFragmentShader : FragmentShader {

    override fun process(inputFragment: InputFragment): OutputFragment {
        // Simple pass through in the default implementation.
        // User defined shaders can handle this differently.
        return OutputFragment(
            inputFragment.material.color,
            inputFragment.depth
        )
    }
}
