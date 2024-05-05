package dev.rakrae.gameengine.graphics.rendering

import dev.rakrae.gameengine.graphics.Bitmap
import dev.rakrae.gameengine.graphics.Buffer2f
import dev.rakrae.gameengine.graphics.rendering.pipeline.Rasterizer
import dev.rakrae.gameengine.graphics.rendering.pipeline.RenderContext
import dev.rakrae.gameengine.graphics.rendering.pipeline.VertexPostProcessing
import dev.rakrae.gameengine.graphics.rendering.pipeline.VertexProcessing
import dev.rakrae.gameengine.math.Vec2i
import dev.rakrae.gameengine.scene.Scene
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

internal class Renderer {

    private val vertexProcessing = VertexProcessing()
    private val vertexPostProcessing = VertexPostProcessing()
    private val rasterizer = Rasterizer()

    suspend fun render(scene: Scene, framebuffer: Bitmap) = coroutineScope {
        val zBuffer = Buffer2f(framebuffer.width, framebuffer.height, initValue = 1.0f)
        val renderComponents = scene.nodes.mapNotNull { it.renderComponent }

        for (renderComponent in renderComponents) {
            launch {
                val modelMatrix = renderComponent.transformMatrix
                val viewMatrix = scene.activeCamera.viewMatrix
                val modelViewMatrix = viewMatrix * modelMatrix
                val projectionMatrix = scene.activeCamera.projectionMatrix

                for (trianglesChunk in renderComponent.mesh.triangles.chunked(20)) {
                    launch {
                        for (triangleWorldSpace in trianglesChunk) {
                            val triangleClipSpace = vertexProcessing.process(
                                triangleWorldSpace,
                                renderComponent.vertexShader,
                                projectionMatrix,
                                modelViewMatrix
                            )

                            val triangleViewportCoordinates = vertexPostProcessing.postProcess(
                                triangleClipSpace,
                                viewportSize = Vec2i(framebuffer.width, framebuffer.height)
                            ) ?: continue

                            val renderContext = RenderContext(
                                framebuffer,
                                zBuffer,
                                wComponents = RenderContext.WComponents(
                                    triangleClipSpace.v0.position.w,
                                    triangleClipSpace.v1.position.w,
                                    triangleClipSpace.v2.position.w
                                ),
                                projectionViewModelMatrix = projectionMatrix * modelViewMatrix
                            )

                            rasterizer.rasterize(
                                triangleViewportCoordinates,
                                triangleWorldSpace.normal,
                                renderComponent.material,
                                renderComponent.fragmentShader,
                                renderContext
                            )
                        }
                    }
                }
            }
        }
    }
}
