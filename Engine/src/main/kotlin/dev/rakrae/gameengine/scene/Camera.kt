package dev.rakrae.gameengine.scene

import dev.rakrae.gameengine.math.Mat4x4f
import dev.rakrae.gameengine.math.Vec3f

class Camera {

    var viewMatrix: Mat4x4f = Mat4x4f.identity
        private set

    fun translate(offset: Vec3f) {
        val translationMatrix = Mat4x4f(
            1f, 0f, 0f, -offset.x,
            0f, 1f, 0f, -offset.y,
            0f, 0f, 1f, -offset.z,
            0f, 0f, 0f, 1f
        )
        val currentViewMatrix = viewMatrix
        viewMatrix = translationMatrix * currentViewMatrix
    }
}
