package dev.rakrae.gameengine.scene

import dev.rakrae.gameengine.math.Mat4x4f
import dev.rakrae.gameengine.math.Vec3f
import kotlin.math.cos
import kotlin.math.sin

class Transform(
    var position: Vec3f = Vec3f.zero,
    var rotationEulerRad: Vec3f = Vec3f.zero,
    var scale: Vec3f = Vec3f.one
) {

    val transformMatrix get() = translationMatrix * rotationMatrix * scaleMatrix

    private val translationMatrix get() = translationMatrix(position)
    private val rotationMatrix get() = rotationMatrix(rotationEulerRad)
    private val scaleMatrix get() = scaleMatrix(scale)

    private fun translationMatrix(offset: Vec3f): Mat4x4f {
        return Mat4x4f(
            1f, 0f, 0f, offset.x,
            0f, 1f, 0f, offset.y,
            0f, 0f, 1f, offset.z,
            0f, 0f, 0f, 1f
        )
    }

    private fun rotationMatrix(eulerRad: Vec3f): Mat4x4f {
        val rotX = Mat4x4f(
            1f, 0f, 0f, 0f,
            0f, cos(eulerRad.x), -sin(eulerRad.x), 0f,
            0f, sin(eulerRad.x), cos(eulerRad.x), 0f,
            0f, 0f, 0f, 1f
        )
        val rotY = Mat4x4f(
            cos(eulerRad.y), 0f, sin(eulerRad.y), 0f,
            0f, 1f, 0f, 0f,
            -sin(eulerRad.y), 0f, cos(eulerRad.y), 0f,
            0f, 0f, 0f, 1f
        )
        val rotZ = Mat4x4f(
            cos(eulerRad.z), -sin(eulerRad.z), 0f, 0f,
            sin(eulerRad.z), cos(eulerRad.z), 0f, 0f,
            0f, 0f, 1f, 0f,
            0f, 0f, 0f, 1f
        )
        return rotZ * rotY * rotX
    }

    private fun scaleMatrix(scale: Vec3f): Mat4x4f {
        return Mat4x4f.identity
            .copy(
                a11 = scale.x,
                a22 = scale.y,
                a33 = scale.z
            )
    }
}
