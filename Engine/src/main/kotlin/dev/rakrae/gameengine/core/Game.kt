package dev.rakrae.gameengine.core

import dev.rakrae.gameengine.scene.Scene

abstract class Game : GameLifeCycleReceiver {

    abstract val title: String
    abstract val scene: Scene

    private lateinit var engine: Engine

    fun start() {
        engine = Engine(this)
        engine.start()
    }

    fun stop() {
        engine.stop()
    }
}
