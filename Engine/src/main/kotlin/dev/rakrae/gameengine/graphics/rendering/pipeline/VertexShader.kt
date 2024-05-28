package dev.rakrae.gameengine.graphics.rendering.pipeline

import dev.rakrae.gameengine.graphics.Vertex
import dev.rakrae.gameengine.math.Vec3f
import dev.rakrae.gameengine.math.Vec4f

interface VertexShader {

    /**
     * Does arbitrary calculations to map one vertex in object space (and additional inputs) to a
     * single output vertex in clip space. This is done for each vertex individually.
     *
     * @param position The vertex position in object space.
     * @param inputs The additional inputs for the vertex shader. At a minimum, this includes
     * the necessary matrices for transforming into clip space, but optionally user-defined
     * inputs can be passed as well.
     */
    fun process(vertex: Vertex, inputs: VertexShaderInputs): VertexShaderOutputs
}

class VertexShaderOutputs(
    val position: Vec4f,
    val lightDirTangentSpace: Vec3f? = null,
    val shaderVariables: ShaderVariables = ShaderVariables()
)
