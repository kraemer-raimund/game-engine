package dev.rakrae.gameengine.math

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Matrix 4x4 Float")
internal class Mat4x4fTest {

    @Test
    fun `matrices are (almost) equal`() {
        val m1 = Mat4x4f(
            a11 = 3.2f, a12 = 4.1f, a13 = 11.7f, a14 = -11.89f,
            a21 = 42.69f, a22 = 13.37f, a23 = 1f, a24 = 0.9f,
            a31 = -2.789f, a32 = -6.456f, a33 = 8f, a34 = 4.5f,
            a41 = 7.654f, a42 = 4.357f, a43 = 8.47f, a44 = 6.894f
        )
        val m2 = Mat4x4f(
            a11 = 3.20001f, a12 = 4.1f, a13 = 11.7f, a14 = -11.89f,
            a21 = 42.69f, a22 = 13.37f, a23 = 1f, a24 = 0.9f,
            a31 = -2.789f, a32 = -6.456f, a33 = 8f, a34 = 4.5f,
            a41 = 7.654f, a42 = 4.357f, a43 = 8.47f, a44 = 6.894f
        )

        assertTrue(
            m1.isCloseTo(m2),
            "Expected matrices to be equal (within margin for rounding error).\n" +
                    "m1:\n$m1\n" +
                    "m2:\n$m2"
        )
    }

    @Test
    fun `matrices are different`() {
        val m1 = Mat4x4f(
            a11 = 100f, a12 = 4.1f, a13 = 11.7f, a14 = -11.89f,
            a21 = 42.69f, a22 = 13.37f, a23 = 1f, a24 = 0.9f,
            a31 = -2.789f, a32 = -6.456f, a33 = 8f, a34 = 4.5f,
            a41 = 7.654f, a42 = 4.357f, a43 = 8.47f, a44 = 6.894f
        )
        val m2 = Mat4x4f(
            a11 = -30f, a12 = 4.1f, a13 = 11.7f, a14 = -11.89f,
            a21 = 42.69f, a22 = 13.37f, a23 = 1f, a24 = 0.9f,
            a31 = -2.789f, a32 = -6.456f, a33 = 8f, a34 = 4.5f,
            a41 = 7.654f, a42 = 4.357f, a43 = 8.47f, a44 = 6.894f
        )

        assertFalse(
            m1.isCloseTo(m2),
            "Expected matrices to be different.\n" +
                    "m1:\n$m1\n" +
                    "m2:\n$m2"
        )
    }

    @Test
    fun `matrix addition`() {
        val m1 = Mat4x4f(
            a11 = 3.2f, a12 = 4.1f, a13 = 11.7f, a14 = -11.89f,
            a21 = 42.69f, a22 = 13.37f, a23 = 1f, a24 = 0.9f,
            a31 = -2.789f, a32 = -6.456f, a33 = 8f, a34 = 4.5f,
            a41 = 7.654f, a42 = 4.357f, a43 = 8.47f, a44 = 6.894f
        )
        val m2 = Mat4x4f(
            a11 = 31.2f, a12 = 4.7f, a13 = 13.4f, a14 = 11.62f,
            a21 = -2.689f, a22 = 13.42f, a23 = 12.7f, a24 = 0.9f,
            a31 = 2.51f, a32 = 6.456f, a33 = 18.56f, a34 = -54.5f,
            a41 = -7f, a42 = 3.57f, a43 = 81.87f, a44 = -6.894f
        )
        val expected = Mat4x4f(
            a11 = 34.4f, a12 = 8.8f, a13 = 25.1f, a14 = -0.27f,
            a21 = 40.001f, a22 = 26.79f, a23 = 13.7f, a24 = 1.8f,
            a31 = -0.279f, a32 = 0.0f, a33 = 26.56f, a34 = -50.0f,
            a41 = 0.654f, a42 = 7.927f, a43 = 90.34f, a44 = 0.0f
        )

        val actual = m1 + m2

        assertTrue(
            actual.isCloseTo(expected),
            "Expected matrices to be equal (within margin for rounding error).\n" +
                    "Expected:\n$expected\n" +
                    "Actual:\n$actual"
        )
    }

