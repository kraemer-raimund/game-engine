package dev.rakrae.gameengine.core

import dev.rakrae.gameengine.graphics.Bitmap
import dev.rakrae.gameengine.graphics.ScreenSize
import dev.rakrae.gameengine.graphics.SpriteRenderer
import dev.rakrae.gameengine.graphics.pipeline.Renderer
import dev.rakrae.gameengine.input.Input
import dev.rakrae.gameengine.platform.AwtKeyboardInputAdapter
import dev.rakrae.gameengine.platform.AwtMouseInputAdapter
import dev.rakrae.gameengine.platform.SwingWindow
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

class Engine(game: Game) {

    private val defaultScreenSize = ScreenSize(DEFAULT_WIDTH, DEFAULT_HEIGHT)
    private val swingWindow = SwingWindow(game.title, defaultScreenSize)

    private val awtKeyboardInputAdapter = AwtKeyboardInputAdapter()
    private val awtMouseInputAdapter = AwtMouseInputAdapter()

    init {
        swingWindow.container.addKeyListener(awtKeyboardInputAdapter)
        Input.axisPairProvider1 = awtKeyboardInputAdapter

        swingWindow.container.addMouseMotionListener(awtMouseInputAdapter)
        Input.axisPairProvider2 = awtMouseInputAdapter
    }

    private val window: Window = swingWindow
    private val screen = Bitmap(defaultScreenSize.width, defaultScreenSize.height)
    private val renderer = Renderer()
    private val spriteRenderer = SpriteRenderer()

    private val gameTime = GameTime()
    private val fpsCounter = FpsCounter()

    private val gameLoop = GameLoop(
        onStart = suspend {
            game.onStart()
        },
        onTick = suspend {
            gameTime.onTick()
            awtMouseInputAdapter.tick()
            game.onTick()
        },
        onRender = suspend {
            gameTime.onRender()
            fpsCounter.onRenderFrame()
            screen.clear()
            renderer.render(game.scene, screen)
            spriteRenderer.render(screen)
            window.displayPixels(screen)
        },
        onPause = suspend {
            game.onPause()
        },
        onResume = suspend {
            game.onResume()
        },
        onStop = suspend {
            game.onStop()
            delay(0.5.seconds)
            screen.clear()
        }
    )

    fun start() {
        gameLoop.start()
    }

    companion object {
        private const val DEFAULT_WIDTH = 1920
        private const val DEFAULT_HEIGHT = 1080

        val aspectRatio: Float
            get() = DEFAULT_WIDTH / DEFAULT_HEIGHT.toFloat()
    }
}
