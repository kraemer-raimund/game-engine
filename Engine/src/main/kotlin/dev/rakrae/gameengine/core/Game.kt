package dev.rakrae.gameengine.core

import dev.rakrae.gameengine.scene.Scene

interface Game {

    val title: String
    val scene: Scene

    /**
     * Runs once before the first tick.
     */
    suspend fun onStart()

    /**
     * Runs once per logical tick (independent of the rendering frame rate), as often per second
     * as the platform and hardware allows.
     */
    suspend fun onTick()

    /**
     * Runs once each time the game is paused.
     */
    suspend fun onPause()

    /**
     * Runs once each time the game is resumed after being paused.
     */
    suspend fun onResume()

    /**
     * Runs once after the last tick before shutting down.
     */
    suspend fun onStop()
}
