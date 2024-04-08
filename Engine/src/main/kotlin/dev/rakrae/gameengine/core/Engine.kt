package dev.rakrae.gameengine.core

import dev.rakrae.gameengine.graphics.*

class Engine(game: Game) {

    private val defaultScreenSize = ScreenSize(DEFAULT_WIDTH, DEFAULT_HEIGHT)
    private val display = Display(TITLE, defaultScreenSize)
    private val screen = Bitmap(defaultScreenSize.width, defaultScreenSize.height)
    private val rasterizer = Rasterizer()
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
            for (mesh in game.meshes) {
                rasterizer.render(mesh, screen)
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
        private const val TITLE = "MyKotlinGameEngine"
    }
}
