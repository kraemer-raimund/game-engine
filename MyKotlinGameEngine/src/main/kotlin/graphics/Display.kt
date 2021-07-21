package com.rk.mykotlingameengine.graphics

import java.awt.Canvas
import java.awt.Dimension
import java.awt.image.BufferedImage
import java.awt.image.DataBufferInt
import javax.swing.JFrame

class Display(title: String, dimension: Dimension) : Canvas() {

    private val frame = JFrame()
    private val bufferedImage = BufferedImage(
        dimension.width,
        dimension.height,
        BufferedImage.TYPE_INT_RGB
    )
    private val pixels = bufferedImage.run {
        val dataBuffer = raster.dataBuffer as DataBufferInt
        dataBuffer.data
    }

    init {
        frame.apply {
            add(this@Display)
            pack()
            this.title = title
            size = dimension
            setLocationRelativeTo(null)
            isResizable = true
            isVisible = true
            defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        }
    }

    fun displayPixels(bitmap: Bitmap) {
        val bufferStrategy = bufferStrategy
        if (bufferStrategy == null) {
            createBufferStrategy(NUMBER_OF_BUFFERS)
            return
        }

        for (i in 0 until bitmap.width * bitmap.height) {
            pixels[i] = bitmap.pixels[i]
        }

        bufferStrategy.drawGraphics.apply {
            drawImage(
                bufferedImage,
                0,
                0,
                bitmap.width,
                bitmap.height,
                null
            )
            dispose()
        }

        bufferStrategy.show()
    }

    companion object {
        const val NUMBER_OF_BUFFERS = 3
    }
}