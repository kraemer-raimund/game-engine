package dev.rakrae.gameengine.graphics.pipeline

interface FragmentShader {

    /**
     * Does arbitrary calculations to map one fragment (and potentially additional inputs) to a
     * single output fragment. This is done for each fragment individually.
     *
     * Each fragment typically
     * represents a single pixel's attributes (like color and depth), although in some cases
     * multiple fragments might be associated with a single pixel, like in the case of
     * multisampling.
     */
    fun process(fragment: Fragment): Fragment
}
