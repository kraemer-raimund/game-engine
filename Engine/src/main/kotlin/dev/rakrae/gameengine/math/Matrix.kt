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

    val transposed: Mat4x4f
        get() {
            return Mat4x4f(
                a11, a21, a31, a41,
                a12, a22, a32, a42,
                a13, a23, a33, a43,
                a14, a24, a34, a44
            )
        }

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

    operator fun times(vector: Vec4f): Vec4f {
        val m = this
        val v = vector
        return Vec4f(
            m.a11 * v.x + m.a12 * v.y + m.a13 * v.z + m.a14 * v.w,
            m.a21 * v.x + m.a22 * v.y + m.a23 * v.z + m.a24 * v.w,
            m.a31 * v.x + m.a32 * v.y + m.a33 * v.z + m.a34 * v.w,
            m.a41 * v.x + m.a42 * v.y + m.a43 * v.z + m.a44 * v.w
        )
    }

    /**
     * A matrix is multiplied by a matrix according to this pattern (exemplified with two
     * 2x2 matrices for simplicity):
     *
     * |1; 2| * |a; b| = |1a + 2c; 1b + 2d|
     * |3; 4|   |c; d|   |3a + 4c; 3b + 4d|
     *
     * This example would be very complex with 4x4 matrices, but the pattern is the same.
     * Note how we use the rows from the first matrix and the columns from the seconds one
     * and calculate each value by summing the products of the corresponding values in the
     * respective row/column from the operands.
     */
    operator fun times(matrix: Mat4x4f): Mat4x4f {
        val (a, b, c, d) = this.asList.subList(0, 4)
        val (e, f, g, h) = this.asList.subList(4, 8)
        val (i, j, k, l) = this.asList.subList(8, 12)
        val (m, n, o, p) = this.asList.subList(12, 16)

        val (A, B, C, D) = matrix.asList.subList(0, 4)
        val (E, F, G, H) = matrix.asList.subList(4, 8)
        val (I, J, K, L) = matrix.asList.subList(8, 12)
        val (M, N, O, P) = matrix.asList.subList(12, 16)

        val m1Rows = listOf(
            listOf(a, b, c, d),
            listOf(e, f, g, h),
            listOf(i, j, k, l),
            listOf(m, n, o, p)
        )

        val m2Columns = listOf(
            listOf(A, E, I, M),
            listOf(B, F, J, N),
            listOf(C, G, K, O),
            listOf(D, H, L, P)
        )

        val values = (0..15).map {
            val row = it / 4
            val col = it % 4
            (0..3)
                .map { i -> m1Rows[row][i] * m2Columns[col][i] }
                .sum()
        }

        return Mat4x4f(values)
    }

    fun isCloseTo(matrix: Mat4x4f, epsilon: Float = 0.01f): Boolean {
        val similar = { f1: Float, f2: Float -> abs(f1 - f2) < epsilon }
        val thisAsList = this.asList
        val otherAsList = matrix.asList
        return (0..thisAsList.lastIndex).all { i -> similar(thisAsList[i], otherAsList[i]) }
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

operator fun Float.times(matrix: Mat4x4f): Mat4x4f {
    return Mat4x4f.scalarMultiply(this, matrix)
}
