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

    private val renderTextures = List(16) {
        Bitmap(512, 512).apply { clear(Color.black) }
    }
    private val spriteRenderer = SpriteRenderer()
    private val vertexProcessing = VertexProcessing()
    private val vertexPostProcessing = VertexPostProcessing()
    private val rasterizer = Rasterizer()
    private val imagePostProcessing = ImagePostProcessing()
    private val deferredRendering = DeferredRendering()

    suspend fun render(scene: Scene, framebuffer: Bitmap) = coroutineScope {
        for (renderTextureCamera in scene.cameras.filter { it.renderTexture != null }) {
            val renderTextureIndex = renderTextureCamera.renderTexture?.index ?: continue
            launch {
                val renderTexture = (renderTextures[renderTextureIndex]).apply { clear(Color.black) }
                render(renderTextureCamera, scene, renderTexture)
            }
        }

        for (viewportCamera in scene.cameras.filter { it.renderTexture == null }) {
            launch {
                val viewportBuffer = with(viewportCamera.viewportSize) { Bitmap(x, y, Color.black) }
                render(viewportCamera, scene, viewportBuffer)
                spriteRenderer.draw(framebuffer, viewportBuffer, viewportCamera.viewportOffset)
            }
        }
    }

    private suspend fun render(camera: Camera, scene: Scene, framebuffer: Bitmap) {
        render(
            framebuffer = framebuffer,
            viewMatrix = camera.viewMatrix,
            projectionMatrix = camera.projectionMatrix,
            viewportMatrix = camera.viewportMatrix,
            clippingPlanes = with(camera) { ClippingPlanes(nearPlane, farPlane) },
            scene = scene,
            postProcessingShaders = camera.postProcessingShaders
        )
    }

    private suspend fun render(
        framebuffer: Bitmap,
        viewMatrix: Mat4x4f,
        projectionMatrix: Mat4x4f,
        viewportMatrix: Mat4x4f,
        clippingPlanes: ClippingPlanes,
        scene: Scene,
        postProcessingShaders: List<PostProcessingShader>
    ) = coroutineScope {
        val zBuffer = Buffer2f(framebuffer.width, framebuffer.height, initValue = 1.0f)

        val renderComponents = scene.nodes.mapNotNull { it.renderComponent }
        for (renderComponent in renderComponents) {
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

        for (postProcessingShader in postProcessingShaders) {
            imagePostProcessing.postProcess(postProcessingShader, framebuffer, zBuffer)
        }

        val deferredRenderingComponents = scene.nodes
            .mapNotNull { it.renderComponent }
            .filter { it.deferredShader != null }
        renderDeferred(
            deferredRenderingComponents,
            framebuffer,
            viewMatrix,
            projectionMatrix,
            viewportMatrix,
            zBuffer,
            clippingPlanes
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

                            val renderTexture = (renderComponent.material.albedo as? RenderTexture)?.let {
                                renderTextures[it.index]
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
