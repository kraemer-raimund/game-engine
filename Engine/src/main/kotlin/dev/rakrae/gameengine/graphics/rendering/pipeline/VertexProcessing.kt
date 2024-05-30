package dev.rakrae.gameengine.graphics.rendering.pipeline

import dev.rakrae.gameengine.graphics.Mesh
import dev.rakrae.gameengine.graphics.Triangle

class VertexProcessing {

    fun process(
        triangleObjectSpace: Mesh.Triangle,
        vertexShader: VertexShader,
        shaderUniforms: ShaderUniforms
    ): VertexProcessingOutput {
        with(triangleObjectSpace) {
            val vertexShaderOutputs = listOf(v0, v1, v2)
                .map { vertexShader.process(it, shaderUniforms) }
            return VertexProcessingOutput(
                Triangle(
                    vertexShaderOutputs[0].position,
                    vertexShaderOutputs[1].position,
                    vertexShaderOutputs[2].position
                ),
                TriangleShaderVariables(
                    vertexShaderOutputs[0].shaderVariables,
                    vertexShaderOutputs[1].shaderVariables,
                    vertexShaderOutputs[2].shaderVariables
                )
            )
        }
    }
}

class VertexProcessingOutput(
    val triangleClipSpace: Triangle,
    val triangleShaderVariables: TriangleShaderVariables
)
