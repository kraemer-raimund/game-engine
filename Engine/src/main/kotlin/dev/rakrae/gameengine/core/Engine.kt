package dev.rakrae.gameengine.core

import dev.rakrae.gameengine.graphics.*
import dev.rakrae.gameengine.platform.Display
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class Engine(game: Game) {

    private val defaultScreenSize = ScreenSize(DEFAULT_WIDTH, DEFAULT_HEIGHT)
    private val display = Display(game.title, defaultScreenSize)
    private val screen = Bitmap(defaultScreenSize.width, defaultScreenSize.height)
    private val rasterizer = Rasterizer()
    private val wireframeRenderer = WireframeRenderer()
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
            runBlocking {
                for (node in game.nodes) {
                    launch(Dispatchers.IO) {
                        rasterizer.render(node, screen)
                    }
                }
            }
            spriteRenderer.render(screen)
            display.displayPixels(screen)
        }
    )

    fun start() {
        gameLoop.start()
    }

    companion object {
        private const val DEFAULT_WIDTH = 800
        private const val DEFAULT_HEIGHT = 800
    }
}
