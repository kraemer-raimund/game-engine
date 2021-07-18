package com.rk.mykotlingameengine

import java.awt.Canvas
import java.awt.Dimension
import java.lang.Exception
import javax.swing.JFrame
import kotlin.system.exitProcess

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

    private var isRunning: Boolean = false
    private var thread: Thread? = null

    fun start() {
        if (isRunning) { return }
        isRunning = true
        thread = Thread(::run).apply { start() }
    }

    private fun stop() {
        if (!isRunning) { return }
        isRunning = false

        try {
            thread?.join()
        } catch (e: Exception) {
            print(e)
        } finally {
            exitProcess(0)
        }
    }

    private fun run() {
        while (isRunning) {
            println("running")
        }
    }

    companion object {
        private const val WIDTH = 800
        private const val HEIGHT = 600
        private const val TITLE = "MyKotlinGameEngine"
    }
}
