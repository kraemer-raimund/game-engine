package dev.rakrae.gameengine.core

import dev.rakrae.gameengine.graphics.Bitmap
import dev.rakrae.gameengine.graphics.ScreenSize
import dev.rakrae.gameengine.graphics.rendering.Renderer
import dev.rakrae.gameengine.graphics.rendering.SpriteRenderer
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

        window = swingWindow
    }

    private var displayBuffer = Bitmap(defaultScreenSize.width, defaultScreenSize.height)
    private val renderer = Renderer()
    private val spriteRenderer = SpriteRenderer()

    private val gameTime = GameTime()

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
            displayBuffer = Bitmap(window.size.width, window.size.height)
            renderer.render(game.scene, displayBuffer)
            spriteRenderer.render(displayBuffer)
            window.displayPixels(displayBuffer)
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
            displayBuffer.clear()
            window.exit()
        }
    )

    internal fun start() {
        gameLoop.start()
    }

    internal fun stop() {
        gameLoop.stop()
    }

    companion object {
        private const val DEFAULT_WIDTH = 1920
        private const val DEFAULT_HEIGHT = 1080

        private lateinit var window: Window
        val activeWindow get() = window

        val aspectRatio: Float
            get() = window.size.width / window.size.height.toFloat()
    }
}
