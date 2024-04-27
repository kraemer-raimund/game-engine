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
}
