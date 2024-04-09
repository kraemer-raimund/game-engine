package dev.rakrae.gameengine.math

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
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
}
