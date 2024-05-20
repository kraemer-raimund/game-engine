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

class Engine(private val game: Game) {

    private val gameLifeCycleReceivers = mutableListOf<GameLifeCycleReceiver>(game)
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

    internal val window: Window = initWindow()

    internal fun start() = gameLoop.start()

    internal fun stop() = gameLoop.stop()

    internal fun registerLifeCycleReceiver(lifeCycleReceiver: GameLifeCycleReceiver): Boolean {
        return gameLifeCycleReceivers.add(lifeCycleReceiver)
    }

    internal fun deregisterLifeCycleReceiver(lifeCycleReceiver: GameLifeCycleReceiver): Boolean {
        return gameLifeCycleReceivers.remove(lifeCycleReceiver)
    }

    private suspend fun onStart() = gameLifeCycleReceivers.forEach { it.onStart() }

    private suspend fun onTick() {
        gameTime.onTick()
        gameLifeCycleReceivers.forEach { it.onTick() }
    }

    private suspend fun onRender() {
        gameTime.onRender()
        val displayBuffer = with(renderResolution) { Bitmap(width, height) }
            .apply { clear(Color(0u, 0u, 0u, 255u)) }
        game.scene.cameras.forEach { camera ->
            renderer.render(camera, game.scene)
            spriteRenderer.draw(displayBuffer, camera.renderBuffer, camera.viewportOffset)
        }
        window.displayPixels(displayBuffer)
    }

    private suspend fun onPause() = gameLifeCycleReceivers.forEach { it.onPause() }

    private suspend fun onResume() = gameLifeCycleReceivers.forEach { it.onResume() }

    private suspend fun onStop() {
        gameLifeCycleReceivers.forEach { it.onStop() }
        window.exit()
    }

    private fun initWindow(): Window {
        return SwingWindow(game.title, renderResolution)
            .also { swingWindow ->
                AwtKeyboardInputAdapter().also { awtKeyboardInputAdapter ->
                    swingWindow.container.addKeyListener(awtKeyboardInputAdapter)
                    Input.axisPairProvider1 = awtKeyboardInputAdapter
                }

                val awtMouseInputAdapter = AwtMouseInputAdapter().also { awtMouseInputAdapter ->
                    swingWindow.container.addMouseMotionListener(awtMouseInputAdapter)
                    Input.axisPairProvider2 = awtMouseInputAdapter
                }
                registerLifeCycleReceiver(awtMouseInputAdapter)
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
