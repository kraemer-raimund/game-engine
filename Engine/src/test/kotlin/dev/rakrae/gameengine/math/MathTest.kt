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
            assertEquals(clamped, 42.0f)
        }

        @Test
        fun `less than min value results in min value`() {
            val clamped = clamp(13.0f, 30.0f, 50.0f)
            assertEquals(clamped, 30.0f)
        }
    }
}
