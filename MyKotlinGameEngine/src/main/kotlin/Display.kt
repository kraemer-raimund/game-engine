package com.rk.mykotlingameengine

import com.rk.mykotlingameengine.graphics.Renderer
import java.awt.Canvas
import java.awt.Dimension
import java.lang.Exception
import javax.swing.JFrame
import kotlin.system.exitProcess

class Display(private val frame: JFrame) : Canvas() {

    private var isRunning: Boolean = false
    private val thread: Thread
    private val renderer: Renderer

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
        thread = Thread(::run)
        renderer = Renderer(WIDTH, HEIGHT)
    }

    fun start() {
        if (isRunning) { return }
        isRunning = true
        thread.start()
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
            tick()
            render()
        }
    }

    private fun tick() {
        println("tick")
    }

    private fun render() {
        println("render")
    }

    companion object {
        private const val WIDTH = 800
        private const val HEIGHT = 600
        private const val TITLE = "MyKotlinGameEngine"
    }
}