    @Test
    fun `matrix subtraction`() {
        val m1 = Mat4x4f(
            a11 = 3.2f, a12 = 4.1f, a13 = 11.7f, a14 = -11.89f,
            a21 = 42.69f, a22 = 13.37f, a23 = 1f, a24 = 0.9f,
            a31 = -2.789f, a32 = -6.456f, a33 = 8f, a34 = 4.5f,
            a41 = 7.654f, a42 = 4.357f, a43 = 8.47f, a44 = 6.894f
        )
        val m2 = Mat4x4f(
            a11 = 31.2f, a12 = 4.7f, a13 = 13.4f, a14 = 11.62f,
            a21 = -2.689f, a22 = 13.42f, a23 = 12.7f, a24 = 0.9f,
            a31 = 2.51f, a32 = 6.456f, a33 = 18.56f, a34 = -54.5f,
            a41 = -7f, a42 = 3.57f, a43 = 81.87f, a44 = -6.894f
        )
        val expected = Mat4x4f(
            a11 = -28.0f, a12 = -0.5999999f, a13 = -1.6999998f, a14 = -23.51f,
            a21 = 45.378998f, a22 = -0.05000019f, a23 = -11.7f, a24 = 0.0f,
            a31 = -5.299f, a32 = -12.912f, a33 = -10.559999f, a34 = 59.0f,
            a41 = 14.653999f, a42 = 0.78699994f, a43 = -73.4f, a44 = 13.788f
        )

        val actual = m1 - m2

        assertTrue(
            actual.isCloseTo(expected),
            "Expected matrices to be equal (within margin for rounding error).\n" +
                    "Expected:\n$expected\n" +
                    "Actual:\n$actual"
        )
    }

    @Test
    fun `scalar matrix multiplication`() {
        val matrix = Mat4x4f(
            a11 = 3.2f, a12 = 4.1f, a13 = 11.7f, a14 = -11.89f,
            a21 = 42.69f, a22 = 13.37f, a23 = 1f, a24 = 0.9f,
            a31 = -2.789f, a32 = -6.456f, a33 = 8f, a34 = 4.5f,
            a41 = 7.654f, a42 = 4.357f, a43 = 8.47f, a44 = 6.894f
        )
        val scalar = 1.23f

        val expected = Mat4x4f(
            a11 = 3.936f, a12 = 5.0429997f, a13 = 14.391f, a14 = -14.624701f,
            a21 = 52.508698f, a22 = 16.4451f, a23 = 1.23f, a24 = 1.107f,
            a31 = -3.43047f, a32 = -7.94088f, a33 = 9.84f, a34 = 5.535f,
            a41 = 9.41442f, a42 = 5.35911f, a43 = 10.4181f, a44 = 8.47962f
        )

        val actual = scalar * matrix

        assertTrue(
            actual.isCloseTo(expected),
            "Expected matrices to be equal (within margin for rounding error).\n" +
                    "Expected:\n$expected\n" +
                    "Actual:\n$actual"
        )
    }

    @Test
    fun `matrix vector multiplication (Vec4f)`() {
        val matrix = Mat4x4f(
            a11 = 3.2f, a12 = 4.1f, a13 = 11.7f, a14 = -11.89f,
            a21 = 42.69f, a22 = 13.37f, a23 = 1f, a24 = 0.9f,
            a31 = -2.789f, a32 = -6.456f, a33 = 8f, a34 = 4.5f,
            a41 = 7.654f, a42 = 4.357f, a43 = 8.47f, a44 = 6.894f
        )
        val vector = Vec4f(
            x = 23.4f,
            y = 5.6f,
            z = -2.57f,
            w = 0.79f
        )
        val expected = Vec4f(
            x = 58.377895f,
            y = 1071.959f,
            z = -118.421196f,
            w = 187.18115f
        )

        val actual = matrix * vector

        assertTrue(
            actual.isCloseTo(expected),
            "Expected vectors to be equal (within margin for rounding error).\n" +
                    "Expected:\n$expected\n" +
                    "Actual:\n$actual"
        )
    }

    @Test
    fun `identity matrix multiplied by matrix yields original matrix`() {
        val m = Mat4x4f(
            a11 = 3.2f, a12 = 4.1f, a13 = 11.7f, a14 = -11.89f,
            a21 = 42.69f, a22 = 13.37f, a23 = 1f, a24 = 0.9f,
            a31 = -2.789f, a32 = -6.456f, a33 = 8f, a34 = 4.5f,
            a41 = 7.654f, a42 = 4.357f, a43 = 8.47f, a44 = 6.894f
        )

        val identityMultipliedByM = Mat4x4f.identity * m

        assertTrue(
            identityMultipliedByM.isCloseTo(m),
            """
                Expected matrices to be equal (within margin for rounding error).
                Expected:
                $m
                Actual:
                $identityMultipliedByM
            """.trimIndent()
        )

        val mMultipliedByIdentity = m * Mat4x4f.identity

        assertTrue(
            mMultipliedByIdentity.isCloseTo(m),
            """
                Expected matrices to be equal (within margin for rounding error).
                Expected:
                $m
                Actual:
                $mMultipliedByIdentity
            """.trimIndent()
        )
    }

