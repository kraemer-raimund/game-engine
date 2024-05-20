package dev.rakrae.gameengine.core

import dev.rakrae.gameengine.scene.Scene

abstract class Game : GameLifeCycleReceiver {

    abstract val title: String
    abstract val scene: Scene

    private val engine: Engine by lazy { Engine(this) }

    val window get() = engine.window

    fun start() = engine.start()
    fun stop() = engine.stop()
}
