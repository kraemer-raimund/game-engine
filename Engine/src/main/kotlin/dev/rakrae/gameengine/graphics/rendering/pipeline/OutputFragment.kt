package dev.rakrae.gameengine.graphics.rendering.pipeline

import dev.rakrae.gameengine.graphics.Color

data class OutputFragment(
    val fragmentColor: Color,
    val depth: Float
)
