package dev.rakrae.gameengine.graphics.pipeline

import dev.rakrae.gameengine.graphics.*
import dev.rakrae.gameengine.math.Mat4x4f
import dev.rakrae.gameengine.scene.Node
import dev.rakrae.gameengine.scene.Scene
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Renderer {

    private val vertexShader: VertexShader = DummyAnimationVertexShader()
    private val fragmentShader: FragmentShader = GouraudFragmentShader()
    private val rasterizer = Rasterizer()

    suspend fun render(scene: Scene, framebuffer: Bitmap) {
        val zBuffer = Buffer2f(framebuffer.width, framebuffer.height)

        withContext(Dispatchers.Default) {
            for (node in scene.nodes) {
                launch {
                    renderNode(node, framebuffer, zBuffer)
                }
            }
        }
    }

    private suspend fun renderNode(node: Node, framebuffer: Bitmap, zBuffer: Buffer2f) {
        val vertexShadedMesh = applyVertexShader(node.mesh)
        val projectedMesh = applyPerspectiveProjection(vertexShadedMesh)
        rasterizer.rasterize(node.copy(mesh = projectedMesh), framebuffer, zBuffer, fragmentShader)
    }

    private fun applyVertexShader(mesh: Mesh): Mesh {
        val processedTriangles = mesh.triangles.map { inputTriangle ->
            Triangle(
                vertexShader.process(inputTriangle.v0),
                vertexShader.process(inputTriangle.v1),
                vertexShader.process(inputTriangle.v2)
            )
        }
        return Mesh(processedTriangles)
    }

    private fun applyPerspectiveProjection(mesh: Mesh): Mesh {
        // https://en.wikipedia.org/wiki/Transformation_matrix#Perspective_projection
        val projectionMatrix = Mat4x4f(
            1f, 0f, 0.8f, 0f,
            0f, 1f, 0.8f, 0f,
            0f, 0f, 1f, 0f,
            0f, 0f, 0f, 1f
        )
        return transform(mesh, projectionMatrix)
    }

    private fun transform(mesh: Mesh, matrix: Mat4x4f): Mesh {
        val transformedTriangles = mesh.triangles.map { inputTriangle ->
            Triangle(
                inputTriangle.v0.copy(position = matrix * inputTriangle.v0.position),
                inputTriangle.v1.copy(position = matrix * inputTriangle.v1.position),
                inputTriangle.v2.copy(position = matrix * inputTriangle.v2.position)
            )
        }
        return Mesh(transformedTriangles)
    }
}
