package dev.rakrae.gameengine.samplegame.chess.levels

import dev.rakrae.gameengine.assets.AssetLoader
import dev.rakrae.gameengine.graphics.Color
import dev.rakrae.gameengine.graphics.Material
import dev.rakrae.gameengine.graphics.Texture
import dev.rakrae.gameengine.graphics.rendering.shaders.*
import dev.rakrae.gameengine.math.Vec2f
import dev.rakrae.gameengine.math.Vec3f
import dev.rakrae.gameengine.samplegame.chess.shaders.DummyAnimationVertexShader
import dev.rakrae.gameengine.scene.RenderComponent

class ChessExampleLevel {

    val nodes: List<dev.rakrae.gameengine.scene.Node> by lazy {
        val king = AssetLoader().loadMesh("/assets/chesspieces/king.obj")
        val queen = AssetLoader().loadMesh("/assets/chesspieces/queen.obj")
        val bishop = AssetLoader().loadMesh("/assets/chesspieces/bishop.obj")
        val knight = AssetLoader().loadMesh("/assets/chesspieces/knight.obj")
        val rook = AssetLoader().loadMesh("/assets/chesspieces/rook.obj")
        val pawn = AssetLoader().loadMesh("/assets/chesspieces/pawn.obj")

        val chessPieces = listOf(king, queen, bishop, knight, rook, pawn)
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

                5 -> Material(
                    color = Color(80u, 160u, 80u, 255u),
                    glossiness = 4f
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
                5 -> DefaultFragmentShader()
                else -> DefaultFragmentShader()
            }
            dev.rakrae.gameengine.scene.Node(
                RenderComponent(
                    mesh = mesh,
                    position = position,
                    scale = Vec3f(1f, 1f, 1f),
                    material = material,
                    vertexShader = vertexShader,
                    fragmentShader = fragmentShader,
                    deferredShader = when (i) {
                        5 -> OutlineDeferredShader(2, 0.2f, Color(255u, 255u, 0u, 255u))
                        else -> null
                    }
                )
            )
        }

        val exampleObjects = listOf(
            dev.rakrae.gameengine.scene.Node(
                renderComponent = RenderComponent(
                    mesh = AssetLoader().loadMesh("/assets/plane.obj"),
                    position = Vec3f(-10f, 0f, 0f),
                    scale = Vec3f(4f, 1f, 4f),
                    material = Material(
                        albedo = Texture(
                            "/assets/textures/medieval-pavement/TCom_Pavement_Medieval_512_albedo.png"
                        ),
                        normal = Texture(
                            "/assets/textures/medieval-pavement/TCom_Pavement_Medieval_512_normal.png"
                        ),
                        glossiness = 3f,
                        uvScale = Vec2f(2f, 2f)
                    ),
                    fragmentShader = UvTextureFragmentShader()
                )
            ),
            dev.rakrae.gameengine.scene.Node(
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
                        glossiness = 1.5f,
                        uvScale = Vec2f(10f, 10f)
                    ),
                    fragmentShader = UvTextureFragmentShader()
                )
            ),
            dev.rakrae.gameengine.scene.Node(
                renderComponent = RenderComponent(
                    mesh = AssetLoader().loadMesh("/assets/cube.obj"),
                    position = Vec3f(-10f, 1f, 0f),
                    scale = Vec3f(1f, 1f, 1f),
                    material = Material(
                        albedo = Texture(
                            "/assets/textures/scifi-panel/TCom_Scifi_Panel_512_albedo.png"
                        ),
                        normal = Texture("/assets/textures/scifi-panel/TCom_Scifi_Panel_512_normal.png"),
                        glossiness = 8f,
                        uvScale = Vec2f(4f, 4f)
                    ),
                    fragmentShader = UvTextureFragmentShader()
                )
            )
        )

        return@lazy chessNodes + exampleObjects
    }
}
