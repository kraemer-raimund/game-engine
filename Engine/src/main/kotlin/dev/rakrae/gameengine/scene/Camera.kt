package dev.rakrae.gameengine.scene

import dev.rakrae.gameengine.core.Engine
import dev.rakrae.gameengine.math.Mat4x4f
import dev.rakrae.gameengine.math.Vec3f
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

class Camera(
    private var loc: Vec3f = Vec3f.zero,
    private var rot: Vec3f = Vec3f.zero
) {

    val horizontalFovRadians: Float
        get() = 0.5f * PI.toFloat()
    var verticalFovRadians: Float = horizontalFovRadians / Engine.aspectRatio
    var nearPlane: Float = 1f
    var farPlane: Float = 100f

    // https://en.wikipedia.org/wiki/Transformation_matrix#Perspective_projection
    val projectionMatrix: Mat4x4f
        get() {
            val f = farPlane
            val n = nearPlane
            return Mat4x4f(
                1f / tan(0.5f * horizontalFovRadians), 0f, 0f, 0f,
                0f, 1f / tan(0.5f * verticalFovRadians), 0f, 0f, 0f,
                0f, -((f + n) / f - n), -((2 * f * n) / (f - n)),
                0f, 0f, 1f, 0f
            )
        }

    val viewMatrix: Mat4x4f
        get() = rotationMatrix * translationMatrix

    private val rotationMatrix: Mat4x4f
        get() {
            val rotX = Mat4x4f(
                1f, 0f, 0f, 0f,
                0f, cos(rot.x), -sin(rot.x), 0f,
                0f, sin(rot.x), cos(rot.x), 0f,
                0f, 0f, 0f, 1f
            )
            val rotY = Mat4x4f(
                cos(rot.y), 0f, sin(rot.y), 0f,
                0f, 1f, 0f, 0f,
                -sin(rot.y), 0f, cos(rot.y), 0f,
                0f, 0f, 0f, 1f
            )
            val rotZ = Mat4x4f(
                cos(rot.z), -sin(rot.z), 0f, 0f,
                sin(rot.z), cos(rot.z), 0f, 0f,
                0f, 0f, 1f, 0f,
                0f, 0f, 0f, 1f
            )
            return rotZ * rotY * rotX
        }

    private val translationMatrix: Mat4x4f
        get() = Mat4x4f(
            1f, 0f, 0f, -loc.x,
            0f, 1f, 0f, -loc.y,
            0f, 0f, 1f, -loc.z,
            0f, 0f, 0f, 1f
        )

    fun translate(offset: Vec3f) {
        loc += offset
    }

    fun rotate(euler: Vec3f) {
        rot += euler
    }
}
