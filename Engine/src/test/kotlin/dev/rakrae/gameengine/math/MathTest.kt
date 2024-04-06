package dev.rakrae.gameengine.math

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@DisplayName("Math")
internal class MathTest {

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class Abs {

        private fun absFloatTestArguments() : Stream<Arguments> {
            return Stream.of(
                Arguments.of(-1.23f, 1.23f),
                Arguments.of(-10000.42f, 10000.42f),
                Arguments.of(2500f, 2500f),
                Arguments.of(0f, 0f),
                Arguments.of(-0f, 0f)
            )
        }

        @ParameterizedTest
        @MethodSource("absFloatTestArguments")
        fun `returns the absolute value`(v: Float, expected: Float) {
            assertThat(abs(v)).isEqualTo(expected)
        }

        private fun absIntTestArguments() : Stream<Arguments> {
            return Stream.of(
                Arguments.of(-1, 1),
                Arguments.of(-10000, 10000),
                Arguments.of(2500, 2500),
                Arguments.of(0, 0),
                Arguments.of(-0, 0)
            )
        }

        @ParameterizedTest
        @MethodSource("absIntTestArguments")
        fun `returns the absolute value`(v: Int, expected: Int) {
            assertThat(abs(v)).isEqualTo(expected)
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class Min {

        private fun minTestArguments() : Stream<Arguments> {
            return Stream.of(
                Arguments.of(13f, 37f, 13f),
                Arguments.of(500f, -500f, -500f),
                Arguments.of(0f, -0f, 0f),
                Arguments.of(-0f, 0f, -0f)
            )
        }

        @ParameterizedTest
        @MethodSource("minTestArguments")
        fun `returns the smaller value`(v1: Float, v2: Float, expected: Float) {
            assertThat(min(v1, v2)).isEqualTo(expected)
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class Max {

        private fun maxTestArguments() : Stream<Arguments> {
            return Stream.of(
                Arguments.of(13f, 37f, 37f),
                Arguments.of(500f, -500f, 500f),
                Arguments.of(0f, -0f, 0f),
                Arguments.of(-0f, 0f, -0f)
            )
        }

        @ParameterizedTest
        @MethodSource("maxTestArguments")
        fun `returns the larger value`(v1: Float, v2: Float, expected: Float) {
            assertThat(max(v1, v2)).isEqualTo(expected)
        }
    }

    @Nested
    @DisplayName("clamping value")
    inner class Clamp {

        @Test
        fun `within range preserves the value`() {
            val clamped = clamp(42.0f, 30.0f, 50.0f)
            assertThat(clamped).isEqualTo(42.0f)
        }

        @Test
        fun `less than min value results in min value`() {
            val clamped = clamp(13.0f, 30.0f, 50.0f)
            assertThat(clamped).isEqualTo(30.0f)
        }

        @Test
        fun `greater than max value results in max value`() {
            val clamped = clamp(1337.0f, 30.0f, 50.0f)
            assertThat(clamped).isEqualTo(50.0f)
        }

        @Test
        fun `below negative lower bound results in that negative lower bound`() {
            val clamped = clamp(-900000.0f, -100.0f, 50000.0f)
            assertThat(clamped).isEqualTo(-100.0f)
        }
    }
}
