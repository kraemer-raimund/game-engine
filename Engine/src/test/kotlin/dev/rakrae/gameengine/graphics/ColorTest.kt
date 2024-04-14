package dev.rakrae.gameengine.graphics

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@DisplayName("Colors")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class ColorTest {

    private fun colorToIntTestArguments(): Stream<Arguments> {
        return Stream.of(
            arguments(Color(0u, 0u, 0u, 0u), 0),
            arguments(Color(255u, 255u, 255u, 255u), UInt.MAX_VALUE.toInt()),
            arguments(Color(0xDDu, 0x9Au, 0x22u, 0x0u), 0xDD9A2200u.toInt())
        )
    }

    @ParameterizedTest
    @MethodSource("colorToIntTestArguments")
    fun `converts to correct integer`(color: Color, expected: Int) {
        assertThat(color.intValue).isEqualTo(expected.toUInt())
    }

    private fun colorFromIntTestArguments(): Stream<Arguments> {
        return Stream.of(
            arguments(0, Color(0u, 0u, 0u, 0u)),
            arguments(UInt.MAX_VALUE.toInt(), Color(255u, 255u, 255u, 255u)),
            arguments(0xDD9A2200u.toInt(), Color(0xDDu, 0x9Au, 0x22u, 0x0u))
        )
    }

    @ParameterizedTest
    @MethodSource("colorFromIntTestArguments")
    fun `converts to correct integer`(colorAsInt: Int, expected: Color) {
        assertThat(Color.from(colorAsInt.toUInt())).isEqualTo(expected)
    }
}
