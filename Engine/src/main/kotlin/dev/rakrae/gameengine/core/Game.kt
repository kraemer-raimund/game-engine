package dev.rakrae.gameengine.core

import dev.rakrae.gameengine.scene.Node

interface Game {

    val title: String
    val nodes: Sequence<Node>
}
