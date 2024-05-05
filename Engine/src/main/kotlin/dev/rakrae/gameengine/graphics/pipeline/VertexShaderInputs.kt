package dev.rakrae.gameengine.graphics.pipeline

import dev.rakrae.gameengine.math.Mat4x4f

data class VertexShaderInputs(
    val projection: Mat4x4f,
    val modelView: Mat4x4f
)
