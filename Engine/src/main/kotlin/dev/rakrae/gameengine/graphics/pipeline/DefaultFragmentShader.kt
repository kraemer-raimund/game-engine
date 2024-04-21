package dev.rakrae.gameengine.graphics.pipeline

class DefaultFragmentShader : FragmentShader {

    override fun process(fragment: Fragment): Fragment {
        // Simple pass through in the default implementation.
        // User defined shaders can handle this differently.
        return fragment
    }
}
