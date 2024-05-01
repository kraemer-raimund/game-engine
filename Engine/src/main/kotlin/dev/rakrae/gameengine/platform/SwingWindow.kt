package dev.rakrae.gameengine.platform

import dev.rakrae.gameengine.core.Window
import dev.rakrae.gameengine.graphics.Bitmap
import dev.rakrae.gameengine.graphics.ScreenSize
import java.awt.Canvas
import java.awt.Dimension
import java.awt.image.BufferedImage
import java.awt.image.DataBufferInt
import javax.swing.JFrame

/**
 * The window with the rendered pixels visible to the player.
 */
internal class SwingWindow(title: String, screenSize: ScreenSize) : Window {

    internal val container get() = canvas
    private val frame = JFrame()
    private val canvas = Canvas()
    private val bufferedImage = BufferedImage(
        screenSize.width,
        screenSize.height,
        BufferedImage.TYPE_INT_RGB
    )
    private val pixels = bufferedImage.run {
        val dataBuffer = raster.dataBuffer as DataBufferInt
        dataBuffer.data
    }

    init {
        frame.apply {
            add(canvas)
            pack()
            this.title = title
            size = Dimension(screenSize.width, screenSize.height)
            setLocationRelativeTo(null)
            isResizable = true
            isVisible = true
            defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        }
    }

    override fun displayPixels(bitmap: Bitmap) {
        val bufferStrategy = canvas.bufferStrategy
        if (bufferStrategy == null) {
            canvas.createBufferStrategy(NUMBER_OF_BUFFERS)
            return
        }

        val flippedVertically = flipVertically(bitmap)
        for (i in 0 until flippedVertically.width * flippedVertically.height) {
            pixels[i] = flippedVertically.pixels[i]
        }

        bufferStrategy.drawGraphics.apply {
            drawImage(
                bufferedImage,
                0,
                0,
                canvas.width,
                canvas.height,
                null
            )
            dispose()
        }

        bufferStrategy.show()
    }

    /**
     * AWT considers the y-axis to go top-down, but all of our calculations in 2D/screen coordinates
     * assume the y-axis to got bottom-up.
     */
    private fun flipVertically(bitmap: Bitmap): Bitmap {
        val flipped = Bitmap(bitmap.width, bitmap.height)
        for (x in 0..<bitmap.width) {
            for (y in 0..<bitmap.height) {
                flipped.setPixelIfInBounds(x, flipped.height - y, bitmap.getPixel(x, y))
            }
        }
        return flipped
    }

    companion object {
        const val NUMBER_OF_BUFFERS = 3
    }
}
