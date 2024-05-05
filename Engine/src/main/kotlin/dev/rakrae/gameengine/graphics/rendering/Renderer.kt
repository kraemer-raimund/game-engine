package dev.rakrae.gameengine.graphics.rendering

import dev.rakrae.gameengine.graphics.Bitmap
import dev.rakrae.gameengine.graphics.Buffer2f
import dev.rakrae.gameengine.graphics.Color
import dev.rakrae.gameengine.graphics.rendering.pipeline.*
import dev.rakrae.gameengine.graphics.rendering.shaders.OutlinePostProcessingShader
import dev.rakrae.gameengine.math.Vec2i
import dev.rakrae.gameengine.scene.Scene
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

internal class Renderer {

    private val vertexProcessing = VertexProcessing()
    private val vertexPostProcessing = VertexPostProcessing()
    private val rasterizer = Rasterizer()
    private val imagePostProcessing = ImagePostProcessing()

    suspend fun render(scene: Scene, displayFrame: Bitmap) = coroutineScope {
        val framebuffer = Bitmap(displayFrame.width, displayFrame.height)
        val zBuffer = Buffer2f(framebuffer.width, framebuffer.height, initValue = 1.0f)
        renderImage(scene, framebuffer, zBuffer)

        val postProcessingShader = OutlinePostProcessingShader(
            thickness = 2,
            threshold = 0.2f,
            outlineColor = Color(255u, 255u, 0u, 255u)
        )
        imagePostProcessing.postProcess(postProcessingShader, framebuffer, zBuffer, displayFrame)
    }

    private suspend fun renderImage(
        scene: Scene,
        framebuffer: Bitmap,
        zBuffer: Buffer2f
    ) = coroutineScope {
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
