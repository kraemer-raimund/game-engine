package dev.rakrae.gameengine.scene

import dev.rakrae.gameengine.math.Mat4x4f

class Scene(val nodes: Sequence<Node>) {

    val activeCamera = Camera(
        viewMatrix = Mat4x4f(
            1f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f,
            0f, 0f, 1f, 0f,
            0f, 0f, 0f, 1f
        )
    )
}
