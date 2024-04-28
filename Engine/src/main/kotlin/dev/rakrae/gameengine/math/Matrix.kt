package dev.rakrae.gameengine.math

import kotlin.math.abs

/**
 * https://en.wikipedia.org/wiki/Matrix_(mathematics)#Notation
 */
data class Mat4x4f(
    val a11: Float, val a12: Float, val a13: Float, val a14: Float,
    val a21: Float, val a22: Float, val a23: Float, val a24: Float,
    val a31: Float, val a32: Float, val a33: Float, val a34: Float,
    val a41: Float, val a42: Float, val a43: Float, val a44: Float
) {

    private val asList
        get() = listOf(
            a11, a12, a13, a14,
            a21, a22, a23, a24,
            a31, a32, a33, a34,
            a41, a42, a43, a44
        )

    private constructor(m: List<Float>) : this(
        m[0], m[1], m[2], m[3],
        m[4], m[5], m[6], m[7],
        m[8], m[9], m[10], m[11],
        m[12], m[13], m[14], m[15]
    )

    operator fun plus(matrix: Mat4x4f): Mat4x4f {
        val otherAsList = matrix.asList
        val resultAsList = this.asList.mapIndexed { i, value -> value + otherAsList[i] }
        return Mat4x4f(resultAsList)
    }

    operator fun minus(matrix: Mat4x4f): Mat4x4f {
        val otherAsList = matrix.asList
        val resultAsList = this.asList.mapIndexed { i, value -> value - otherAsList[i] }
        return Mat4x4f(resultAsList)
    }

    fun isCloseTo(matrix: Mat4x4f, epsilon: Float = 0.01f): Boolean {
        val similar = { f1: Float, f2: Float -> abs(f1 - f2) < epsilon }
        val thisAsList = this.asList
        val otherAsList = matrix.asList
        return (0..thisAsList.lastIndex)
            .all { i -> similar(thisAsList[i], otherAsList[i]) }
    }

    companion object {
        val identity = Mat4x4f(
            1f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f,
            0f, 0f, 1f, 0f,
            0f, 0f, 0f, 1f
        )

        fun scalarMultiply(scalar: Float, matrix: Mat4x4f): Mat4x4f {
            val matrixAsList = matrix.asList
            val resultAsList = (0..15).map { i -> scalar * matrixAsList[i] }
            return Mat4x4f(resultAsList)
        }
    }
}
