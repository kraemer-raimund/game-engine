package dev.rakrae.gameengine.graphics.rendering.pipeline

import dev.rakrae.gameengine.graphics.Color
import dev.rakrae.gameengine.math.Vec3f

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