    @Test
    fun `identity matrix multiplied by vector yields original vector`() {
        val originalVector = Vec4f(
            x = 23.4f,
            y = 5.6f,
            z = -2.57f,
            w = 0.79f
        )

        val actual = Mat4x4f.identity * originalVector

        assertTrue(
            actual.isCloseTo(originalVector),
            "Expected vectors to be equal (within margin for rounding error).\n" +
                    "Expected:\n$originalVector\n" +
                    "Actual:\n$actual"
        )
    }

    @Test
    fun `translation matrix moves vector`() {
        val translationMatrix = Mat4x4f(
            1f, 0f, 0f, 1.5f,
            0f, 1f, 0f, 2.3f,
            0f, 0f, 1f, -7.31f,
            0f, 0f, 0f, 1f
        )
        val vectorToBeTransformed = Vec4f(
            x = 23.4f,
            y = 5.6f,
            z = -2.57f,
            w = 1f
        )
        val expected = Vec4f(
            x = 24.9f,
            y = 7.8999996f,
            z = -9.88f,
            w = 1f
        )

        val actual = translationMatrix * vectorToBeTransformed

        assertTrue(
            actual.isCloseTo(expected),
            "Expected vectors to be equal (within margin for rounding error).\n" +
                    "Expected:\n$expected\n" +
                    "Actual:\n$actual"
        )
    }

    @Test
    fun `matrix multiplication`() {
        val m1 = Mat4x4f(
            a11 = 3.2f, a12 = 4.1f, a13 = 11.7f, a14 = -11.89f,
            a21 = 42.69f, a22 = 13.37f, a23 = 1f, a24 = 0.9f,
            a31 = -2.789f, a32 = -6.456f, a33 = 8f, a34 = 4.5f,
            a41 = 7.654f, a42 = 4.357f, a43 = 8.47f, a44 = 6.894f
        )
        val m2 = Mat4x4f(
            a11 = 31.2f, a12 = 4.7f, a13 = 13.4f, a14 = 11.62f,
            a21 = -2.689f, a22 = 13.42f, a23 = 12.7f, a24 = 0.9f,
            a31 = 2.51f, a32 = 6.456f, a33 = 18.56f, a34 = -54.5f,
            a41 = -7f, a42 = 3.57f, a43 = 81.87f, a44 = -6.894f
        )
        val expected = Mat4x4f(
            a11 = 201.41211f, a12 = 103.1499f, a13 = -661.3324f, a14 = -514.8063f,
            a21 = 1292.186f, a22 = 389.7374f, a23 = 834.08795f, a24 = 447.38617f,
            a31 = -81.076614f, a32 = -32.03482f, a33 = 397.5312f, a34 = -505.24158f,
            a41 = 200.09053f, a42 = 173.73862f, a43 = 879.5125f, a44 = -416.2815f
        )

        val actual = m1 * m2

        assertTrue(
            actual.isCloseTo(expected),
            "Expected matrices to be equal (within margin for rounding error).\n" +
                    "Expected:\n$expected\n" +
                    "Actual:\n$actual"
        )
    }

    @Test
    fun `matrix transposition`() {
        val m = Mat4x4f(
            a11 = 3.2f, a12 = 4.1f, a13 = 11.7f, a14 = -11.89f,
            a21 = 42.69f, a22 = 13.37f, a23 = 1f, a24 = 0.9f,
            a31 = -2.789f, a32 = -6.456f, a33 = 8f, a34 = 4.5f,
            a41 = 7.654f, a42 = 4.357f, a43 = 8.47f, a44 = 6.894f
        )
        val mTransposeExpected = Mat4x4f(
            a11 = 3.2f, a12 = 42.69f, a13 = -2.789f, a14 = 7.654f,
            a21 = 4.1f, a22 = 13.37f, a23 = -6.456f, a24 = 4.357f,
            a31 = 11.7f, a32 = 1f, a33 = 8f, a34 = 8.47f,
            a41 = -11.89f, a42 = 0.9f, a43 = 4.5f, a44 = 6.894f
        )

        val actual = m.transpose

        assertTrue(
            actual.isCloseTo(mTransposeExpected),
            "Expected matrices to be equal (within margin for rounding error).\n" +
                    "Expected:\n$mTransposeExpected\n" +
                    "Actual:\n$actual"
        )
    }

