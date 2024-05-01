package dev.rakrae.gameengine.input

import dev.rakrae.gameengine.math.Vec2f

object Input {

    internal var inputAdapter1: InputAdapter? = null
    internal var inputAdapter2: InputAdapter? = null

    val axisPair1: Vec2f
        get() = inputAdapter1?.axisPair ?: Vec2f(0f, 0f)

    val axisPair2: Vec2f
        get() = inputAdapter2?.axisPair ?: Vec2f(0f, 0f)
}
