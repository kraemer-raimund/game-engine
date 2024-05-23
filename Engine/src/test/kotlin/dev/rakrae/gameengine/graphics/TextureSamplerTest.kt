package dev.rakrae.gameengine.graphics

import dev.rakrae.gameengine.math.Vec2f
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class TextureSamplerTest {

    private fun filterByNearestTestArgs(): Stream<Arguments> {
        return Stream.of(
            arguments(Vec2f(0.45f, 0.69f), Color.blue),
            arguments(Vec2f(0.36f, 0.71f), Color.white),
            arguments(Vec2f(0.43f, 0.64f), Color.black),
        )
    }

    @ParameterizedTest
    @MethodSource("filterByNearestTestArgs")
    fun `filter by nearest`(uv: Vec2f, expectedColor: Color) {
        val sampler = TextureSampler(TextureSampler.Filter.NEAREST)
        val bitmap = Bitmap(10, 20, Color.black).apply {
            setPixel(3, 13, Color.white)
            setPixel(4, 13, Color.blue)
        }

        assertThat(sampler.sample(bitmap, uv)).isEqualTo(expectedColor)
    }

    private fun filterLinearlyTestArgs(): Stream<Arguments> {
        return Stream.of(
            arguments(Vec2f(0.20f, 0.69f), Color.black),
            arguments(Vec2f(0.45f, 0.72f), Color(r = 0x59, g = 0x51, b = 0xF2, a = 0xFF)),
            arguments(Vec2f(0.42f, 0.65f), Color(r = 0x45, g = 0x45, b = 0x45, a = 0xFF)),
            arguments(Vec2f(0.48f, 0.7f), Color(r = 0xCA, g = 0xB2, b = 0xAD, a = 0xFF)),
            arguments(Vec2f(0.51f, 0.68f), Color(r = 0xEA, g = 0xEA, b = 0x5F, a = 0xFF)),
            arguments(Vec2f(0.4f, 0.71f), Color(r = 0x4E, g = 0x4E, b = 0x99, a = 0xFF)),
            arguments(Vec2f(0.38f, 0.69f), Color(r = 0x5F, g = 0x5F, b = 0x6B, a = 0xFF)),
            arguments(Vec2f(0.44f, 0.69f), Color(r = 0xD9, g = 0xD9, b = 0xF4, a = 0xFF)),
        )
    }

    @ParameterizedTest
    @MethodSource("filterLinearlyTestArgs")
    fun `filter linearly`(uv: Vec2f, expectedColor: Color) {
        val sampler = TextureSampler(TextureSampler.Filter.LINEAR)
        val bitmap = Bitmap(10, 20, Color.black).apply {
            setPixel(4, 13, Color.white)
            setPixel(5, 13, Color.yellow)
            setPixel(4, 14, Color.blue)
            setPixel(5, 14, Color.red)
        }

        assertThat(sampler.sample(bitmap, uv)).isEqualTo(expectedColor)
    }
}
