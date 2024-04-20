package dev.rakrae.gameengine.graphics

import dev.rakrae.gameengine.math.Vec3f

class Triangle(val v0: Vertex, val v1: Vertex, val v2: Vertex) {

    val normal: Vec3f by lazy {
        val a = v0.position.toVec3f()
        val b = v1.position.toVec3f()
        val c = v2.position.toVec3f()
        return@lazy (c - a) cross (b - a)
    }
}
