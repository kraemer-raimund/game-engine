package dev.rakrae.gameengine.graphics.pipeline

import dev.rakrae.gameengine.graphics.Color
import dev.rakrae.gameengine.math.Vec2i

data class InputFragment(
    val windowSpacePosition: Vec2i,
    val interpolatedVertexColor: Color,
    val depth: Float
)
