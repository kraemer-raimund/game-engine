package dev.rakrae.gameengine.core

import dev.rakrae.gameengine.scene.Scene

interface Game : GameLifeCycleReceiver {

    val title: String
    val scene: Scene
}
