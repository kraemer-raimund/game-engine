package dev.rakrae.gameengine.graphics.rendering.pipeline

import dev.rakrae.gameengine.math.Vec3f

data class VertexShaderInput(
    val shaderUniforms: ShaderUniforms,
    val lightDirWorldSpace: Vec3f
)
