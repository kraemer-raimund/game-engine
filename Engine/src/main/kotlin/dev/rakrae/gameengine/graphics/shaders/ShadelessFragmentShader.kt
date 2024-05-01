package dev.rakrae.gameengine.graphics.shaders

import dev.rakrae.gameengine.graphics.pipeline.FragmentShader
import dev.rakrae.gameengine.graphics.pipeline.InputFragment
import dev.rakrae.gameengine.graphics.pipeline.OutputFragment

class ShadelessFragmentShader : FragmentShader {

    override fun process(inputFragment: InputFragment): OutputFragment {
        return OutputFragment(
            inputFragment.material.color,
            inputFragment.depth
        )
    }
}
