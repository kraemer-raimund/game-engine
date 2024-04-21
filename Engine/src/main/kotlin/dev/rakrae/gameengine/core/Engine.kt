package dev.rakrae.gameengine.core

import dev.rakrae.gameengine.graphics.Bitmap
import dev.rakrae.gameengine.graphics.ScreenSize
import dev.rakrae.gameengine.graphics.SpriteRenderer
import dev.rakrae.gameengine.graphics.pipeline.Renderer
import dev.rakrae.gameengine.platform.Display

class Engine(game: Game) {

    private val defaultScreenSize = ScreenSize(DEFAULT_WIDTH, DEFAULT_HEIGHT)
    private val display = Display(game.title, defaultScreenSize)
    private val screen = Bitmap(defaultScreenSize.width, defaultScreenSize.height)
    private val renderer = Renderer()
    private val spriteRenderer = SpriteRenderer()

    private val gameTime = GameTime()
    private val fpsCounter = FpsCounter()
    private val gameLoop = GameLoop(
        onTick = suspend {
            gameTime.onTick()
            game.onTick()
        },
        onRender = suspend {
            fpsCounter.onTick()
            screen.clear()
            renderer.render(game.scene, screen)
            spriteRenderer.render(screen)
            display.displayPixels(screen)
        }
    )

    fun start() {
        gameLoop.start()
    }

    companion object {
        private const val DEFAULT_WIDTH = 1000
        private const val DEFAULT_HEIGHT = 1000
    }
}
