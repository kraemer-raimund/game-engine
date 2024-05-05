package dev.rakrae.gameengine.graphics.rendering.pipeline

import dev.rakrae.gameengine.graphics.Material
import dev.rakrae.gameengine.math.Vec2f
import dev.rakrae.gameengine.math.Vec2i
import dev.rakrae.gameengine.math.Vec3f

data class InputFragment(
    val windowSpacePosition: Vec2i,
    val renderContext: RenderContext,
    val interpolatedNormal: Vec3f,
    val faceNormalWorldSpace: Vec3f,
    val depth: Float,
    val material: Material,
    val uv: Vec2f
)
