package dev.rakrae.gameengine.math

import kotlin.math.abs
import kotlin.math.sqrt

data class Vec2i(val x: Int, val y: Int) {

    operator fun plus(vector: Vec2i): Vec2i {
        return Vec2i(x + vector.x, y + vector.y)
    }

    operator fun minus(vector: Vec2i): Vec2i {
        return Vec2i(x - vector.x, y - vector.y)
    }
}

data class Vec2f(val x: Float, val y: Float)

data class Vec3f(val x: Float, val y: Float, val z: Float) {

    val magnitude: Float = sqrt(x * x + y * y + z * z)
    val normalized: Vec3f = Vec3f(x / magnitude, y / magnitude, z / magnitude)

    operator fun plus(vector: Vec3f): Vec3f {
        return Vec3f(
            x + vector.x,
            y + vector.y,
            z + vector.z
        )
    }

    operator fun minus(vector: Vec3f): Vec3f {
        return Vec3f(
            x - vector.x,
            y - vector.y,
            z - vector.z
        )
    }

    operator fun times(scalar: Float): Vec3f {
        return Vec3f(scalar * x, scalar * y, scalar * z)
    }

    operator fun times(scalar: Int): Vec3f {
        return Vec3f(scalar * x, scalar * y, scalar * z)
    }

    infix fun cross(vector: Vec3f): Vec3f {
        // https://en.wikipedia.org/wiki/Cross_product#Mnemonic
        // a = (b cross c)
        val b = this
        val c = vector

        val ax = b.y * c.z - b.z * c.y
        val ay = b.z * c.x - b.x * c.z
        val az = b.x * c.y - b.y * c.x

        return Vec3f(ax, ay, az)
    }

    infix fun dot(vector: Vec3f): Float {
        // https://en.wikipedia.org/wiki/Dot_product
        val a = this
        val b = vector
        return (a.x * b.x) + (a.y * b.y) + (a.z * b.z)
    }

    fun isCloseTo(other: Vec3f, epsilon: Float = 0.01f): Boolean {
        val similar = { f1: Float, f2: Float -> abs(f1 - f2) < epsilon }
        return similar(this.x, other.x)
                && similar(this.y, other.y)
                && similar(this.z, other.z)
    }

    companion object {
        val zero = Vec3f(0f, 0f, 0f)
    }
}

data class Vec4f(val x: Float, val y: Float, val z: Float, val w: Float) {

    fun toVec3f(): Vec3f {
        return Vec3f(x, y, z)
    }
}
