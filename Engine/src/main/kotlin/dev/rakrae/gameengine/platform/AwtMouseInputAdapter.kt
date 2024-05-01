package dev.rakrae.gameengine.platform

import dev.rakrae.gameengine.input.InputAxisAdapter
import dev.rakrae.gameengine.math.Vec2f

class AwtMouseInputAdapter : InputAxisAdapter {

    override val axisPair: Vec2f
        get() = Vec2f(0f, 0f)
}
