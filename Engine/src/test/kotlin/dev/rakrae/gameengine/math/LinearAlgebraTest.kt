package dev.rakrae.gameengine.math

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.offset
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@DisplayName("Linear Algebra")
internal class LinearAlgebraTest {

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class Vec2iTest {

        private fun vectorAdditionTestArguments(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(Vec2i(0, 0), Vec2i(0, 0), Vec2i(0, 0)),
                Arguments.of(Vec2i(1, 0), Vec2i(0, 0), Vec2i(1, 0)),
                Arguments.of(Vec2i(-4, 16), Vec2i(13, 37), Vec2i(9, 53)),
                Arguments.of(Vec2i(-42, -69), Vec2i(42, 69), Vec2i(0, 0))
            )
        }

        @ParameterizedTest
        @MethodSource("vectorAdditionTestArguments")
        fun `adding two vectors`(v1: Vec2i, v2: Vec2i, expected: Vec2i) {
            assertThat(v1 + v2).isEqualTo(expected)
        }

        private fun vectorSubtractionTestArguments(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(Vec2i(0, 0), Vec2i(0, 0), Vec2i(0, 0)),
                Arguments.of(Vec2i(1, 0), Vec2i(0, 0), Vec2i(1, 0)),
                Arguments.of(Vec2i(-4, 16), Vec2i(13, 37), Vec2i(-17, -21)),
                Arguments.of(Vec2i(-42, -69), Vec2i(-42, -69), Vec2i(0, 0))
            )
        }

        @ParameterizedTest
        @MethodSource("vectorSubtractionTestArguments")
        fun `subtracting a vector from another vector`(v1: Vec2i, v2: Vec2i, expected: Vec2i) {
            assertThat(v1 - v2).isEqualTo(expected)
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class Vec3fTest {

        private fun vectorAdditionTestArguments(): Stream<Arguments> {
            return Stream.of(
                arguments(
                    Vec3f(0f, 0f, 0f),
                    Vec3f(0f, 0f, 0f),
                    Vec3f(0f, 0f, 0f)
                ),
                arguments(
                    Vec3f(1f, 0f, 0f),
                    Vec3f(0.5f, 0f, 0f),
                    Vec3f(1.5f, 0f, 0f)
                ),
                arguments(
                    Vec3f(-4.7f, 16.894f, 123.456f),
                    Vec3f(13f, 37.712f, 69.314f),
                    Vec3f(8.3f, 54.606f, 192.77f)
                ),
                arguments(
                    Vec3f(-42f, -69f, 37.42f),
                    Vec3f(42f, 69f, -37.42f),
                    Vec3f(0f, 0f, 0f)
                )
            )
        }

        @ParameterizedTest
        @MethodSource("vectorAdditionTestArguments")
        fun `adding two vectors`(v1: Vec3f, v2: Vec3f, expected: Vec3f) {
            assertTrue((v1 + v2).isCloseTo(expected, epsilon = 0.01f))
        }

        private fun vectorSubtractionTestArguments(): Stream<Arguments> {
            return Stream.of(
                arguments(
                    Vec3f(0f, 0f, 0f),
                    Vec3f(0f, 0f, 0f),
                    Vec3f(0f, 0f, 0f)
                ),
                arguments(
                    Vec3f(1f, 0f, 0f),
                    Vec3f(0.5f, 0f, 0f),
                    Vec3f(0.5f, 0f, 0f)
                ),
                arguments(
                    Vec3f(-4.7f, 16.894f, 123.456f),
                    Vec3f(13f, 37.712f, 69.314f),
                    Vec3f(-17.7f, -20.818f, 54.142f)
                ),
                arguments(
                    Vec3f(-42f, -69f, 37.42f),
                    Vec3f(-42f, -69f, 37.42f),
                    Vec3f(0f, 0f, 0f)
                )
            )
        }

        @ParameterizedTest
        @MethodSource("vectorSubtractionTestArguments")
        fun `subtracting a vector from another vector`(v1: Vec3f, v2: Vec3f, expected: Vec3f) {
            assertTrue((v1 - v2).isCloseTo(expected))
        }

        @Test
        fun `cross product of two vectors`() {
            val v1 = Vec3f(3.14f, 42.1337f, 12.4f)
            val v2 = Vec3f(-1f, 5.2f, 0f)

            val expectedCrossProduct = Vec3f(-64.48f, -12.4f, 58.46f)

            assertTrue((v1 cross v2).isCloseTo(expectedCrossProduct, epsilon = 0.01f))
        }

        @Test
        fun `dot product of two vectors`() {
            val v1 = Vec3f(3.14f, 42.1337f, 12.4f)
            val v2 = Vec3f(-1f, 5.2f, 0f)

            val expectedDotProduct = 215.9552f

            assertThat(v1 dot v2).isCloseTo(expectedDotProduct, offset(0.01f))
        }

        @Test
        fun `calculates the magnitude meaning the length of the vector`() {
            val v = Vec3f(3.14f, 42.1337f, 12.4f)
            assertThat(v.magnitude).isCloseTo(44.0325f, offset(0.01f))
        }

        @Test
        fun `normalizes the vector resulting in unit vector of same direction`() {
            val v = Vec3f(3.14f, 42.1337f, 12.4f)

            val expectedNormalized = Vec3f(0.0713f, 0.9568f, 0.2816f)

            assertTrue(v.normalized.isCloseTo(expectedNormalized, epsilon = 0.01f))
        }

        private fun vectorLerpTestArgs(): Stream<Arguments> {
            return Stream.of(
                arguments(
                    Vec3f(0f, 0f, 0f),
                    Vec3f(100f, 100f, 100f),
                    0f,
                    Vec3f(0f, 0f, 0f)
                ),
                arguments(
                    Vec3f(0f, 0f, 0f),
                    Vec3f(100f, 100f, 100f),
                    0.2f,
                    Vec3f(20f, 20f, 20f)
                ),
                arguments(
                    Vec3f(0f, 0f, 0f),
                    Vec3f(100f, 100f, 100f),
                    0.5f,
                    Vec3f(50f, 50f, 50f)
                ),
                arguments(
                    Vec3f(0f, 0f, 0f),
                    Vec3f(100f, 100f, 100f),
                    1f,
                    Vec3f(100f, 100f, 100f)
                ),
                arguments(
                    Vec3f(69f, 69f, 69f),
                    Vec3f(100f, 100f, 100f),
                    -42f,
                    Vec3f(69f, 69f, 69f)
                ),
                arguments(
                    Vec3f(69f, 69f, 69f),
                    Vec3f(100f, 100f, 100f),
                    42f,
                    Vec3f(100f, 100f, 100f),
                )
            )
        }

        @ParameterizedTest
        @MethodSource("vectorLerpTestArgs")
        fun `interpolates between two vectors`(
            v1: Vec3f,
            v2: Vec3f,
            t: Float,
            expected: Vec3f
        ) {
            val actual = Vec3f.lerp(v1, v2, t)

            assertTrue(actual.isCloseTo(expected, epsilon = 0.01f))
        }
    }
}
