package dev.rakrae.gameengine.graphics.pipeline

import dev.rakrae.gameengine.graphics.Color
import dev.rakrae.gameengine.math.Vec2i

data class Fragment(
    val screenPosition: Vec2i,
    val color: Color,
    val depth: Float
)
