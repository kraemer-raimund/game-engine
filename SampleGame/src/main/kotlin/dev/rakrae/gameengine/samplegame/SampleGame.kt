package dev.rakrae.gameengine.samplegame

import dev.rakrae.gameengine.assets.AssetLoader
import dev.rakrae.gameengine.core.Game
import dev.rakrae.gameengine.graphics.Mesh

class SampleGame : Game {

    private val chessPieces = AssetLoader().loadMesh("/assets/chesspieces/chess-piece-3d-models.obj")

    override val meshes: Sequence<Mesh>
        get() = sequenceOf(chessPieces)

    override fun onTick() {
    }
}
