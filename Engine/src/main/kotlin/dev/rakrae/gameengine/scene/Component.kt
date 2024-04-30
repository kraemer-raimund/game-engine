package dev.rakrae.gameengine.scene

import dev.rakrae.gameengine.graphics.Material
import dev.rakrae.gameengine.graphics.Mesh
import dev.rakrae.gameengine.math.Mat4x4f
import dev.rakrae.gameengine.math.Vec3f

sealed class Component

class RenderComponent(
    val mesh: Mesh,
    position: Vec3f,
    scale: Vec3f = Vec3f(1f, 1f, 1f),
    val material: Material = Material.default
) : Component() {

    val transformMatrix by lazy { translationMatrix * scaleMatrix }

    val translationMatrix = Mat4x4f(
        1f, 0f, 0f, position.x,
        0f, 1f, 0f, position.y,
        0f, 0f, 1f, position.z,
        0f, 0f, 0f, 1f
    )

    val scaleMatrix = Mat4x4f(
        scale.x, 0f, 0f, 0f,
        0f, scale.y, 0f, 0f,
        0f, 0f, scale.z, 0f,
        0f, 0f, 0f, 1f
    )
}
