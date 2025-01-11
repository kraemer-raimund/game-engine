package dev.rakrae.gameengine.samplegame.chess

import dev.rakrae.gameengine.core.Game
import dev.rakrae.gameengine.core.GameTime
import dev.rakrae.gameengine.graphics.Color
import dev.rakrae.gameengine.graphics.rendering.*
import dev.rakrae.gameengine.input.Input
import dev.rakrae.gameengine.math.Vec2f
import dev.rakrae.gameengine.math.Vec3f
import dev.rakrae.gameengine.samplegame.chess.levels.ChessExampleLevel
import dev.rakrae.gameengine.scene.Camera
import dev.rakrae.gameengine.scene.EnvironmentAttributes
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
                viewportOffsetNormalized = Vec2f(0f, 0.0f),
                viewportScaleNormalized = Vec2f(1f, 1f)
            ).apply {
                postProcessingShaders.add(PixelArtPostProcessingShader())
            }
        )
        Scene(
            EnvironmentAttributes(
                ambientColor = Color.black,
                ambientIntensityMultiplier = 1.0f
            ),
            cameras,
            level.nodes,
            sunLightDirection = Vec3f(0.5f, -1f, -1f).normalized
        )
    }

    private var fpsCounterCoroutine: Job? = null

    override suspend fun onStart() {
        println("Game started")

        startFpsCounterCoroutine()

        scene.cameras[0].apply {
            translate(Vec3f(-0.2f, 1.8f, 10f))
            rotate(Vec3f(0f, PI.toFloat(), 0f))
        }
    }

    override suspend fun onTick() {
        val areCameraControlsDisabled = true
        if (areCameraControlsDisabled) {
            return
        }

        val mainCamera = scene.cameras[0]
        val moveSpeed = 3f
        val forwardOffset = moveSpeed * GameTime.deltaTime * Input.axisPair1.x
        val sidewaysOffset = moveSpeed * GameTime.deltaTime * Input.axisPair1.y

        mainCamera.apply {
            translate(Vec3f(1f, 0f, 0f) * forwardOffset)
            translate(Vec3f(0f, 0f, 1f) * sidewaysOffset)
            val mouseSensitivity = 1f
            rotate(Vec3f(0f, -1f, 0f) * (mouseSensitivity * Input.axisPair2.x))
            rotate(Vec3f(-1f, 0f, 0f) * (mouseSensitivity * Input.axisPair2.y))
        }
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
