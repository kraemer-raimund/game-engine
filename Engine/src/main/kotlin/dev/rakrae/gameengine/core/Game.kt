package dev.rakrae.gameengine.core

import dev.rakrae.gameengine.scene.Scene

interface Game {

    val title: String
    val scene: Scene

    fun onTick()
}
