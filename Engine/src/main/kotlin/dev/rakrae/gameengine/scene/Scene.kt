package dev.rakrae.gameengine.scene

import dev.rakrae.gameengine.graphics.Color

class Scene(
    val environmentAttributes: EnvironmentAttributes,
    val cameras: List<Camera>,
    val nodes: List<Node>
)

class EnvironmentAttributes(
    val ambientColor: Color = Color.white,
    val ambientIntensityMultiplier: Float = 1.0f
)
