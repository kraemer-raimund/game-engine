package dev.rakrae.gameengine.core

import dev.rakrae.gameengine.math.Vector3f

class Transform(owner: Actor) : Component(owner) {

    var position: Vector3f = Vector3f.zero

    var rotationEuler: Vector3f = Vector3f.zero
}
