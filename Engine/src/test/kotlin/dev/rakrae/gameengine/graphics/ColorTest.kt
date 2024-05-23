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

    private fun colorToIntARGBTestArguments(): Stream<Arguments> {
        return Stream.of(
            arguments(Color(0u, 0u, 0u, 0u), 0),
            arguments(Color(255u, 255u, 255u, 255u), UInt.MAX_VALUE.toInt()),
            arguments(Color(0xDDu, 0x9Au, 0x22u, 0x00u), 0x00DD9A22u.toInt()),
            arguments(Color(0xDDu, 0x9Au, 0x22u, 0xDFu), 0xDFDD9A22u.toInt())
        )
    }

    @ParameterizedTest
    @MethodSource("colorToIntARGBTestArguments")
    fun `converts color to correct integer`(color: Color, expected: Int) {
        assertThat(color.asIntARGB).isEqualTo(expected.toUInt())
    }

    private fun colorFromIntARGBTestArguments(): Stream<Arguments> {
        return Stream.of(
            arguments(0, Color(0u, 0u, 0u, 0u)),
            arguments(UInt.MAX_VALUE.toInt(), Color(255u, 255u, 255u, 255u)),
            arguments(0x00DD9A22u.toInt(), Color(0xDDu, 0x9Au, 0x22u, 0x00u)),
            arguments(0xDFDD9A22u.toInt(), Color(0xDDu, 0x9Au, 0x22u, 0xDFu))
        )
    }

    @ParameterizedTest
    @MethodSource("colorFromIntARGBTestArguments")
    fun `converts to correct color from integer`(colorAsInt: Int, expected: Color) {
        assertThat(Color.fromIntARGB(colorAsInt.toUInt())).isEqualTo(expected)
    }

    private fun colorToStringTestArguments(): Stream<Arguments> {
        return Stream.of(
            arguments(Color(0u, 0u, 0u, 0u), "Color(r=0x00, g=0x00, b=0x00, a=0x00)"),
            arguments(Color(255u, 255u, 255u, 255u), "Color(r=0xFF, g=0xFF, b=0xFF, a=0xFF)"),
            arguments(Color(0xDDu, 0x9Au, 0x22u, 0x0u), "Color(r=0xDD, g=0x9A, b=0x22, a=0x00)")
        )
    }

    @ParameterizedTest
    @MethodSource("colorToStringTestArguments")
    fun `formats color as human readable string`(color: Color, expected: String) {
        assertThat(color.toString()).isEqualTo(expected)
    }
}
