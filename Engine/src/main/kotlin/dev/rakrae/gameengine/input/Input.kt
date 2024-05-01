package dev.rakrae.gameengine.input

import dev.rakrae.gameengine.math.Vec2f

object Input {

    internal var inputAxisAdapter1: InputAxisAdapter? = null
    internal var inputAxisAdapter2: InputAxisAdapter? = null

    val axisPair1: Vec2f
        get() = inputAxisAdapter1?.axisPair ?: Vec2f(0f, 0f)

    val axisPair2: Vec2f
        get() = inputAxisAdapter2?.axisPair ?: Vec2f(0f, 0f)
}
