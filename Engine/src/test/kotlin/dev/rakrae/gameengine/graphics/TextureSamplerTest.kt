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
            arguments(Vec2f(0.39f, 0.71f), Color.white),
            arguments(Vec2f(0.43f, 0.64f), Color.black),
        )
    }

    @ParameterizedTest
    @MethodSource("filterByNearestTestArgs")
    fun `filter by nearest`(uv: Vec2f, expectedColor: Color) {
        val sampler = TextureSampler(TextureSampler.Filter.NEAREST)
        val bitmap = Bitmap(10, 20, Color.black).apply {
            setPixel(5, 14, Color.blue)
            setPixel(4, 14, Color.white)
        }

        assertThat(sampler.sample(bitmap, uv)).isEqualTo(expectedColor)
    }
}
