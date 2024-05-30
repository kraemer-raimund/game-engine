package dev.rakrae.gameengine.core

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

internal class GameLoop(
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

        GlobalScope.run {
            launch {
                onStart()
                while (isRunning) {
                    onRender()
                    if (isPaused) {
                        onPause()
                        while (isPaused) {
                            onRender()
                        }
                        onResume()
                    }
                    onTick()
                }
                onStop()
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
