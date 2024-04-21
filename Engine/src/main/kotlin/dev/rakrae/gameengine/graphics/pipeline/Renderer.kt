package dev.rakrae.gameengine.graphics.pipeline

import dev.rakrae.gameengine.graphics.*
import dev.rakrae.gameengine.scene.Node
import dev.rakrae.gameengine.scene.Scene
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Renderer {

    private val vertexShader: VertexShader = DummyAnimationVertexShader()
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
        rasterizer.render(node.copy(mesh = vertexShadedMesh), framebuffer, zBuffer)
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
}
