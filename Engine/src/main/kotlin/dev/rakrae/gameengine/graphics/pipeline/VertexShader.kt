package dev.rakrae.gameengine.graphics.pipeline

import dev.rakrae.gameengine.graphics.Vertex

interface VertexShader {

    /**
     * Does arbitrary calculations to map one vertex in world space (and potentially additional
     * inputs) to a single output vertex in clip space. This is done for each vertex individually.
     */
    fun process(vertex: Vertex): Vertex
}
