package dev.rakrae.gameengine.graphics.rendering.pipeline

import dev.rakrae.gameengine.graphics.Triangle
import dev.rakrae.gameengine.math.Mat4x4f
import dev.rakrae.gameengine.math.Vec3f

class VertexProcessing {

    fun process(
        triangleObjectSpace: Triangle,
        vertexShader: VertexShader,
        projection: Mat4x4f,
        modelView: Mat4x4f,
        model: Mat4x4f,
        lightDirWorldSpace: Vec3f
    ): VertexProcessingOutput {
        with(triangleObjectSpace) {
            val vertexShaderInputs = VertexShaderInputs(projection, modelView, model, lightDirWorldSpace)
            val vertexShaderOutputs = listOf(v0, v1, v2)
                .map { vertexShader.process(it, vertexShaderInputs) }
            return VertexProcessingOutput(
                Triangle(
                    v0.copy(position = vertexShaderOutputs[0].position),
                    v1.copy(position = vertexShaderOutputs[1].position),
                    v2.copy(position = vertexShaderOutputs[2].position)
                ),
                listOf(
                    vertexShaderOutputs[0],
                    vertexShaderOutputs[1],
                    vertexShaderOutputs[2]
                )
            )
        }
    }
}

class VertexProcessingOutput(
    val triangleClipSpace: Triangle,
    val vertexShaderOutputs: List<VertexShaderOutputs>
)
