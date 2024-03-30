package dev.rakrae.gameengine.math

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals

@DisplayName("Math")
internal class MathTest {

    @Nested
    @DisplayName("clamping value")
    inner class Clamp {

        @Test
        fun `within range preserves the value`() {
            val clamped = clamp(42.0f, 30.0f, 50.0f)
            assertEquals(42.0f, clamped)
        }

        @Test
        fun `less than min value results in min value`() {
            val clamped = clamp(13.0f, 30.0f, 50.0f)
            assertEquals(30.0f, clamped)
        }

        @Test
        fun `greater than max value results in max value`() {
            val clamped = clamp(1337.0f, 30.0f, 50.0f)
            assertEquals(50.0f, clamped)
        }
    }
}
