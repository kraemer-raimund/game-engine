package dev.rakrae.gameengine.core

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class GameLoop(val onTick: suspend () -> Unit, val onRender: suspend () -> Unit) {

    private var isRunning: Boolean = false

    @OptIn(DelicateCoroutinesApi::class)
    fun start() {
        isRunning = true

        GlobalScope.launch {
            launch {
                while (isRunning) {
                    onTick()
                }
            }
            launch {
                while (isRunning) {
                    onRender()
                }
            }
        }
    }

    fun stop() {
        isRunning = false
    }
}
