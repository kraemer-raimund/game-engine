package com.rk.mykotlingameengine.core

import com.rk.mykotlingameengine.graphics.Display
import com.rk.mykotlingameengine.graphics.Renderer
import com.rk.mykotlingameengine.graphics.ScreenSize

class Engine(private val game: IGame) {

    private val defaultScreenSize = ScreenSize(DEFAULT_WIDTH, DEFAULT_HEIGHT)
    private val display = Display(TITLE, defaultScreenSize)
    private val renderer = Renderer(defaultScreenSize)
    private val gameLoop = GameLoop(
        onTick = game::tick,
        onRender = {
            val screen = renderer.render(game)
            display.displayPixels(screen)
        }
    )

    fun start() {
        gameLoop.start()
    }

    companion object {
        private const val DEFAULT_WIDTH = 800
        private const val DEFAULT_HEIGHT = 600
        private const val TITLE = "MyKotlinGameEngine"
    }
}