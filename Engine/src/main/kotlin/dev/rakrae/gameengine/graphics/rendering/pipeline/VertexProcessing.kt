package dev.rakrae.gameengine.graphics.rendering.pipeline

import dev.rakrae.gameengine.graphics.Mesh
import dev.rakrae.gameengine.graphics.Triangle
import dev.rakrae.gameengine.math.Mat4x4f
import dev.rakrae.gameengine.math.Vec3f

class VertexProcessing {

    fun process(
        triangleObjectSpace: Mesh.Triangle,
        vertexShader: VertexShader,
        projection: Mat4x4f,
        modelView: Mat4x4f,
        model: Mat4x4f,
        lightDirWorldSpace: Vec3f
    ): VertexProcessingOutput {
        with(triangleObjectSpace) {
            val vertexShaderInput = VertexShaderInput(projection, modelView, model, lightDirWorldSpace)
            val vertexShaderOutputs = listOf(v0, v1, v2)
                .map { vertexShader.process(it, vertexShaderInput) }
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
