package com.rk.mykotlingameengine.core

class Camera(owner: Actor) : Component(owner) {

    // Think "how quickly does the depth grow from the edges to the
    // center of the screen".
    var lens = 30.0f

    // Basically the FOV, but as a scalar rather than an angle.
    // Think "how closely are parallel lines in the 3D world represented
    // by parallel lines on screen".
    var horizontalFovMultiplier = 1.0f

    // The hypothetical infinite distance at the exact screen center.
    var maxDepth = 10000.0f
}