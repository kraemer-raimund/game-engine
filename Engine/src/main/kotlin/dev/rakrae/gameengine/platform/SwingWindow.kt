package dev.rakrae.gameengine.platform

import dev.rakrae.gameengine.core.Window
import dev.rakrae.gameengine.graphics.Bitmap
import dev.rakrae.gameengine.graphics.ScreenSize
import java.awt.Canvas
import java.awt.Dimension
import java.awt.Point
import java.awt.event.WindowEvent
import java.awt.image.BufferedImage
import java.awt.image.DataBufferInt
import javax.swing.JFrame

internal class SwingWindow(title: String, screenSize: ScreenSize) : Window {

    internal val container get() = canvas
    private val frame = JFrame()
    private val canvas = Canvas()
    private var bufferedImage = BufferedImage(
        screenSize.width,
        screenSize.height,
        BufferedImage.TYPE_INT_ARGB
    )
    private var currentState = Window.State.Windowed
    private var requestedState: Window.State? = null
    private var windowedSize: Dimension = screenSize.toDimension()
    private var windowedPosition: Point = Point(0, 0)

    init {
        frame.apply {
            add(canvas.also { it.size = screenSize.toDimension() })
            frame.title = title
            isResizable = true
            isUndecorated = false
            pack()
            extendedState = JFrame.NORMAL
            setLocationRelativeTo(null)
            defaultCloseOperation = JFrame.EXIT_ON_CLOSE
            isVisible = true
        }
        canvas.requestFocus()
    }

    override val size get() = canvas.size.toScreenSize()

    override fun displayPixels(bitmap: Bitmap) {
        requestedState?.let {
            currentState = it
            requestedState = null
            applyWindowSettings(currentState)
        }

        val bufferedImage = this.bufferedImage
            .takeIf { it.width == bitmap.width && it.height == bitmap.height }
            ?: BufferedImage(bitmap.width, bitmap.height, BufferedImage.TYPE_INT_ARGB)
        val pixels = (bufferedImage.raster.dataBuffer as DataBufferInt).data

        val bufferStrategy = canvas.bufferStrategy
        if (bufferStrategy == null) {
            canvas.createBufferStrategy(NUMBER_OF_BUFFERS)
            return
        }

        flipVertically(bitmap).pixels.copyInto(pixels)

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
                flipped.setPixel(x, flipped.height - y - 1, bitmap.getPixel(x, y))
            }
        }
        return flipped
    }

    override fun requestWindowState(requestedState: Window.State) {
        // Set the requested state, but don't immediately apply the respective window settings
        // in order to prevent concurrency issues. The render thread will apply the settings upon
        // rendering the next frame.
        this.requestedState = requestedState
    }

    private fun applyWindowSettings(requestedState: Window.State) {
        val requestedFullScreen = requestedState == Window.State.FullScreen
        val isAlreadyFullScreen = frame.extendedState == JFrame.MAXIMIZED_BOTH

        if (requestedFullScreen == isAlreadyFullScreen) {
            return
        }

        if (requestedFullScreen) {
            windowedSize = size.toDimension()
            windowedPosition = frame.locationOnScreen
        }

        frame.apply {
            dispose()
            isResizable = !requestedFullScreen
            isUndecorated = requestedFullScreen
            if (!requestedFullScreen) {
                canvas.size = windowedSize
            }
            pack()
            extendedState = if (requestedFullScreen) JFrame.MAXIMIZED_BOTH else JFrame.NORMAL
            if (!requestedFullScreen) {
                location = windowedPosition
            }
            isVisible = true
        }
        canvas.requestFocus()
    }

    override fun exit() {
        frame.dispatchEvent(WindowEvent(frame, WindowEvent.WINDOW_CLOSING))
    }

    private fun ScreenSize.toDimension(): Dimension {
        return Dimension(width, height)
    }

    private fun Dimension.toScreenSize(): ScreenSize {
        return ScreenSize(width, height)
    }

    companion object {
        const val NUMBER_OF_BUFFERS = 3
    }
}
