package dev.rakrae.gameengine.scene

import dev.rakrae.gameengine.core.Engine
import dev.rakrae.gameengine.graphics.RenderTexture
import dev.rakrae.gameengine.graphics.rendering.pipeline.PostProcessingShader
import dev.rakrae.gameengine.math.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

class Camera(
    private val horizontalFovRadians: Float = 0.5f * PI.toFloat(),
    private val viewportOffsetNormalized: Vec2f = Vec2f(0f, 0f),
    private val viewportScaleNormalized: Vec2f = Vec2f(1f, 1f),
    private var loc: Vec3f = Vec3f.zero,
    private var rot: Vec3f = Vec3f.zero
) {

    var renderTexture: RenderTexture? = null

    val worldPos: Vec3f get() = loc

    val postProcessingShaders: MutableList<PostProcessingShader> = mutableListOf()

    val nearPlane: Float = 1f
    val farPlane: Float = 100f
    private val verticalFovRadians: Float
        get() = if (renderTexture == null) horizontalFovRadians / viewportAspectRatio else 1f

    val viewportOffset
        get() = Vec2i(
            (viewportOffsetNormalized.x * Engine.screenSize.width).toInt(),
            (viewportOffsetNormalized.y * Engine.screenSize.height).toInt(),
        )

    val viewportSize
        get() = if (renderTexture == null) Vec2i(
            (viewportScaleNormalized.x * Engine.screenSize.width).toInt(),
            (viewportScaleNormalized.y * Engine.screenSize.height).toInt(),
        ) else Vec2i(512, 512)

    private val viewportAspectRatio
        get() = with(viewportSize) { x.toFloat() / y.toFloat() }

    /**
     * Transforms normalized device coordinates into screen coordinates.
     * - [https://learnwebgl.brown37.net/08_projections/projections_viewport.html](https://learnwebgl.brown37.net/08_projections/projections_viewport.html)
     */
    val viewportMatrix: Mat4x4f
        get() {
            val halfW = 0.5f * viewportSize.x
            val halfH = 0.5f * viewportSize.y
            return Mat4x4f(
                halfW, 0f, 0f, halfW,
                0f, halfH, 0f, halfH,
                0f, 0f, 0.5f, 0.5f,
                0f, 0f, 0f, 1f
            )
        }

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
            return rotX * rotZ * rotY
        }

    private val translationMatrix: Mat4x4f
        get() = Mat4x4f(
            1f, 0f, 0f, -loc.x,
            0f, 1f, 0f, -loc.y,
            0f, 0f, 1f, -loc.z,
            0f, 0f, 0f, 1f
        )

    fun translate(offset: Vec3f) {
        val revertedRotY = Mat4x4f(
            cos(-rot.y), 0f, sin(-rot.y), 0f,
            0f, 1f, 0f, 0f,
            -sin(-rot.y), 0f, cos(-rot.y), 0f,
            0f, 0f, 0f, 1f
        )
        loc += (revertedRotY * Vec4f(offset, 1f)).toVec3f()
    }

    fun rotate(euler: Vec3f) {
        rot += euler
    }

    companion object {
        /**
         * Calculate a view matrix from a given camera (eye) position, the position to look at,
         * and the up vector in world space, i.e. usually (0, 1, 0).
         */
        fun lookAt(eyePos: Vec3f, targetPos: Vec3f, worldUp: Vec3f): Mat4x4f {
            val camDir = (targetPos - eyePos).normalized
            val camRight = (camDir cross worldUp).normalized
            val camUp = (camRight cross camDir).normalized

            val viewMatrix = Mat4x4f(
                camRight.x, camRight.y, camRight.z, -(camRight dot eyePos),
                camUp.x, camUp.y, camUp.z, -(camUp dot eyePos),
                camDir.x, camDir.y, camDir.z, -(camDir dot eyePos),
                0f, 0f, 0f, 1f
            )
            return viewMatrix
        }
    }
}
