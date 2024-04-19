package dev.rakrae.gameengine.samplegame

import dev.rakrae.gameengine.assets.AssetLoader
import dev.rakrae.gameengine.core.Game
import dev.rakrae.gameengine.math.Vec3f
import dev.rakrae.gameengine.scene.Node

class SampleGame : Game {

    override val title = "Chess (Sample Game)"

    override val nodes: Sequence<Node> by lazy {
        meshes.mapIndexed { i, mesh ->
            Node(mesh, Vec3f(i.toFloat(), 0f, 0f))
        }
    }

    private val meshes by lazy {
        val king = AssetLoader().loadMesh("/assets/chesspieces/king.obj")
        val queen = AssetLoader().loadMesh("/assets/chesspieces/queen.obj")
        val bishop = AssetLoader().loadMesh("/assets/chesspieces/bishop.obj")
        val knight = AssetLoader().loadMesh("/assets/chesspieces/knight.obj")
        sequenceOf(king, queen, bishop, knight)
    }
}
