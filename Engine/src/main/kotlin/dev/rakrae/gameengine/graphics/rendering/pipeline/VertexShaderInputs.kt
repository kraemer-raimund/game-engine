package dev.rakrae.gameengine.graphics.rendering.pipeline

import dev.rakrae.gameengine.math.Mat4x4f
import dev.rakrae.gameengine.math.Vec3f

data class VertexShaderInputs(
    val projection: Mat4x4f,
    val modelView: Mat4x4f,
    val model: Mat4x4f,
    val lightDirWorldSpace: Vec3f
)
