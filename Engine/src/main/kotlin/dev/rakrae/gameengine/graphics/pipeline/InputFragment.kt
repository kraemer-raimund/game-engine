package dev.rakrae.gameengine.graphics.pipeline

import dev.rakrae.gameengine.graphics.Color
import dev.rakrae.gameengine.math.Vec2i
import dev.rakrae.gameengine.math.Vec3f

data class InputFragment(
    val windowSpacePosition: Vec2i,
    val interpolatedVertexColor: Color,
    val interpolatedNormal: Vec3f,
    val faceNormal: Vec3f,
    val depth: Float
)