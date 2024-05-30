package dev.rakrae.gameengine.scene

import dev.rakrae.gameengine.graphics.Color
import dev.rakrae.gameengine.math.Vec3f

class Scene(
    val environmentAttributes: EnvironmentAttributes,
    val cameras: List<Camera>,
    val nodes: List<Node>,
    var sunLightDirection: Vec3f
)

class EnvironmentAttributes(
    val ambientColor: Color = Color.white,
    val ambientIntensityMultiplier: Float = 1.0f
)
