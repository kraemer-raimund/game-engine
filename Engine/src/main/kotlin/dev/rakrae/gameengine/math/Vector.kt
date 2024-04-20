package dev.rakrae.gameengine.math

import kotlin.math.abs

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

    fun isCloseTo(other: Vec3f, epsilon: Float = 0.00001f): Boolean {
        val similar = { f1: Float, f2: Float -> abs(f1 - f2) < epsilon }
        return similar(this.x, other.x)
                && similar(this.y, other.y)
                && similar(this.z, other.z)
    }

    companion object {
        val zero = Vec3f(0f, 0f, 0f)
    }
}

data class Vec4f(val x: Float, val y: Float, val z: Float, val w: Float)
