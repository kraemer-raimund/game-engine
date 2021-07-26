package com.rk.mykotlingameengine.core

class GameLoop(val onTick: () -> Unit, val onRender: () -> Unit) {

    private var isRunning: Boolean = false
    private val thread: Thread

    init {
        thread = Thread(::run)
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
