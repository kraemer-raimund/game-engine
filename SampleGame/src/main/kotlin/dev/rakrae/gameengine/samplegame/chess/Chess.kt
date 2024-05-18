package dev.rakrae.gameengine.samplegame.chess

import dev.rakrae.gameengine.core.Game
import dev.rakrae.gameengine.core.GameTime
import dev.rakrae.gameengine.input.Input
import dev.rakrae.gameengine.math.Vec3f
import dev.rakrae.gameengine.samplegame.chess.levels.ChessExampleLevel
import dev.rakrae.gameengine.scene.Scene
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext
import kotlin.time.Duration.Companion.seconds

class Chess : Game() {

    override val title = "Chess (Sample Game)"

    override val scene: Scene by lazy {
        val level = ChessExampleLevel()
        Scene(level.nodes)
    }

    private var fpsCounterCoroutine: Job? = null

    override suspend fun onStart() {
        println("Game started")
        scene.activeCamera.translate(Vec3f(0f, 1.8f, 0f))

        startFpsCounterCoroutine()
    }

    override suspend fun onTick() {
        val moveSpeed = 3f
        val forwardOffset = moveSpeed * GameTime.deltaTime * Input.axisPair1.x
        val sidewaysOffset = moveSpeed * GameTime.deltaTime * Input.axisPair1.y
        scene.activeCamera.translate(Vec3f(1f, 0f, 0f) * forwardOffset)
        scene.activeCamera.translate(Vec3f(0f, 0f, 1f) * sidewaysOffset)

        val mouseSensitivity = 0.12f
        scene.activeCamera.rotate(Vec3f(0f, -1f, 0f) * (mouseSensitivity * Input.axisPair2.x))
        scene.activeCamera.rotate(Vec3f(-1f, 0f, 0f) * (mouseSensitivity * Input.axisPair2.y))
    }

    override suspend fun onPause() {
        println("Game paused")
    }

    override suspend fun onResume() {
        println("Game resumed")
    }

    override suspend fun onStop() {
        println("Game stopped")
        fpsCounterCoroutine?.cancel()
    }

    /**
     * Print the current frame rate to the console in regular intervals.
     */
    private suspend fun startFpsCounterCoroutine() {
        fpsCounterCoroutine = CoroutineScope(coroutineContext).launch {
            while (true) {
                delay(0.5.seconds)
                println("FPS: ${GameTime.currentFps}")
            }
        }
    }
}