    @Test
    fun `transpose of the transpose is the original matrix`() {
        val m = Mat4x4f(
            a11 = 3.2f, a12 = 4.1f, a13 = 11.7f, a14 = -11.89f,
            a21 = 42.69f, a22 = 13.37f, a23 = 1f, a24 = 0.9f,
            a31 = -2.789f, a32 = -6.456f, a33 = 8f, a34 = 4.5f,
            a41 = 7.654f, a42 = 4.357f, a43 = 8.47f, a44 = 6.894f
        )

        val transposeOfTransposeOfM = m.transpose.transpose

        assertTrue(
            transposeOfTransposeOfM.isCloseTo(m),
            """
                Expected matrices to be equal (within margin for rounding error).
                Expected:
                $m
                Actual:
                $transposeOfTransposeOfM
            """.trimIndent()
        )
    }

    @Test
    fun `inverse of a matrix multiplied by the original matrix yields identity`() {
        val m = Mat4x4f(
            a11 = 3.2f, a12 = 4.1f, a13 = 11.7f, a14 = -11.89f,
            a21 = 42.69f, a22 = 13.37f, a23 = 1f, a24 = 0.9f,
            a31 = -2.789f, a32 = -6.456f, a33 = 8f, a34 = 4.5f,
            a41 = 7.654f, a42 = 4.357f, a43 = 8.47f, a44 = 6.894f
        )

        val mInverseMultipliedByM = m * m.inverse()

        assertTrue(
            mInverseMultipliedByM.isCloseTo(Mat4x4f.identity),
            """
                Expected matrices to be equal (within margin for rounding error).
                Expected:
                ${Mat4x4f.identity}
                Actual:
                $mInverseMultipliedByM
            """.trimIndent()
        )

        val mMultipliedByMInverse = m.inverse() * m

        assertTrue(
            mMultipliedByMInverse.isCloseTo(Mat4x4f.identity),
            """
                Expected matrices to be equal (within margin for rounding error).
                Expected:
                ${Mat4x4f.identity}
                Actual:
                $mMultipliedByMInverse
            """.trimIndent()
        )
    }

    @Test
    fun `inverse of the inverse of a matrix yields original matrix`() {
        val m = Mat4x4f(
            a11 = 3.2f, a12 = 4.1f, a13 = 11.7f, a14 = -11.89f,
            a21 = 42.69f, a22 = 13.37f, a23 = 1f, a24 = 0.9f,
            a31 = -2.789f, a32 = -6.456f, a33 = 8f, a34 = 4.5f,
            a41 = 7.654f, a42 = 4.357f, a43 = 8.47f, a44 = 6.894f
        )

        val inverseOfInverseOfM = m.inverse().inverse()

        assertTrue(
            inverseOfInverseOfM.isCloseTo(m),
            """
                Expected matrices to be equal (within margin for rounding error).
                Expected:
                $m
                Actual:
                $inverseOfInverseOfM
            """.trimIndent()
        )
    }

    @Test
    fun `inverse transpose of a matrix is equivalent to transpose of inverse and vice versa`() {
        val m = Mat4x4f(
            a11 = 3.2f, a12 = 4.1f, a13 = 11.7f, a14 = -11.89f,
            a21 = 42.69f, a22 = 13.37f, a23 = 1f, a24 = 0.9f,
            a31 = -2.789f, a32 = -6.456f, a33 = 8f, a34 = 4.5f,
            a41 = 7.654f, a42 = 4.357f, a43 = 8.47f, a44 = 6.894f
        )

        val inverseTransposeOfM = m.inverseTranspose()
        val transposeOfInverseOfM = m.inverse().transpose
        val inverseOfTransposeOfM = m.transpose.inverse()

        assertTrue(
            transposeOfInverseOfM.isCloseTo(inverseTransposeOfM),
            """
                Expected matrices to be equal (within margin for rounding error).
                Expected:
                $inverseTransposeOfM
                Actual:
                $transposeOfInverseOfM
            """.trimIndent()
        )

        assertTrue(
            inverseOfTransposeOfM.isCloseTo(inverseTransposeOfM),
            """
                Expected matrices to be equal (within margin for rounding error).
                Expected:
                $inverseTransposeOfM
                Actual:
                $inverseOfTransposeOfM
            """.trimIndent()
        )
    }
}
