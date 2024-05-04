package dev.rakrae.gameengine.graphics.shaders

import dev.rakrae.gameengine.graphics.pipeline.FragmentShader
import dev.rakrae.gameengine.graphics.pipeline.InputFragment
import dev.rakrae.gameengine.graphics.pipeline.OutputFragment

class UvTextureFragmentShader : FragmentShader {

    override fun process(inputFragment: InputFragment): OutputFragment {
        // Work in progress. Simply use material color for now.
        return OutputFragment(
            fragmentColor = inputFragment.material.color,
            depth = inputFragment.depth
        )
    }
}
