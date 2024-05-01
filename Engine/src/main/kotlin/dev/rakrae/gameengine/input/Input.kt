package dev.rakrae.gameengine.input

import dev.rakrae.gameengine.math.Vec2f

object Input {

    internal var axisPairProvider1: AxisPairProvider? = null
    internal var axisPairProvider2: AxisPairProvider? = null

    val axisPair1: Vec2f
        get() = axisPairProvider1?.axisPair ?: Vec2f(0f, 0f)

    val axisPair2: Vec2f
        get() = axisPairProvider2?.axisPair ?: Vec2f(0f, 0f)
}
