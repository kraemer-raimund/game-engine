package com.rk.mykotlingameengine.core

import com.rk.mykotlingameengine.graphics.Bitmap
import com.rk.mykotlingameengine.graphics.Display
import com.rk.mykotlingameengine.graphics.Renderer
import java.awt.Dimension
import kotlin.random.Random

class Engine(private val game: IGame) {

    private val defaultScreenDimension = Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT)
    private val display = Display(TITLE, defaultScreenDimension)
    private val renderer = Renderer()
    private val screen = Bitmap(DEFAULT_WIDTH, DEFAULT_HEIGHT)
    private val gameLoop = GameLoop(
        onTick = game::tick,
        onRender = {
            renderer.draw(screen, generateRandomSprite(), 0, 0)
            display.displayPixels(screen)
        }
    )

    fun start() {
        gameLoop.start()
    }

    private fun generateRandomSprite(): Bitmap {
        val sprite = Bitmap(256, 256)
        for (i in 0 until sprite.width * sprite.height) {
            sprite.pixels[i] = Random.nextInt()
        }
        return sprite
    }

    companion object {
        private const val DEFAULT_WIDTH = 800
        private const val DEFAULT_HEIGHT = 600
        private const val TITLE = "MyKotlinGameEngine"
    }
}