package dev.rakrae.gameengine.samplegame

import dev.rakrae.gameengine.assets.AssetLoader
import dev.rakrae.gameengine.core.Game
import dev.rakrae.gameengine.core.GameTime
import dev.rakrae.gameengine.graphics.Color
import dev.rakrae.gameengine.graphics.Material
import dev.rakrae.gameengine.graphics.Texture
import dev.rakrae.gameengine.graphics.rendering.shaders.*
import dev.rakrae.gameengine.input.Input
import dev.rakrae.gameengine.math.Vec3f
import dev.rakrae.gameengine.scene.Node
import dev.rakrae.gameengine.scene.RenderComponent
import dev.rakrae.gameengine.scene.Scene
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext
import kotlin.time.Duration.Companion.seconds

class SampleGame : Game {

    override val title = "Chess (Sample Game)"

    override val scene: Scene by lazy { Scene(nodes) }

    private val nodes: List<Node> by lazy {
        val king = AssetLoader().loadMesh("/assets/chesspieces/king.obj")
        val queen = AssetLoader().loadMesh("/assets/chesspieces/queen.obj")
        val bishop = AssetLoader().loadMesh("/assets/chesspieces/bishop.obj")
        val knight = AssetLoader().loadMesh("/assets/chesspieces/knight.obj")
        val rook = AssetLoader().loadMesh("/assets/chesspieces/rook.obj")

        val chessPieces = listOf(king, queen, bishop, knight, rook)
        val chessNodes = chessPieces.mapIndexed { i, mesh ->
            val position = Vec3f(
                x = -2.5f + 2f * i.mod(3),
                y = 0f,
                z = 5f + 2f * (i / 3)
            )
            val material = when (i) {
                0 -> Material(color = Color(80u, 80u, 190u, 255u))
                1 -> Material(color = Color(255u, 0u, 0u, 255u), glossiness = 12f)
                2 -> Material()
                3 -> Material(color = Color(50u, 120u, 180u, 255u), glossiness = 0.5f)
                4 -> Material(
                    color = Color(80u, 80u, 20u, 255u),
                    glossiness = 4f,
                    albedo = Texture("/assets/textures/wood-oak-veneer/TCom_Wood_OakVeneer2_512_albedo.png")
                )

                else -> Material.default
            }
            val vertexShader = when (i) {
                1, 3 -> DummyAnimationVertexShader()
                else -> DefaultVertexShader()
            }
            val fragmentShader = when (i) {
                0 -> DefaultFragmentShader()
                1 -> GouraudFragmentShader()
                2 -> DepthFragmentShader()
                3 -> GouraudFragmentShader()
                4 -> UvTextureFragmentShader()
                else -> DefaultFragmentShader()
            }
            Node(
                RenderComponent(
                    mesh = mesh,
                    position = position,
                    scale = Vec3f(1f, 1f, 1f),
                    material = material,
                    vertexShader = vertexShader,
                    fragmentShader = fragmentShader
                )
            )
        }

        val exampleObjects = listOf(
            Node(
                renderComponent = RenderComponent(
                    mesh = AssetLoader().loadMesh("/assets/floor.obj"),
                    position = Vec3f(-10f, 0f, 0f),
                    scale = Vec3f(8f, 1f, 8f),
                    material = Material(
                        albedo = Texture(
                            "/assets/textures/medieval-pavement/TCom_Pavement_Medieval_512_albedo.png"
                        ),
                        normal = Texture(
                            "/assets/textures/medieval-pavement/TCom_Pavement_Medieval_512_normal.png"
                        ),
                        glossiness = 3f
                    ),
                    fragmentShader = UvTextureFragmentShader()
                )
            ),
            Node(
                renderComponent = RenderComponent(
                    mesh = AssetLoader().loadMesh("/assets/cube.obj"),
                    position = Vec3f(-10f, 2f, -10f),
                    scale = Vec3f(2f, 2f, 0.1f),
                    material = Material(
                        albedo = Texture(
                            "/assets/textures/stone-wall/TCom_Wall_Stone3_2x2_512_albedo.png"
                        ),
                        normal = Texture(
                            "/assets/textures/stone-wall/TCom_Wall_Stone3_2x2_512_normal.png"
                        ),
                        glossiness = 1.5f
                    ),
                    fragmentShader = UvTextureFragmentShader()
                )
            ),
            Node(
                renderComponent = RenderComponent(
                    mesh = AssetLoader().loadMesh("/assets/cube.obj"),
                    position = Vec3f(-10f, 1f, 0f),
                    scale = Vec3f(1f, 1f, 1f),
                    material = Material(
                        albedo = Texture(
                            "/assets/textures/scifi-panel/TCom_Scifi_Panel_512_albedo.png"
                        ),
                        normal = Texture("/assets/textures/scifi-panel/TCom_Scifi_Panel_512_normal.png"),
                        glossiness = 8f
                    ),
                    fragmentShader = UvTextureFragmentShader()
                )
            )
        )

        return@lazy chessNodes + exampleObjects
    }

    private var fpsCounterCoroutine: Job? = null

    override suspend fun onStart() {
        println("Game started")
        scene.activeCamera.translate(Vec3f(0f, 1.8f, 0f))

        startFpsCounterCoroutine()
    }

    override suspend fun onTick() {
        val moveSpeed = 2f
        val forwardOffset = moveSpeed * GameTime.deltaTime * Input.axisPair1.x
        val sidewaysOffset = moveSpeed * GameTime.deltaTime * Input.axisPair1.y
        scene.activeCamera.translate(Vec3f(1f, 0f, 0f) * forwardOffset)
        scene.activeCamera.translate(Vec3f(0f, 0f, 1f) * sidewaysOffset)

        val mouseSensitivity = 0.2f
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
