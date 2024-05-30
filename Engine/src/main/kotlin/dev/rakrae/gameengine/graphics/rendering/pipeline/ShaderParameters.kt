package dev.rakrae.gameengine.graphics.rendering.pipeline

import dev.rakrae.gameengine.graphics.Color
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

    class FloatVariable(val value: Float, val interpolation: Interpolation)
    class VectorVariable(val value: Vec3f, val interpolation: Interpolation)
    class ColorVariable(val value: Color, val interpolation: Interpolation)

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
class ShaderUniforms {

    object BuiltinKeys {
        const val ambientColor = "_builtin_ambient_color"
        const val ambientIntensityMultiplier = "_builtin_ambient_intensity_multiplier"
    }

    private val floats = mutableMapOf<String, Float>()
    private val vectors = mutableMapOf<String, Vec4f>()
    private val colors = mutableMapOf<String, Color>()

    val floatKeys get() = floats.keys
    val vectorKeys get() = vectors.keys
    val colorKeys get() = vectors.keys

    fun getFloat(key: String): Float = floats.getValue(key)
    fun setFloat(key: String, float: Float) {
        floats[key] = float
    }

    fun getVector(key: String): Vec4f = vectors.getValue(key)
    fun setVector(key: String, vector: Vec4f) {
        vectors[key] = vector
    }

    fun getColor(key: String): Color = colors.getValue(key)
    fun setColor(key: String, color: Color) {
        colors[key] = color
    }
}
