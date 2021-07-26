package com.rk.mykotlingameengine

import com.rk.mykotlingameengine.core.Engine
import com.rk.mykotlingameengine.game.ExampleGame

fun main() {
    val game = ExampleGame()
    val engine = Engine(game)
    engine.start()
}