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

class Engine(private val game: Game) {

    private val awtMouseInputAdapter = AwtMouseInputAdapter()
    internal val window: Window = initWindow(awtMouseInputAdapter)

    private val renderer = Renderer()
    private val spriteRenderer = SpriteRenderer()
    private val gameTime = GameTime()
    private val gameLoop = GameLoop(
        onStart = ::onStart,
        onTick = ::onTick,
        onRender = ::onRender,
        onPause = ::onPause,
        onResume = ::onResume,
        onStop = ::onStop
    )

    private var displayBuffer = with(renderResolution) { Bitmap(width, height) }

    internal fun start() = gameLoop.start()

    internal fun stop() = gameLoop.stop()

    private suspend fun onStart() = game.onStart()

    private suspend fun onTick() {
        gameTime.onTick()
        awtMouseInputAdapter.tick()
        game.onTick()
    }

    private suspend fun onRender() {
        gameTime.onRender()
        val activeCamera = game.scene.activeCamera
        displayBuffer = with(renderResolution) { Bitmap(width, height) }
            .apply { clear(Color(0u, 0u, 0u, 255u)) }
        renderer.render(game.scene, activeCamera.renderbuffer)
        spriteRenderer.draw(displayBuffer, activeCamera.renderbuffer, activeCamera.viewportOffset)
        window.displayPixels(displayBuffer)
    }

    private suspend fun onPause() = game.onPause()

    private suspend fun onResume() = game.onResume()

    private suspend fun onStop() {
        game.onStop()
        delay(0.5.seconds)
        displayBuffer.clear()
        window.exit()
    }

    private fun initWindow(mouseInputAdapter: AwtMouseInputAdapter): Window {
        return SwingWindow(game.title, renderResolution)
            .also { swingWindow ->
                AwtKeyboardInputAdapter().also { awtKeyboardInputAdapter ->
                    swingWindow.container.addKeyListener(awtKeyboardInputAdapter)
                    Input.axisPairProvider1 = awtKeyboardInputAdapter
                }

                mouseInputAdapter.also { awtMouseInputAdapter ->
                    swingWindow.container.addMouseMotionListener(awtMouseInputAdapter)
                    Input.axisPairProvider2 = awtMouseInputAdapter
                }
                Companion.window = swingWindow
            }
    }

    companion object {
        private val renderResolution = ScreenSize(1280, 720)

        private lateinit var window: Window

        val screenSize: ScreenSize
            get() = renderResolution
    }
}
