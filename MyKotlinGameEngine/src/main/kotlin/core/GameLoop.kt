package com.rk.mykotlingameengine.core

import com.rk.mykotlingameengine.graphics.Renderer

class GameLoop(val onTick: () -> Unit, val onRender: () -> Unit) {

    private var isRunning: Boolean = false
    private val thread: Thread
    private val renderer: Renderer

    init {
        thread = Thread(::run)
        renderer = Renderer()
    }

    @Synchronized
    fun start() {
        if (isRunning) { return }
        isRunning = true
        thread.start()
    }

    @Synchronized
    fun stop() {
        if (!isRunning) { return }
        isRunning = false
        try {
            thread.join()
        } catch (e: Exception) {
            print(e)
        }
    }

    private fun run() {
        while (isRunning) {
            onTick()
            onRender()
        }
    }
}
