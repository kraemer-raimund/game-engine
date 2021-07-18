package com.rk.mykotlingameengine

import java.awt.Canvas
import java.awt.Dimension
import javax.swing.JFrame

class Display(private val frame: JFrame) : Canvas() {

    init {
        frame.apply {
            add(this@Display)
            pack()
            title = TITLE
            size = Dimension(WIDTH, HEIGHT)
            setLocationRelativeTo(null)
            isResizable = true
            isVisible = true
            defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        }
    }

    fun showGameWindow() {
        frame.show()
    }

    companion object {
        private const val WIDTH = 800
        private const val HEIGHT = 600
        private const val TITLE = "MyKotlinGameEngine"
    }

}