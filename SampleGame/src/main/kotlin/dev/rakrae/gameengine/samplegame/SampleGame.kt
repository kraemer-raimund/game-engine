package dev.rakrae.gameengine.samplegame

import dev.rakrae.gameengine.assets.AssetLoader
import dev.rakrae.gameengine.core.FpsCounter
import dev.rakrae.gameengine.core.Game
import dev.rakrae.gameengine.graphics.Color
import dev.rakrae.gameengine.graphics.Material
import dev.rakrae.gameengine.graphics.pipeline.*
import dev.rakrae.gameengine.input.Input
import dev.rakrae.gameengine.math.Vec3f
import dev.rakrae.gameengine.scene.Node
import dev.rakrae.gameengine.scene.RenderComponent
import dev.rakrae.gameengine.scene.Scene

class SampleGame : Game {

    override val title = "Chess (Sample Game)"

    override val scene: Scene by lazy { Scene(nodes) }

    private val nodes: Sequence<Node> by lazy {
        val king = AssetLoader().loadMesh("/assets/chesspieces/king.obj")
        val queen = AssetLoader().loadMesh("/assets/chesspieces/queen.obj")
        val bishop = AssetLoader().loadMesh("/assets/chesspieces/bishop.obj")
        val knight = AssetLoader().loadMesh("/assets/chesspieces/knight.obj")

        val meshes = sequenceOf(king, queen, bishop, knight)
        meshes.mapIndexed { i, mesh ->
            val position = Vec3f((0.25f * i.toFloat()) + 0.5f, 0.05f * i.toFloat(), 1f * i.toFloat())
            val material = when (i) {
                0 -> Material(DefaultVertexShader(), DefaultFragmentShader(), Color(80u, 80u, 190u, 255u))
                1 -> Material(DummyAnimationVertexShader(), GouraudFragmentShader(), Color(255u, 0u, 0u, 255u))
                2 -> Material(DefaultVertexShader(), DummyExampleFragmentShader())
                else -> Material(DummyAnimationVertexShader(), GouraudFragmentShader(), Color(50u, 120u, 180u, 255u))
            }
            Node(
                RenderComponent(
                    mesh = mesh,
                    position = position,
                    scale = Vec3f(0.2f + 0.1f * i, 0.2f + 0.1f * i, 0.2f + 0.1f * i),
                    material = material
                )
            )
        }
    }

    override suspend fun onStart() {
        println("Game started")
    }

    override suspend fun onTick() {
        println("FPS: ${FpsCounter.currentFps}")
        val moveSpeed = 0.000001f
        scene.activeCamera.translate(Vec3f(1f, 0f, 0f) * (moveSpeed * Input.axisPair1.x))
        scene.activeCamera.translate(Vec3f(0f, 0f, 1f) * (moveSpeed * Input.axisPair1.y))

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
    }
}
