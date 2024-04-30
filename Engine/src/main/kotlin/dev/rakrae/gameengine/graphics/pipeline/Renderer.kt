package dev.rakrae.gameengine.graphics.pipeline

import dev.rakrae.gameengine.graphics.Bitmap
import dev.rakrae.gameengine.graphics.Buffer2f
import dev.rakrae.gameengine.graphics.Mesh
import dev.rakrae.gameengine.graphics.Triangle
import dev.rakrae.gameengine.math.Mat4x4f
import dev.rakrae.gameengine.math.Vec3f
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
                val vertexShadedMesh = applyVertexShader(node.renderComponent.mesh)
                val translatedMesh = applyTranslation(vertexShadedMesh, node.renderComponent.position)
                val projectedMesh = applyPerspectiveProjection(translatedMesh)

                for (trianglesChunk in projectedMesh.triangles.chunked(100)) {
                    launch {
                        for (triangle in trianglesChunk) {
                            rasterizer.rasterize(triangle, framebuffer, zBuffer, fragmentShader)
                        }
                    }
                }
            }
        }
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

    private fun applyTranslation(mesh: Mesh, offset: Vec3f): Mesh {
        val translationMatrix = Mat4x4f(
            1f, 0f, 0f, offset.x,
            0f, 1f, 0f, offset.y,
            0f, 0f, 1f, offset.z,
            0f, 0f, 0f, 1f
        )
        return transform(mesh, translationMatrix)
    }

    private fun applyPerspectiveProjection(mesh: Mesh): Mesh {
        // https://en.wikipedia.org/wiki/Transformation_matrix#Perspective_projection
        val projectionMatrix = Mat4x4f(
            1f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f,
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
