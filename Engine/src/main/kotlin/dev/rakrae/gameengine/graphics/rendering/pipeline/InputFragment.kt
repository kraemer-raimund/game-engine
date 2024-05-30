package dev.rakrae.gameengine.graphics.rendering.pipeline

import dev.rakrae.gameengine.graphics.Bitmap
import dev.rakrae.gameengine.graphics.Material
import dev.rakrae.gameengine.math.Vec4f

data class InputFragment(
    /**
     * The window-space position in the form (x, y, z, 1/w).
     */
    val fragPos: Vec4f,
    val renderContext: RenderContext,
    val material: Material,
    val renderTexture: Bitmap?,
    val shaderVariables: ShaderVariables,
    val shaderUniforms: ShaderUniforms
)
