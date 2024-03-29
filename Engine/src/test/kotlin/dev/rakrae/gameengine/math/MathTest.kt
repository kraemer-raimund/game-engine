package dev.rakrae.gameengine.math

import kotlin.test.Test
import kotlin.test.assertEquals

internal class MathTest {

    @Test
    fun `clamping value within range preserves the value`() {
        val clamped = clamp(42.0f, 30.0f, 50.0f)

        assertEquals(clamped, 42.0f)
    }
}
