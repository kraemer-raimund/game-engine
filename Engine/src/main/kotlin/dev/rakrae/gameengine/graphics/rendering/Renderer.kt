package dev.rakrae.gameengine.graphics.rendering

import dev.rakrae.gameengine.graphics.Bitmap
import dev.rakrae.gameengine.graphics.Buffer2f
import dev.rakrae.gameengine.graphics.Color
import dev.rakrae.gameengine.graphics.rendering.pipeline.*
import dev.rakrae.gameengine.graphics.rendering.shaders.DepthDarkeningPostProcessingShader
import dev.rakrae.gameengine.math.Mat4x4f
import dev.rakrae.gameengine.math.Vec2i
import dev.rakrae.gameengine.scene.RenderComponent
import dev.rakrae.gameengine.scene.Scene
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

internal class Renderer {

    private val vertexProcessing = VertexProcessing()
    private val vertexPostProcessing = VertexPostProcessing()
    private val rasterizer = Rasterizer()
    private val imagePostProcessing = ImagePostProcessing()
    private val deferredRendering = DeferredRendering()

    suspend fun render(scene: Scene, displayFrame: Bitmap) = coroutineScope {
        val viewMatrix = scene.activeCamera.viewMatrix
        val projectionMatrix = scene.activeCamera.projectionMatrix
        val framebuffer = Bitmap(displayFrame.width, displayFrame.height)
        val zBuffer = Buffer2f(framebuffer.width, framebuffer.height, initValue = 1.0f)

        val renderComponents = scene.nodes.mapNotNull { it.renderComponent }
        for (renderComponent in renderComponents) {
            val modelMatrix = renderComponent.transformMatrix
            val modelViewMatrix = viewMatrix * modelMatrix
            render(renderComponent, framebuffer, zBuffer, modelViewMatrix, projectionMatrix)
        }

        imagePostProcessing.postProcess(DepthDarkeningPostProcessingShader(), framebuffer, zBuffer, displayFrame)

        val deferredRenderingComponents = scene.nodes
            .mapNotNull { it.renderComponent }
            .filter { it.deferredShader != null }
        renderDeferred(deferredRenderingComponents, displayFrame, framebuffer, viewMatrix, projectionMatrix, zBuffer)
    }

    private suspend fun render(
        renderComponent: RenderComponent,
        framebuffer: Bitmap,
        zBuffer: Buffer2f,
        modelViewMatrix: Mat4x4f,
        projectionMatrix: Mat4x4f,
    ) = coroutineScope {
        launch {
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

    private suspend fun renderDeferred(
        deferredRenderingComponents: List<RenderComponent>,
        displayFrame: Bitmap,
        framebuffer: Bitmap,
        viewMatrix: Mat4x4f,
        projectionMatrix: Mat4x4f,
        zBuffer: Buffer2f
    ) = coroutineScope {
        for (renderComponent in deferredRenderingComponents) {
            launch {
                val deferredFramebuffer = Bitmap(displayFrame.width, displayFrame.height)
                    .apply { clear(Color(0u, 0u, 0u, 0u)) }
                val deferredZBuffer = Buffer2f(
                    framebuffer.width,
                    framebuffer.height,
                    initValue = Float.POSITIVE_INFINITY
                )
                val modelMatrix = renderComponent.transformMatrix
                val modelViewMatrix = viewMatrix * modelMatrix
                render(renderComponent, deferredFramebuffer, deferredZBuffer, modelViewMatrix, projectionMatrix)
                deferredRendering.postProcess(
                    renderComponent.deferredShader!!,
                    displayFrame,
                    zBuffer,
                    deferredFramebuffer,
                    deferredZBuffer
                )
            }
        }
    }
}
