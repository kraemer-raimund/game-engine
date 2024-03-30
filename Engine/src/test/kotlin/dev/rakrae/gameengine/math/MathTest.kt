package dev.rakrae.gameengine.math

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("Math")
internal class MathTest {

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
