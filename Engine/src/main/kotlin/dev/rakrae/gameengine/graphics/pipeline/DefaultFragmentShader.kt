package dev.rakrae.gameengine.graphics.pipeline

class DefaultFragmentShader : FragmentShader {

    override fun process(inputFragment: InputFragment): OutputFragment {
        // Simple pass through in the default implementation.
        // User defined shaders can handle this differently.
        return OutputFragment(
            inputFragment.interpolatedVertexColor,
            inputFragment.depth
        )
    }
}
