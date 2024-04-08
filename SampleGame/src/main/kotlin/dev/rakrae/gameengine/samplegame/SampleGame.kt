package dev.rakrae.gameengine.samplegame

import dev.rakrae.gameengine.assets.AssetLoader
import dev.rakrae.gameengine.core.Game

class SampleGame : Game {

    override val title = "Chess (Sample Game)"

    override val meshes by lazy {
        val chessPieces = AssetLoader().loadMesh("/assets/chesspieces/chess-piece-3d-models.obj")
        sequenceOf(chessPieces)
    }

    override fun onTick() {
    }
}
