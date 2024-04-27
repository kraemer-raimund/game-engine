package dev.rakrae.gameengine.math

import kotlin.math.abs

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

    operator fun plus(matrix: Mat4x4f): Mat4x4f {
        val m1 = this
        val m2 = matrix
        return Mat4x4f(
            a11 = m1.a11 + m2.a11, a12 = m1.a12 + m2.a12, a13 = m1.a13 + m2.a13, a14 = m1.a14 + m2.a14,
            a21 = m1.a21 + m2.a21, a22 = m1.a22 + m2.a22, a23 = m1.a23 + m2.a23, a24 = m1.a24 + m2.a24,
            a31 = m1.a31 + m2.a31, a32 = m1.a32 + m2.a32, a33 = m1.a33 + m2.a33, a34 = m1.a34 + m2.a34,
            a41 = m1.a41 + m2.a41, a42 = m1.a42 + m2.a42, a43 = m1.a43 + m2.a43, a44 = m1.a44 + m2.a44
        )
    }

    fun isCloseTo(matrix: Mat4x4f, epsilon: Float = 0.01f): Boolean {
        val similar = { f1: Float, f2: Float -> abs(f1 - f2) < epsilon }
        val thisAsArray = this.asList
        val otherAsArray = matrix.asList
        return (0..15).all { i -> similar(thisAsArray[i], otherAsArray[i]) }
    }
}
