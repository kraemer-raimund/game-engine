package dev.rakrae.gameengine.core

import dev.rakrae.gameengine.graphics.Bitmap

interface Window {

    val aspectRatio: Float

    fun displayPixels(bitmap: Bitmap)

    /**
     * Request changing the window state if the platform supports it. Note that some platforms
     * might either always or never display the game in full screen.
     */
    fun requestWindowState(requestedState: State)

    fun exit()

    enum class State {
        FullScreen,
        Windowed
    }
}
