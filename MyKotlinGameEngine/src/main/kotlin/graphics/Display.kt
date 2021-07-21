package com.rk.mykotlingameengine.graphics

import java.awt.Canvas
import java.awt.Dimension
import javax.swing.JFrame

class Display(title: String, dimension: Dimension) : Canvas() {

    private val frame = JFrame()

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
}