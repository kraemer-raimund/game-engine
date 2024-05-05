package dev.rakrae.gameengine.core

import dev.rakrae.gameengine.scene.Scene

abstract class Game : GameLifeCycleReceiver {

    abstract val title: String
    abstract val scene: Scene

    fun start() {
        Engine(this).start()
    }
}
