package dev.rakrae.gameengine.graphics.rendering.pipeline

import dev.rakrae.gameengine.graphics.Color
import dev.rakrae.gameengine.math.Mat4x4f
import dev.rakrae.gameengine.math.Vec3f
import dev.rakrae.gameengine.math.Vec4f

/**
 * Shader variables are the outputs of a vertex shader and (after interpolation by the rasterizer)
 * the inputs of a fragment shader.
 */
class ShaderVariables {

    private val floatVariables = mutableMapOf<String, FloatVariable>()
    private val vectorVariables = mutableMapOf<String, VectorVariable>()

    val floatKeys get() = floatVariables.keys
    val vectorKeys get() = vectorVariables.keys

    fun getFloat(key: String): FloatVariable = floatVariables.getValue(key)
    fun setFloat(key: String, floatVariable: FloatVariable) {
        floatVariables[key] = floatVariable
    }

    fun getVector(key: String): VectorVariable = vectorVariables.getValue(key)
    fun setVector(key: String, vectorVariable: VectorVariable) {
        vectorVariables[key] = vectorVariable
    }

    data class FloatVariable(val value: Float, val interpolation: Interpolation)
    data class VectorVariable(val value: Vec3f, val interpolation: Interpolation)
    data class ColorVariable(val value: Color, val interpolation: Interpolation)

    enum class Interpolation {
        FLAT,
        LINEAR,
        PERSPECTIVE
    }
}

/**
 * Uniforms are shader parameters that remain constant between different invocations of the same
 * shader within a rendering call.
 */
class ShaderUniforms(
    builtinMatrixMVP: Mat4x4f,
    builtinMatrixMV: Mat4x4f,
    builtinMatrixV: Mat4x4f,
    builtinMatrixP: Mat4x4f,
    builtinMatrixVP: Mat4x4f,
    builtinMatrixM: Mat4x4f,
    cameraPosWorld: Vec4f,
    cameraRotWorld: Vec4f,
    sunLightDirection: Vec4f,
    ambientColor: Color,
    ambientIntensityMultiplier: Float
) {

    object BuiltinKeys {
        const val MATRIX_MVP = "_builtin_matrix_MVP"
        const val MATRIX_MV = "_builtin_matrix_MV"
        const val MATRIX_V = "_builtin_matrix_V"
        const val MATRIX_P = "_builtin_matrix_P"
        const val MATRIX_VP = "_builtin_matrix_VP"
        const val MATRIX_M = "_builtin_matrix_M"

        const val CAMERA_POS_WORLD = "_builtin_camera_pos_world"
        const val CAMERA_ROT_WORLD = "_builtin_camera_rot_world"

        const val SUN_LIGHT_DIRECTION = "_builtin_sun_light_direction"
        const val POINT_LIGHT_0_POSITION = "_builtin_point_light_0_position"

        const val AMBIENT_COLOR = "_builtin_ambient_color"
        const val AMBIENT_INTENSITY_MULTIPLIER = "_builtin_ambient_intensity_multiplier"
    }

    private val floats = mutableMapOf<String, Float>()
    private val vectors = mutableMapOf<String, Vec4f>()
    private val matrices = mutableMapOf<String, Mat4x4f>()
    private val colors = mutableMapOf<String, Color>()

    init {
        setMatrix(BuiltinKeys.MATRIX_MVP, builtinMatrixMVP)
        setMatrix(BuiltinKeys.MATRIX_MV, builtinMatrixMV)
        setMatrix(BuiltinKeys.MATRIX_V, builtinMatrixV)
        setMatrix(BuiltinKeys.MATRIX_P, builtinMatrixP)
        setMatrix(BuiltinKeys.MATRIX_VP, builtinMatrixVP)
        setMatrix(BuiltinKeys.MATRIX_M, builtinMatrixM)
        setVector(BuiltinKeys.CAMERA_POS_WORLD, cameraPosWorld)
        setVector(BuiltinKeys.CAMERA_ROT_WORLD, cameraRotWorld)
        setVector(BuiltinKeys.SUN_LIGHT_DIRECTION, sunLightDirection)
        setColor(BuiltinKeys.AMBIENT_COLOR, ambientColor)
        setFloat(BuiltinKeys.AMBIENT_INTENSITY_MULTIPLIER, ambientIntensityMultiplier)
    }

    val floatKeys get() = floats.keys
    val vectorKeys get() = vectors.keys
    val matrixKeys get() = vectors.keys
    val colorKeys get() = vectors.keys

    fun getFloat(key: String): Float = floats.getValue(key)
    fun setFloat(key: String, float: Float) {
        floats[key] = float
    }

    fun getVector(key: String): Vec4f = vectors.getValue(key)
    fun setVector(key: String, vector: Vec4f) {
        vectors[key] = vector
    }

    fun getMatrix(key: String): Mat4x4f = matrices.getValue(key)
    fun setMatrix(key: String, matrix: Mat4x4f) {
        matrices[key] = matrix
    }

    fun getColor(key: String): Color = colors.getValue(key)
    fun setColor(key: String, color: Color) {
        colors[key] = color
    }
}
