package dev.rakrae.gameengine.graphics.pipeline

import dev.rakrae.gameengine.graphics.Vertex

interface VertexShader {

    /**
     * Does arbitrary calculations to map one vertex (and potentially additional inputs) to a
     * single output vertex. This is done for each vertex separately.
     */
    fun process(vertex: Vertex): Vertex
}
