package dev.rakrae.gameengine.graphics.rendering.shaders

import dev.rakrae.gameengine.graphics.rendering.pipeline.FragmentShader
import dev.rakrae.gameengine.graphics.rendering.pipeline.InputFragment
import dev.rakrae.gameengine.graphics.rendering.pipeline.OutputFragment

class DefaultFragmentShader : FragmentShader {

    override fun process(inputFragment: InputFragment): OutputFragment {
        return OutputFragment(
            fragmentColor = inputFragment.material.color,
            depth = inputFragment.fragPos.z
        )
    }
}
