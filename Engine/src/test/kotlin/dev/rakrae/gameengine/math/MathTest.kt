package dev.rakrae.gameengine.math

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.withPrecision
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class MathTest {

    private fun lerpTestArgs(): Stream<Arguments> {
        return Stream.of(
            arguments(0f, 100f, 0f, 0f),
            arguments(0f, 100f, 1f, 100f),
            arguments(0f, 100f, 0.5f, 50f),
            arguments(0f, 100f, 0.25f, 25f),
            arguments(0f, 100f, -69f, 0f),
            arguments(0f, 100f, 42f, 100f)
        )
    }

    @ParameterizedTest
    @MethodSource("lerpTestArgs")
    fun `interpolates between two values`(
        v1: Float,
        v2: Float,
        t: Float,
        expected: Float
    ) {
        val actual = Math.lerp(v1, v2, t)

        assertThat(actual).isEqualTo(expected, withPrecision(0.001f))
    }
}
