package dev.rakrae.gameengine.graphics.pipeline

import dev.rakrae.gameengine.graphics.Material
import dev.rakrae.gameengine.math.Vec2i
import dev.rakrae.gameengine.math.Vec3f

data class InputFragment(
    val windowSpacePosition: Vec2i,
    val interpolatedNormal: Vec3f,
    val faceNormalWorldSpace: Vec3f,
    val depth: Float,
    val material: Material
)
