package dev.rakrae.gameengine.core

import dev.rakrae.gameengine.graphics.Bitmap
import dev.rakrae.gameengine.graphics.ScreenSize

interface Window {

    val size: ScreenSize

    fun displayPixels(bitmap: Bitmap)

    /**
     * Request changing the window state if the platform supports it. Note that some platforms
     * might either always or never display the game in full screen.
     */
    fun requestWindowState(requestedState: State)

    enum class State {
        FullScreen,
        Windowed
    }
}
