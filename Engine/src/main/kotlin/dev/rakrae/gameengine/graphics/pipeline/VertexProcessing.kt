package dev.rakrae.gameengine.graphics.pipeline

import dev.rakrae.gameengine.graphics.Triangle
import dev.rakrae.gameengine.math.Mat4x4f

class VertexProcessing {

    fun process(
        triangleWorldSpace: Triangle,
        vertexShader: VertexShader,
        projection: Mat4x4f,
        modelView: Mat4x4f
    ): Triangle {
        val triangleVertexShaded = triangleWorldSpace.let { t ->
            Triangle(
                vertexShader.process(t.v0),
                vertexShader.process(t.v1),
                vertexShader.process(t.v2)
            )
        }
        val matrix = projection * modelView
        return transform(triangleVertexShaded, matrix)
    }

    private fun transform(triangle: Triangle, matrix: Mat4x4f): Triangle {
        return Triangle(
            triangle.v0.copy(position = matrix * triangle.v0.position),
            triangle.v1.copy(position = matrix * triangle.v1.position),
            triangle.v2.copy(position = matrix * triangle.v2.position)
        )
    }
}
