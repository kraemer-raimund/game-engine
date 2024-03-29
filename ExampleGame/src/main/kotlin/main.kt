package com.rk.examplegame

import com.rk.mykotlingameengine.core.Engine

fun main() {
    val game = ExampleGame()
    val engine = Engine(game)
    engine.start()
}
