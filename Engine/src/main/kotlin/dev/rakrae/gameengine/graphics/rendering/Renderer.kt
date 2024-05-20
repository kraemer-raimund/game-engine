package dev.rakrae.gameengine.graphics.rendering

import dev.rakrae.gameengine.graphics.Bitmap
import dev.rakrae.gameengine.graphics.Buffer2f
import dev.rakrae.gameengine.graphics.Color
import dev.rakrae.gameengine.graphics.rendering.pipeline.*
import dev.rakrae.gameengine.math.Mat4x4f
import dev.rakrae.gameengine.scene.Camera
import dev.rakrae.gameengine.scene.RenderComponent
import dev.rakrae.gameengine.scene.Scene
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.time.measureTime

internal class Renderer {

    private val vertexProcessing = VertexProcessing()
    private val vertexPostProcessing = VertexPostProcessing()
    private val rasterizer = Rasterizer()
    private val imagePostProcessing = ImagePostProcessing()
    private val deferredRendering = DeferredRendering()

    private val postProcessingShader: PostProcessingShader? = null

    suspend fun render(
        camera: Camera,
        scene: Scene
    ) = coroutineScope {
        val frameBuffer = camera.renderBuffer
        frameBuffer.clear(Color(0u, 0u, 0u, 255u))
        val viewMatrix = camera.viewMatrix
        val projectionMatrix = camera.projectionMatrix
        val viewportMatrix = camera.viewportMatrix
        val clippingPlanes = with(camera) { ClippingPlanes(nearPlane, farPlane) }
        val zBuffer = Buffer2f(frameBuffer.width, frameBuffer.height, initValue = 1.0f)

        val renderingTime = measureTime {
            val renderComponents = scene.nodes.mapNotNull { it.renderComponent }
            for (renderComponent in renderComponents) {
                val modelMatrix = renderComponent.transformMatrix
                val modelViewMatrix = viewMatrix * modelMatrix
                render(
                    renderComponent,
                    frameBuffer,
                    zBuffer,
                    modelViewMatrix,
                    projectionMatrix,
                    viewportMatrix,
                    clippingPlanes
                )
            }
        }

        val imagePostProcessingTime = measureTime {
            if (postProcessingShader != null) {
                imagePostProcessing.postProcess(postProcessingShader, frameBuffer, zBuffer)
            }
        }

        val deferredRenderingTime = measureTime {
            val deferredRenderingComponents = scene.nodes
                .mapNotNull { it.renderComponent }
                .filter { it.deferredShader != null }
            renderDeferred(
                deferredRenderingComponents,
                frameBuffer,
                viewMatrix,
                projectionMatrix,
                viewportMatrix,
                zBuffer,
                clippingPlanes
            )
        }

        println(
            "Rendering time: $renderingTime\n" +
                    "Image post processing time: $imagePostProcessingTime\n" +
                    "Deferred rendering time: $deferredRenderingTime"
        )
    }

    private suspend fun render(
        renderComponent: RenderComponent,
        framebuffer: Bitmap,
        zBuffer: Buffer2f,
        modelViewMatrix: Mat4x4f,
        projectionMatrix: Mat4x4f,
        viewportMatrix: Mat4x4f,
        clippingPlanes: ClippingPlanes
    ) = coroutineScope {
        launch {
            for (trianglesChunk in renderComponent.mesh.triangles.chunked(20)) {
                launch {
                    for (triangleObjectSpace in trianglesChunk) {
                        val clipSpace = vertexProcessing.process(
                            triangleObjectSpace,
                            renderComponent.vertexShader,
                            projectionMatrix,
                            modelViewMatrix
                        )
                        val clippedTriangles = vertexPostProcessing.clip(clipSpace, clippingPlanes)

                        clippedTriangles.forEach { triangleClipSpace ->
                            val triangleViewportCoordinates = vertexPostProcessing.toViewport(
                                triangleClipSpace,
                                viewportMatrix
                            ) ?: return@forEach
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
                                triangleObjectSpace.normal,
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

    private suspend fun renderDeferred(
        deferredRenderingComponents: List<RenderComponent>,
        framebuffer: Bitmap,
        viewMatrix: Mat4x4f,
        projectionMatrix: Mat4x4f,
        viewportMatrix: Mat4x4f,
        zBuffer: Buffer2f,
        clippingPlanes: ClippingPlanes
    ) = coroutineScope {
        for (renderComponent in deferredRenderingComponents) {
            launch {
                val deferredFramebuffer = Bitmap(framebuffer.width, framebuffer.height)
                    .apply { clear(Color(0u, 0u, 0u, 0u)) }
                val deferredZBuffer = Buffer2f(
                    framebuffer.width,
                    framebuffer.height,
                    initValue = Float.POSITIVE_INFINITY
                )
                val modelMatrix = renderComponent.transformMatrix
                val modelViewMatrix = viewMatrix * modelMatrix
                render(
                    renderComponent,
                    deferredFramebuffer,
                    deferredZBuffer,
                    modelViewMatrix,
                    projectionMatrix,
                    viewportMatrix,
                    clippingPlanes
                )
                deferredRendering.postProcess(
                    renderComponent.deferredShader!!,
                    framebuffer,
                    zBuffer,
                    deferredFramebuffer,
                    deferredZBuffer
                )
            }
        }
    }
}
