package dev.rakrae.gameengine.platform

import dev.rakrae.gameengine.input.InputAdapter
import dev.rakrae.gameengine.math.Vec2f

class AwtMouseInputAdapter : InputAdapter {

    override val axisPair: Vec2f
        get() = Vec2f(0f, 0f)
}
