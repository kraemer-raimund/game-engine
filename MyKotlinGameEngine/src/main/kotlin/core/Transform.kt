package com.rk.mykotlingameengine.core

import com.rk.mykotlingameengine.math.Vector3f

class Transform(owner: Actor) : Component(owner) {

    var position: Vector3f = Vector3f.zero

    var rotationEuler: Vector3f = Vector3f.zero
}