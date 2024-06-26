package dev.rakrae.gameengine.samplegame.chess.levels

import dev.rakrae.gameengine.assets.AssetLoader
import dev.rakrae.gameengine.graphics.BitmapTexture
import dev.rakrae.gameengine.graphics.Color
import dev.rakrae.gameengine.graphics.Material
import dev.rakrae.gameengine.graphics.rendering.BuiltinShaders
import dev.rakrae.gameengine.math.Vec2f
import dev.rakrae.gameengine.math.Vec3f
import dev.rakrae.gameengine.scene.PointLight
import dev.rakrae.gameengine.scene.RenderComponent
import dev.rakrae.gameengine.scene.Transform
import kotlin.math.PI

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
                1 -> Material(color = Color(255u, 0u, 0u, 255u), glossiness = 2f)
                2 -> Material(color = Color(254u, 80u, 0u, 255u), glossiness = 2f)
                3 -> Material(color = Color(50u, 120u, 180u, 255u), glossiness = 0.5f)
                4 -> Material(
                    color = Color(80u, 160u, 80u, 255u),
                    glossiness = 4f,
                    albedo = BitmapTexture(
                        "/assets/textures/wood-oak-veneer/TCom_Wood_OakVeneer2_512_albedo.png"
                    )
                )

                5 -> Material(
                    color = Color(80u, 160u, 80u, 255u),
                    glossiness = 4f
                )

                else -> Material.default
            }
            val vertexShader = when (i) {
                else -> BuiltinShaders.Material.standardPBR.vertexShader
            }
            val fragmentShader = when (i) {
                else -> BuiltinShaders.Material.standardPBR.fragmentShader
            }
            dev.rakrae.gameengine.scene.MeshNode(
                RenderComponent(
                    mesh = mesh,
                    position = position,
                    scale = Vec3f(1f, 1f, 1f),
                    material = material,
                    vertexShader = vertexShader,
                    fragmentShader = fragmentShader,
                    deferredShader = when (i) {
                        5 -> BuiltinShaders.Deferred.outline(2, Color(255u, 255u, 0u, 255u))
                        else -> null
                    }
                )
            )
        }

        val exampleObjects = listOf(
            dev.rakrae.gameengine.scene.MeshNode(
                renderComponent = RenderComponent(
                    mesh = AssetLoader().loadMesh("/assets/plane.obj"),
                    position = Vec3f(0f, 0f, 0f),
                    scale = Vec3f(50f, 50f, 50f),
                    material = Material(
                        albedo = BitmapTexture(
                            "/assets/textures/medieval-pavement/TCom_Pavement_Medieval_512_albedo.png"
                        ),
                        uvScale = Vec2f(20f, 20f)
                    ),
                    fragmentShader = BuiltinShaders.Material.unlitTextured.fragmentShader,
                    vertexShader = BuiltinShaders.Material.unlitTextured.vertexShader
                )
            ),
            dev.rakrae.gameengine.scene.MeshNode(
                renderComponent = RenderComponent(
                    mesh = AssetLoader().loadMesh("/assets/plane.obj"),
                    position = Vec3f(-10f, 2f, -10f),
                    rotationEulerRad = Vec3f(0.5f * PI.toFloat(), 0f, 0f),
                    scale = Vec3f(2f, 2f, 2f),
                    material = Material(
                        albedo = BitmapTexture(
                            "/assets/textures/stone-wall/TCom_Wall_Stone3_2x2_512_albedo.png"
                        ),
                        normal = BitmapTexture(
                            "/assets/textures/stone-wall/TCom_Wall_Stone3_2x2_512_normal.png"
                        ),
                        glossiness = 1.5f,
                        uvScale = Vec2f(2f, 2f)
                    ),
                    fragmentShader = BuiltinShaders.Material.standardPBR.fragmentShader,
                    vertexShader = BuiltinShaders.Material.standardPBR.vertexShader
                )
            ),
            dev.rakrae.gameengine.scene.MeshNode(
                renderComponent = RenderComponent(
                    mesh = AssetLoader().loadMesh("/assets/cube.obj"),
                    position = Vec3f(-6f, 1f, 4f),
                    scale = Vec3f(1f, 1f, 1f),
                    material = Material(
                        albedo = BitmapTexture(
                            "/assets/textures/scifi-panel/TCom_Scifi_Panel_512_albedo.png"
                        ),
                        normal = BitmapTexture("/assets/textures/scifi-panel/TCom_Scifi_Panel_512_normal.png"),
                        glossiness = 8f,
                        uvScale = Vec2f(4f, 4f)
                    ),
                    fragmentShader = BuiltinShaders.Material.standardPBR.fragmentShader,
                    vertexShader = BuiltinShaders.Material.standardPBR.vertexShader
                )
            ),
            dev.rakrae.gameengine.scene.MeshNode(
                renderComponent = RenderComponent(
                    mesh = AssetLoader().loadMesh("/assets/cube.obj"),
                    position = Vec3f(0f, 0f, 0f),
                    scale = Vec3f(-20f, 20f, 20f),
                    material = Material(
                        albedo = BitmapTexture(
                            "/assets/textures/environment-parking-garage/TCom_JapanParkingGarageB_8K_hdri_sphere_tone.jpg"
                        ),
                        uvScale = Vec2f(1f, 1f)
                    ),
                    fragmentShader = BuiltinShaders.Material.unlitSkybox.fragmentShader,
                    vertexShader = BuiltinShaders.Material.unlitSkybox.vertexShader
                )
            )
        )

        return@lazy chessNodes + exampleObjects + listOf(PointLight(Transform()))
    }
}
