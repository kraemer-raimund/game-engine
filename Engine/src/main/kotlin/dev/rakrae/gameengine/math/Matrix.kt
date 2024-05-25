package dev.rakrae.gameengine.math

import kotlin.math.abs

/**
 * [https://en.wikipedia.org/wiki/Matrix_(mathematics)#Notation](https://en.wikipedia.org/wiki/Matrix_(mathematics)#Notation)
 */
data class Mat4x4f(
    val a11: Float, val a12: Float, val a13: Float, val a14: Float,
    val a21: Float, val a22: Float, val a23: Float, val a24: Float,
    val a31: Float, val a32: Float, val a33: Float, val a34: Float,
    val a41: Float, val a42: Float, val a43: Float, val a44: Float
) {

    /**
     * The transpose of this matrix, meaning rows become columns and vice versa.
     *
     * [https://en.wikipedia.org/wiki/Transpose](https://en.wikipedia.org/wiki/Transpose)
     */
    val transpose: Mat4x4f
        get() = Mat4x4f(
            a11, a21, a31, a41,
            a12, a22, a32, a42,
            a13, a23, a33, a43,
            a14, a24, a34, a44
        )

    private val asList
        get() = listOf(
            a11, a12, a13, a14,
            a21, a22, a23, a24,
            a31, a32, a33, a34,
            a41, a42, a43, a44
        )

    constructor(m: List<Float>) : this(
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

    /**
     * Calculate the inverse of this matrix. The transformation achieved by multiplying this matrix
     * with a vector or with another matrix is the opposite of the original matrix's transformation.
     *
     * Note that this is a relatively computation-intensive calculation.
     *
     * See also [MatrixInversion.inverse].
     */
    fun inverse(): Mat4x4f = MatrixInversion.inverse(this)

    /**
     * Calculate the inverse transpose of this matrix, which is equivalent to the transpose of the
     * inverse. The inverse transpose is used for neutralizing the scaling part of a transformation
     * that is applied to normal vectors, since we want to transform normal vectors without
     * non-uniform scalings (e.g., scaling a normal vector along the X axis would affect both its
     * direction and magnitude).
     *
     * The inverse transpose is mostly needed for [correctly transforming normal vectors](https://stackoverflow.com/questions/13654401/why-transform-normals-with-the-transpose-of-the-inverse-of-the-modelview-matrix).
     * Possibly the [most intuitive explanation is this](https://paroj.github.io/gltut/Illumination/Tut09%20Normal%20Transformation.html):
     * When we scale a sphere along one axis, the normal vectors bend closer and closer together. They
     * stop being perpendicular to the surface. To fix this, we need to apply the opposite scale of the
     * object's transformation, but without affecting the rotation. That's what the inverse transpose
     * does. Since for a pure rotation matrix the transpose is equivalent to the inverse, the inverse
     * transpose of a pure rotation matrix is effectively a no-op. For a pure scale matrix, the values
     * are along the diagonal, so transposing a pure scale matrix is effectively a no-op. Therefore,
     * we can use the inverse transpose to invert the scale part of the transformation without affecting
     * the rotation part.
     *
     * Note that this is a relatively computation-intensive calculation.
     *
     * See also [MatrixInversion.inverse].
     */
    fun inverseTranspose(): Mat4x4f = MatrixInversion.inverse(this).transpose

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

private object MatrixInversion {

    /**
     * Calculate the inverse of this matrix. The algorithm used is
     * [matrix inversion](https://en.wikipedia.org/wiki/Invertible_matrix)
     * using [cofactors](https://en.wikipedia.org/wiki/Minor_(linear_algebra)#Inverse_of_a_matrix)
     * and [adjugate](https://en.wikipedia.org/wiki/Adjugate_matrix).
     *
     * The math here can be a bit daunting, but the important thing is to understand the general concept
     * and how the inverse of a matrix is used. The calculation is just based on an established algorithm
     * for determining the inverse of a matrix that is commonly used in computer graphics and for which
     * examples can be found in open source CG/math libraries. There are other algorithms that achieve
     * the same that are potentially more efficient (in certain cases) but also even more complicated.
     *
     * Further reading and examples:
     * - [https://www.cs.rochester.edu/~brown/Crypto/assts/projects/adj.html](https://www.cs.rochester.edu/~brown/Crypto/assts/projects/adj.html)
     * - [https://github.com/g-truc/glm/blob/89a4d957e99cdefb08a95f7cf1a71478117583ad/glm/gtc/matrix_inverse.inl](https://github.com/g-truc/glm/blob/89a4d957e99cdefb08a95f7cf1a71478117583ad/glm/gtc/matrix_inverse.inl)
     * - [https://en.wikipedia.org/wiki/Adjugate_matrix](https://en.wikipedia.org/wiki/Adjugate_matrix)
     * - [https://en.wikipedia.org/wiki/Minor_(linear_algebra)#Inverse_of_a_matrix](https://en.wikipedia.org/wiki/Minor_(linear_algebra)#Inverse_of_a_matrix)
     * - [https://en.wikipedia.org/wiki/Singular_value_decomposition](https://en.wikipedia.org/wiki/Singular_value_decomposition)
     * - [https://www.the-mathroom.ca/lnalg/lnalg1.5/lnalg1.5.htm](https://www.the-mathroom.ca/lnalg/lnalg1.5/lnalg1.5.htm)
     */
    fun inverse(matrix: Mat4x4f): Mat4x4f {
        val m = with(matrix) {
            listOf(
                listOf(a11, a21, a31, a41),
                listOf(a12, a22, a32, a42),
                listOf(a13, a23, a33, a43),
                listOf(a14, a24, a34, a44)
            )
        }

        val cofactor00 = m[2][2] * m[3][3] - m[3][2] * m[2][3]
        val cofactor01 = m[2][1] * m[3][3] - m[3][1] * m[2][3]
        val cofactor02 = m[2][1] * m[3][2] - m[3][1] * m[2][2]
        val cofactor03 = m[2][0] * m[3][3] - m[3][0] * m[2][3]
        val cofactor04 = m[2][0] * m[3][2] - m[3][0] * m[2][2]
        val cofactor05 = m[2][0] * m[3][1] - m[3][0] * m[2][1]
        val cofactor06 = m[1][2] * m[3][3] - m[3][2] * m[1][3]
        val cofactor07 = m[1][1] * m[3][3] - m[3][1] * m[1][3]
        val cofactor08 = m[1][1] * m[3][2] - m[3][1] * m[1][2]
        val cofactor09 = m[1][0] * m[3][3] - m[3][0] * m[1][3]
        val cofactor10 = m[1][0] * m[3][2] - m[3][0] * m[1][2]
        val cofactor11 = m[1][0] * m[3][1] - m[3][0] * m[1][1]
        val cofactor12 = m[1][2] * m[2][3] - m[2][2] * m[1][3]
        val cofactor13 = m[1][1] * m[2][3] - m[2][1] * m[1][3]
        val cofactor14 = m[1][1] * m[2][2] - m[2][1] * m[1][2]
        val cofactor15 = m[1][0] * m[2][3] - m[2][0] * m[1][3]
        val cofactor16 = m[1][0] * m[2][2] - m[2][0] * m[1][2]
        val cofactor17 = m[1][0] * m[2][1] - m[2][0] * m[1][1]

        val adjugateMatrix = listOf(
            listOf(
                +(m[1][1] * cofactor00 - m[1][2] * cofactor01 + m[1][3] * cofactor02),
                -(m[1][0] * cofactor00 - m[1][2] * cofactor03 + m[1][3] * cofactor04),
                +(m[1][0] * cofactor01 - m[1][1] * cofactor03 + m[1][3] * cofactor05),
                -(m[1][0] * cofactor02 - m[1][1] * cofactor04 + m[1][2] * cofactor05)
            ),
            listOf(
                -(m[0][1] * cofactor00 - m[0][2] * cofactor01 + m[0][3] * cofactor02),
                +(m[0][0] * cofactor00 - m[0][2] * cofactor03 + m[0][3] * cofactor04),
                -(m[0][0] * cofactor01 - m[0][1] * cofactor03 + m[0][3] * cofactor05),
                +(m[0][0] * cofactor02 - m[0][1] * cofactor04 + m[0][2] * cofactor05),
            ),
            listOf(
                +(m[0][1] * cofactor06 - m[0][2] * cofactor07 + m[0][3] * cofactor08),
                -(m[0][0] * cofactor06 - m[0][2] * cofactor09 + m[0][3] * cofactor10),
                +(m[0][0] * cofactor07 - m[0][1] * cofactor09 + m[0][3] * cofactor11),
                -(m[0][0] * cofactor08 - m[0][1] * cofactor10 + m[0][2] * cofactor11),
            ),
            listOf(
                -(m[0][1] * cofactor12 - m[0][2] * cofactor13 + m[0][3] * cofactor14),
                +(m[0][0] * cofactor12 - m[0][2] * cofactor15 + m[0][3] * cofactor16),
                -(m[0][0] * cofactor13 - m[0][1] * cofactor15 + m[0][3] * cofactor17),
                +(m[0][0] * cofactor14 - m[0][1] * cofactor16 + m[0][2] * cofactor17)
            )
        )

        val determinant = listOf(
            m[0][0] * adjugateMatrix[0][0],
            m[0][1] * adjugateMatrix[0][1],
            m[0][2] * adjugateMatrix[0][2],
            m[0][3] * adjugateMatrix[0][3]
        ).sum()

        val inverse = Mat4x4f(adjugateMatrix.flatten().map { it / determinant })
        return inverse
    }
}
