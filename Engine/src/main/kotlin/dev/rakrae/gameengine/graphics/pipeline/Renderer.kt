package dev.rakrae.gameengine.graphics.pipeline

import dev.rakrae.gameengine.graphics.Bitmap
import dev.rakrae.gameengine.graphics.Buffer2f
import dev.rakrae.gameengine.graphics.Mesh
import dev.rakrae.gameengine.graphics.Triangle
import dev.rakrae.gameengine.math.Mat4x4f
import dev.rakrae.gameengine.math.Vec2i
import dev.rakrae.gameengine.math.Vec3f
import dev.rakrae.gameengine.math.Vec4f
import dev.rakrae.gameengine.scene.Scene
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.acos

class Renderer {

    private val rasterizer = Rasterizer()

    suspend fun render(scene: Scene, framebuffer: Bitmap) = coroutineScope {
        val zBuffer = Buffer2f(framebuffer.width, framebuffer.height, initValue = 1.0f)
        val renderComponents = scene.nodes.mapNotNull { it.renderComponent }

        for (renderComponent in renderComponents) {
            launch {
                val vertexShadedMesh = applyVertexShader(
                    renderComponent.mesh,
                    renderComponent.vertexShader
                )

                val modelMatrix = renderComponent.transformMatrix
                val viewMatrix = scene.activeCamera.viewMatrix
                val projectionMatrix = scene.activeCamera.projectionMatrix

                val finalMatrix = projectionMatrix * viewMatrix * modelMatrix

                for (trianglesChunk in vertexShadedMesh.triangles.chunked(20)) {
                    launch {
                        val trianglesClipSpace = trianglesChunk.map { transform(it, finalMatrix) }
                        val trianglesNormalizedDeviceCoordinates = trianglesClipSpace.map(::applyPerspectiveDivide)
                        val trianglesInViewFrustum = trianglesNormalizedDeviceCoordinates.filter(::isInsideViewFrustum)
                        val frontFacesInViewFrustum = trianglesInViewFrustum.filter { isFrontFace(it) }

                        for (triangle in frontFacesInViewFrustum) {
                            val screenSize = Vec2i(framebuffer.width, framebuffer.height)
                            val viewportCoordinates = viewportTransform(triangle, screenSize)
                            rasterizer.rasterize(
                                viewportCoordinates,
                                triangle.normal,
                                framebuffer,
                                zBuffer,
                                renderComponent.material,
                                renderComponent.fragmentShader
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

    private fun applyPerspectiveDivide(triangle: Triangle): Triangle {
        return Triangle(
            triangle.v0.copy(position = applyPerspectiveDivide(triangle.v0.position)),
            triangle.v1.copy(position = applyPerspectiveDivide(triangle.v1.position)),
            triangle.v2.copy(position = applyPerspectiveDivide(triangle.v2.position))
        )
    }

    private fun applyPerspectiveDivide(vector: Vec4f): Vec4f {
        return Vec4f(
            vector.x / vector.w,
            vector.y / vector.w,
            vector.z / vector.w,
            1f
        )
    }

    private fun viewportTransform(triangle: Triangle, screenSize: Vec2i): Triangle {
        return Triangle(
            triangle.v0.copy(position = viewportTransform(triangle.v0.position, screenSize)),
            triangle.v1.copy(position = viewportTransform(triangle.v1.position, screenSize)),
            triangle.v2.copy(position = viewportTransform(triangle.v2.position, screenSize))
        )
    }

    private fun viewportTransform(vector: Vec4f, screenSize: Vec2i): Vec4f {
        return Vec4f(
            0.5f * screenSize.x + (vector.x * 0.5f * screenSize.x),
            0.5f * screenSize.y + (vector.y * 0.5f * screenSize.y),
            vector.z,
            1f
        )
    }

    private fun isInsideViewFrustum(triangleClipSpace: Triangle): Boolean {
        val vertices = with(triangleClipSpace) { listOf(v0, v1, v2) }
        val vertexPositions = vertices.map { it.position }
        return vertexPositions.any { position ->
            position.x in -1f..1f
                    && position.y in -1f..1f
                    && position.z in -1f..1f
        }
    }

    /**
     * True if the polygon's front face is oriented towards the camera.
     * If back face culling is desired/enabled, the polygon will only be rendered if this is true.
     */
    private fun isFrontFace(triangleClipSpace: Triangle): Boolean {
        val n = triangleClipSpace.normal.normalized
        val view = Vec3f(0f, 0f, 1f)
        val angleRad = acos(view dot n)
        return angleRad < 0.5f * PI
    }
}
