package dev.rakrae.gameengine.core

import dev.rakrae.gameengine.graphics.Bitmap
import dev.rakrae.gameengine.graphics.Color
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

    private val awtMouseInputAdapter = AwtMouseInputAdapter()
    val window: Window = SwingWindow(game.title, renderResolution)
        .also { swingWindow ->
            AwtKeyboardInputAdapter().also { awtKeyboardInputAdapter ->
                swingWindow.container.addKeyListener(awtKeyboardInputAdapter)
                Input.axisPairProvider1 = awtKeyboardInputAdapter
            }

            awtMouseInputAdapter.also { awtMouseInputAdapter ->
                swingWindow.container.addMouseMotionListener(awtMouseInputAdapter)
                Input.axisPairProvider2 = awtMouseInputAdapter
            }
            Companion.window = swingWindow
        }

    private var displayBuffer = with(renderResolution) { Bitmap(width, height) }
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
            val activeCamera = game.scene.activeCamera
            displayBuffer = with(renderResolution) { Bitmap(width, height) }
                .apply { clear(Color(0u, 0u, 0u, 255u)) }
            val viewportBuffer = with(activeCamera.viewportSize) { Bitmap(x, y) }
            renderer.render(game.scene, viewportBuffer)
            spriteRenderer.draw(displayBuffer, viewportBuffer, activeCamera.viewportOffset)
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
        private val renderResolution = ScreenSize(1280, 720)

        private lateinit var window: Window

        val screenSize: ScreenSize
            get() = renderResolution
    }
}
