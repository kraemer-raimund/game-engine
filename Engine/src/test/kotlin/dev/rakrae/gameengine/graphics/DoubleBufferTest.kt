package dev.rakrae.gameengine.graphics

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class DoubleBufferTest {

    @Test
    fun `reads and writes use different buffers before buffer swap`() {
        val doubleBuffer = DoubleBufferedBitmap(width = 69, height = 42, initialColor = Color.black)

        doubleBuffer.writePixel(13, 37, Color.blue)

        assertThat(doubleBuffer.readPixel(13, 37)).isEqualTo(Color.black)
    }

    @Test
    fun `after buffer swap the previously written pixels become available for reading`() {
        val doubleBuffer = DoubleBufferedBitmap(width = 21, height = 12, initialColor = Color.white)

        doubleBuffer.writePixel(4, 7, Color.blue)
        doubleBuffer.swap()

        assertThat(doubleBuffer.readPixel(4, 7)).isEqualTo(Color.blue)
        assertThat(doubleBuffer.readPixel(5, 2)).isEqualTo(Color.white)
    }

    @Test
    fun `the back buffer can be cleared`() {
        val doubleBuffer = DoubleBufferedBitmap(width = 10, height = 18, initialColor = Color.black)

        doubleBuffer.writePixel(4, 7, Color.blue)
        doubleBuffer.clearBackBuffer(Color.black)
        doubleBuffer.swap()

        assertThat(doubleBuffer.readPixel(4, 7)).isEqualTo(Color.black)
    }
}
