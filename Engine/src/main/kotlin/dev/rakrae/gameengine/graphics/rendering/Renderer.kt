package dev.rakrae.gameengine.graphics.rendering

import dev.rakrae.gameengine.graphics.Bitmap
import dev.rakrae.gameengine.graphics.Buffer2f
import dev.rakrae.gameengine.graphics.Color
import dev.rakrae.gameengine.graphics.RenderTexture
import dev.rakrae.gameengine.graphics.rendering.pipeline.*
import dev.rakrae.gameengine.math.Mat4x4f
import dev.rakrae.gameengine.scene.Camera
import dev.rakrae.gameengine.scene.RenderComponent
import dev.rakrae.gameengine.scene.Scene
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

internal class Renderer {

    private var renderTexturesCurrentFrame = List(16) {
        Bitmap(512, 512).apply { clear(Color.black) }
    }
    private var renderTexturesNextFrame = List(16) {
        Bitmap(512, 512).apply { clear(Color.black) }
    }
    private val spriteRenderer = SpriteRenderer()
    private val vertexProcessing = VertexProcessing()
    private val vertexPostProcessing = VertexPostProcessing()
    private val rasterizer = Rasterizer()
    private val imagePostProcessing = ImagePostProcessing()
    private val deferredRendering = DeferredRendering()

    suspend fun render(scene: Scene, framebuffer: Bitmap) {
        coroutineScope {
            for (camera in scene.cameras) {
                launch {
                    val renderTexture = camera.renderTexture
                    if (renderTexture != null) {
                        render(camera, scene, renderTexturesNextFrame[renderTexture.index].apply { clear(Color.black) })
                    } else {
                        val viewportBuffer = with(camera.viewportSize) { Bitmap(x, y, Color.black) }
                        render(camera, scene, viewportBuffer)
                        spriteRenderer.draw(framebuffer, viewportBuffer, camera.viewportOffset)
                    }
                }
            }
        }

        renderTexturesCurrentFrame = renderTexturesNextFrame
        renderTexturesNextFrame = List(16) {
            Bitmap(512, 512).apply { clear(Color.black) }
        }
    }

    private suspend fun render(camera: Camera, scene: Scene, framebuffer: Bitmap) = coroutineScope {
        val viewMatrix = camera.viewMatrix
        val projectionMatrix = camera.projectionMatrix
        val viewportMatrix = camera.viewportMatrix
        val clippingPlanes = ClippingPlanes(camera.nearPlane, camera.farPlane)
        val postProcessingShaders = camera.postProcessingShaders

        val zBuffer = Buffer2f(framebuffer.width, framebuffer.height, initValue = 1.0f)

        coroutineScope {
            val renderComponents = scene.nodes.mapNotNull { it.renderComponent }
            for (renderComponent in renderComponents) {
                launch {
                    val modelMatrix = renderComponent.transformMatrix
                    val modelViewMatrix = viewMatrix * modelMatrix
                    render(
                        renderComponent,
                        framebuffer,
                        zBuffer,
                        modelViewMatrix,
                        projectionMatrix,
                        viewportMatrix,
                        clippingPlanes
                    )
                }
            }
        }

        for (postProcessingShader in postProcessingShaders) {
            imagePostProcessing.postProcess(postProcessingShader, framebuffer, zBuffer)
        }

        coroutineScope {
            val deferredRenderingComponents = scene.nodes
                .mapNotNull { it.renderComponent }
                .filter { it.deferredShader != null }
            for (renderComponent in deferredRenderingComponents) {
                launch {
                    val modelMatrix = renderComponent.transformMatrix
                    val modelViewMatrix = viewMatrix * modelMatrix
                    val deferredFramebuffer = Bitmap(framebuffer.width, framebuffer.height)
                        .apply { clear(Color(0u, 0u, 0u, 0u)) }
                    val deferredZBuffer = Buffer2f(
                        framebuffer.width,
                        framebuffer.height,
                        initValue = Float.POSITIVE_INFINITY
                    )
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

    private suspend fun render(
        renderComponent: RenderComponent,
        framebuffer: Bitmap,
        zBuffer: Buffer2f,
        modelViewMatrix: Mat4x4f,
        projectionMatrix: Mat4x4f,
        viewportMatrix: Mat4x4f,
        clippingPlanes: ClippingPlanes
    ) = coroutineScope {
        for (trianglesChunk in renderComponent.mesh.triangles.chunked(200)) {
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
                        launch {
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

                            val renderTexture = (renderComponent.material.albedo as? RenderTexture)?.let {
                                renderTexturesCurrentFrame[it.index]
                            }
                            rasterizer.rasterize(
                                triangleViewportCoordinates,
                                triangleObjectSpace.normal,
                                renderComponent.material,
                                renderTexture,
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
