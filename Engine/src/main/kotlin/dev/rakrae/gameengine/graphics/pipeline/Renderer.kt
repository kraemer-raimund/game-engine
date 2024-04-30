package dev.rakrae.gameengine.graphics.pipeline

import dev.rakrae.gameengine.graphics.*
import dev.rakrae.gameengine.math.Mat4x4f
import dev.rakrae.gameengine.scene.Scene
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Renderer {

    private val rasterizer = Rasterizer()

    suspend fun render(scene: Scene, framebuffer: Bitmap) {
        val zBuffer = Buffer2f(framebuffer.width, framebuffer.height)

        withContext(Dispatchers.Default) {
            for (node in scene.nodes) {
                val vertexShadedMesh = applyVertexShader(
                    node.renderComponent.mesh,
                    node.renderComponent.material.vertexShader
                )

                val modelMatrix = node.renderComponent.translationMatrix
                val viewMatrix = scene.activeCamera.viewMatrix
                val projectionMatrix = scene.activeCamera.projectionMatrix

                val finalMatrix = projectionMatrix * viewMatrix * modelMatrix

                for (trianglesChunk in vertexShadedMesh.triangles.chunked(20)) {
                    launch {
                        for (triangle in trianglesChunk) {
                            val projectedTriangle = transform(triangle, finalMatrix)
                            val color = Color(255u, 255u, 255u, 255u)
                            rasterizer.rasterize(
                                projectedTriangle,
                                color,
                                framebuffer,
                                zBuffer,
                                node.renderComponent.material.fragmentShader
                            )
                        }
                    }
                }
            }
        }
    }

    private fun applyVertexShader(mesh: Mesh, vertexShader: VertexShader): Mesh {
        val processedTriangles = mesh.triangles.map { inputTriangle ->
            Triangle(
                vertexShader.process(inputTriangle.v0),
                vertexShader.process(inputTriangle.v1),
                vertexShader.process(inputTriangle.v2)
            )
        }
        return Mesh(processedTriangles)
    }

    private fun transform(triangle: Triangle, matrix: Mat4x4f): Triangle {
        return Triangle(
            triangle.v0.copy(position = matrix * triangle.v0.position),
            triangle.v1.copy(position = matrix * triangle.v1.position),
            triangle.v2.copy(position = matrix * triangle.v2.position)
        )
    }

}
