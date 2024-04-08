package dev.rakrae.gameengine.core

import dev.rakrae.gameengine.graphics.Mesh

interface Game {

    val title: String
    val meshes: Sequence<Mesh>

    fun onTick()
}
