package dev.rakrae.gameengine.samplegame.chess

import dev.rakrae.gameengine.core.Game
import dev.rakrae.gameengine.core.GameTime
import dev.rakrae.gameengine.core.Window
import dev.rakrae.gameengine.graphics.RenderTexture
import dev.rakrae.gameengine.graphics.rendering.shaders.DepthOfFieldPostProcessingShader
import dev.rakrae.gameengine.input.Input
import dev.rakrae.gameengine.math.Vec2f
import dev.rakrae.gameengine.math.Vec3f
import dev.rakrae.gameengine.samplegame.chess.levels.ChessExampleLevel
import dev.rakrae.gameengine.scene.Camera
import dev.rakrae.gameengine.scene.Scene
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext
import kotlin.math.PI
import kotlin.time.Duration.Companion.seconds

class Chess : Game() {

    override val title = "Chess (Sample Game)"

    override val scene: Scene by lazy {
        val level = ChessExampleLevel()
        val cameras = listOf(
            Camera(
                viewportOffsetNormalized = Vec2f(0f, 0.05f),
                viewportScaleNormalized = Vec2f(1f, 0.9f)
            ),
            Camera(
                viewportOffsetNormalized = Vec2f(0.85f, 0.1f),
                viewportScaleNormalized = Vec2f(0.1f, 0.2f)
            ),
            Camera(horizontalFovRadians = 0.5f * PI.toFloat()).apply {
                renderTexture = RenderTexture(0)
                postProcessingShaders.add(DepthOfFieldPostProcessingShader(effectStrength = 4f))
            }
        )
        Scene(cameras, level.nodes)
    }

    private var fpsCounterCoroutine: Job? = null

    override suspend fun onStart() {
        println("Game started")

        window.requestWindowState(Window.State.FullScreen)
        startFpsCounterCoroutine()

        scene.cameras[0].translate(Vec3f(0f, 1.8f, 0f))

        scene.cameras[1].translate(Vec3f(0f, 4f, -2f))
        scene.cameras[1].rotate(Vec3f(-0.35f * PI.toFloat(), 0f, 0f))

        scene.cameras[2].translate(Vec3f(1f, 2f, 1f))
        scene.cameras[2].rotate(Vec3f(-0.15f * PI.toFloat(), 0.05f * PI.toFloat(), 0f))
    }

    override suspend fun onTick() {
        val mainCamera = scene.cameras[0]
        val moveSpeed = 3f
        val forwardOffset = moveSpeed * GameTime.deltaTime * Input.axisPair1.x
        val sidewaysOffset = moveSpeed * GameTime.deltaTime * Input.axisPair1.y
        mainCamera.translate(Vec3f(1f, 0f, 0f) * forwardOffset)
        mainCamera.translate(Vec3f(0f, 0f, 1f) * sidewaysOffset)

        val mouseSensitivity = 0.12f
        mainCamera.rotate(Vec3f(0f, -1f, 0f) * (mouseSensitivity * Input.axisPair2.x))
        mainCamera.rotate(Vec3f(-1f, 0f, 0f) * (mouseSensitivity * Input.axisPair2.y))
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
