package dev.rakrae.gameengine.graphics.pipeline

import dev.rakrae.gameengine.graphics.Bitmap
import dev.rakrae.gameengine.graphics.Mesh
import dev.rakrae.gameengine.graphics.Rasterizer
import dev.rakrae.gameengine.graphics.Triangle
import dev.rakrae.gameengine.scene.Node
import dev.rakrae.gameengine.scene.Scene
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Renderer {

    private val vertexShader: VertexShader = DummyAnimationVertexShader()
    private val rasterizer = Rasterizer()

    suspend fun render(scene: Scene, bitmap: Bitmap) {
        withContext(Dispatchers.Default) {
            for (node in scene.nodes) {
                launch {
                    renderNode(node, bitmap)
                }
            }
        }
    }

    private suspend fun renderNode(node: Node, bitmap: Bitmap) {
        val vertexShadedMesh = applyVertexShader(node.mesh)
        rasterizer.render(node.copy(mesh = vertexShadedMesh), bitmap)
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
