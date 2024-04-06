package dev.rakrae.gameengine.core

import dev.rakrae.gameengine.math.Vec3f

class Transform(owner: Actor) : Component(owner) {

    var position: Vec3f = Vec3f.zero

    var rotationEuler: Vec3f = Vec3f.zero
}
