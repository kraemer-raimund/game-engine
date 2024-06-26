package dev.rakrae.gameengine.graphics.rendering

import dev.rakrae.gameengine.graphics.*
import dev.rakrae.gameengine.graphics.rendering.pipeline.*
import dev.rakrae.gameengine.math.Mat4x4f
import dev.rakrae.gameengine.scene.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

internal class Renderer {

    private var renderTextures = List(4) {
        DoubleBufferedBitmap(512, 512, Color.black)
    }
    private val spriteRenderer = SpriteRenderer()
    private val vertexProcessing = VertexProcessing()
    private val vertexPostProcessing = VertexPostProcessing()
    private val rasterizer = Rasterizer()
    private val imagePostProcessing = ImagePostProcessing()
    private val deferredRendering = DeferredRendering()

    suspend fun render(scene: Scene, framebuffer: Bitmap) {
        coroutineScope {
            val renderedImages = scene.cameras.map { camera ->
                async {
                    val renderTexture = camera.renderTexture
                    if (renderTexture != null) {
                        val renderTextureBuffer = renderTextures[renderTexture.index]
                            .apply { clearBackBuffer(Color.black) }
                            .backBuffer
                        render(camera, scene, renderTextureBuffer)
                        return@async null
                    } else {
                        val viewportBuffer = with(camera.viewportSize) { Bitmap(x, y, Color.black) }
                        render(camera, scene, viewportBuffer)
                        return@async Pair(viewportBuffer, camera)
                    }
                }
            }
            renderedImages.awaitAll().filterNotNull().forEach { (viewportBuffer, camera) ->
                spriteRenderer.draw(framebuffer, viewportBuffer, camera.viewportOffset)
            }
        }

        renderTextures.forEach(DoubleBufferedBitmap::swap)
    }

    private suspend fun render(camera: Camera, scene: Scene, framebuffer: Bitmap) {
        val viewMatrix = camera.viewMatrix
        val projectionMatrix = camera.projectionMatrix
        val viewportMatrix = camera.viewportMatrix
        val clippingPlanes = ClippingPlanes(camera.nearPlane, camera.farPlane)
        val postProcessingShaders = camera.postProcessingShaders

        val zBuffer = Buffer2f(framebuffer.width, framebuffer.height, initValue = 1.0f)

        coroutineScope {
            val renderComponents = scene.nodes
                .filterIsInstance<MeshNode>()
                .map { it.renderComponent }
            val lights = scene.nodes.filterIsInstance<Light>()

            for (renderComponent in renderComponents) {
                launch {
                    val modelMatrix = renderComponent.transformMatrix
                    val modelViewMatrix = viewMatrix * modelMatrix
                    val shaderUniforms = ShaderUniforms(
                        builtinMatrixMVP = projectionMatrix * modelViewMatrix,
                        builtinMatrixMV = modelViewMatrix,
                        builtinMatrixV = viewMatrix,
                        builtinMatrixP = projectionMatrix,
                        builtinMatrixVP = projectionMatrix * viewMatrix,
                        builtinMatrixM = modelMatrix,
                        cameraPosWorld = camera.worldPos.toVec4(),
                        cameraRotWorld = camera.worldRot.toVec4(),
                        sunLightDirection = scene.sunLightDirection.toVec4(0f),
                        ambientColor = scene.environmentAttributes.ambientColor,
                        ambientIntensityMultiplier = scene.environmentAttributes.ambientIntensityMultiplier
                    )
                    lights.forEach { light ->
                        shaderUniforms.setVector(
                            ShaderUniforms.BuiltinKeys.POINT_LIGHT_0_POSITION,
                            light.transform.position.toVec4()
                        )
                    }

                    render(
                        renderComponent,
                        framebuffer,
                        zBuffer,
                        viewportMatrix,
                        clippingPlanes,
                        shaderUniforms
                    )
                }
            }
        }

        for (postProcessingShader in postProcessingShaders) {
            imagePostProcessing.postProcess(postProcessingShader, framebuffer, zBuffer)
        }

        coroutineScope {
            val deferredRenderingComponents = scene.nodes
                .filterIsInstance<MeshNode>()
                .map { it.renderComponent }
                .filter { it.deferredShader != null }
            val lights = scene.nodes.filterIsInstance<Light>()

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
                    val shaderUniforms = ShaderUniforms(
                        builtinMatrixMVP = projectionMatrix * modelViewMatrix,
                        builtinMatrixMV = modelViewMatrix,
                        builtinMatrixV = viewMatrix,
                        builtinMatrixP = projectionMatrix,
                        builtinMatrixVP = projectionMatrix * viewMatrix,
                        builtinMatrixM = modelMatrix,
                        cameraPosWorld = camera.worldPos.toVec4(),
                        cameraRotWorld = camera.worldRot.toVec4(),
                        sunLightDirection = scene.sunLightDirection.toVec4(0f),
                        ambientColor = scene.environmentAttributes.ambientColor,
                        ambientIntensityMultiplier = scene.environmentAttributes.ambientIntensityMultiplier
                    )
                    lights.forEach { light ->
                        shaderUniforms.setVector(
                            ShaderUniforms.BuiltinKeys.POINT_LIGHT_0_POSITION,
                            light.transform.position.toVec4()
                        )
                    }

                    render(
                        renderComponent,
                        deferredFramebuffer,
                        deferredZBuffer,
                        viewportMatrix,
                        clippingPlanes,
                        shaderUniforms
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

    private fun render(
        renderComponent: RenderComponent,
        framebuffer: Bitmap,
        zBuffer: Buffer2f,
        viewportMatrix: Mat4x4f,
        clippingPlanes: ClippingPlanes,
        shaderUniforms: ShaderUniforms
    ) {
        for (triangleObjectSpace in renderComponent.mesh.triangles) {
            val vertexProcessingOutput = vertexProcessing.process(
                triangleObjectSpace,
                renderComponent.vertexShader,
                shaderUniforms
            )
            val clippingResults = vertexPostProcessing.clip(
                vertexProcessingOutput.triangleClipSpace,
                vertexProcessingOutput.triangleShaderVariables,
                clippingPlanes
            )

            clippingResults.forEach { (triangleClipSpace, triangleShaderVariables) ->
                val triangleViewportCoordinates = vertexPostProcessing.toViewport(
                    triangleClipSpace,
                    viewportMatrix
                ) ?: return@forEach
                val renderContext = RenderContext(
                    framebuffer,
                    zBuffer,
                    wComponents = RenderContext.WComponents(
                        triangleClipSpace.vertexPos0.w,
                        triangleClipSpace.vertexPos1.w,
                        triangleClipSpace.vertexPos2.w
                    )
                )

                val renderTexture = (renderComponent.material.albedo as? RenderTexture)
                    ?.let { renderTextures[it.index] }
                    ?.frontBuffer
                rasterizer.rasterize(
                    triangle = triangleViewportCoordinates,
                    triangleShaderVariables = triangleShaderVariables,
                    shaderUniforms = shaderUniforms,
                    material = renderComponent.material,
                    renderTexture = renderTexture,
                    fragmentShader = renderComponent.fragmentShader,
                    renderContext = renderContext
                )
            }
        }
    }
}
