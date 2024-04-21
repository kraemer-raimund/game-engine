package dev.rakrae.gameengine.core

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield

class GameLoop(
    val onStart: suspend () -> Unit,
    val onTick: suspend () -> Unit,
    val onRender: suspend () -> Unit,
    val onPause: suspend () -> Unit,
    val onResume: suspend () -> Unit,
    val onStop: suspend () -> Unit
) {

    private var isRunning: Boolean = false
    private var isPaused: Boolean = false

    @OptIn(DelicateCoroutinesApi::class)
    fun start() {
        isRunning = true

        GlobalScope.launch {
            launch {
                onStart()
                while (isRunning) {
                    if (isPaused) {
                        onPause()
                        while (isPaused) yield()
                        onResume()
                    }
                    onTick()
                }
                onStop()
            }
            launch {
                while (isRunning) {
                    onRender()
                }
            }
        }
    }

    fun pause() {
        isPaused = true
    }

    fun resume() {
        isPaused = false
    }

    /**
     * Gracefully finish the game loop and shut down the game.
     */
    fun stop() {
        isRunning = false
    }
}
