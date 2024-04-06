package dev.rakrae.gameengine.core

import dev.rakrae.gameengine.graphics.Bitmap
import dev.rakrae.gameengine.graphics.Display
import dev.rakrae.gameengine.graphics.ScreenSize
import dev.rakrae.gameengine.graphics.SpriteRenderer

class Engine(private val game: IGame) {

    private val defaultScreenSize = ScreenSize(DEFAULT_WIDTH, DEFAULT_HEIGHT)
    private val display = Display(TITLE, defaultScreenSize)
    private val screen = Bitmap(defaultScreenSize.width, defaultScreenSize.height)
    private val spriteRenderer = SpriteRenderer()

    private val gameTime = GameTime()
    private val fpsCounter = FpsCounter()
    private val gameLoop = GameLoop(
        onTick = {
            gameTime.onTick()
            fpsCounter.onTick()
            game.onTick()
        },
        onRender = {
            screen.clear()
            spriteRenderer.render(screen, game)
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
