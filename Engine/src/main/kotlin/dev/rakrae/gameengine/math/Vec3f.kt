package dev.rakrae.gameengine.math

class Vec3f(val x: Float, val y: Float, val z: Float) {

    operator fun plus(vector3f: Vec3f): Vec3f {
        return Vec3f(
            x + vector3f.x,
            y + vector3f.y,
            z + vector3f.z
        )
    }

    operator fun times(scalar: Float): Vec3f {
        return Vec3f(scalar * x, scalar * y, scalar * z)
    }

    operator fun times(scalar: Int): Vec3f {
        return Vec3f(scalar * x, scalar * y, scalar * z)
    }

    companion object {
        val right = Vec3f(1f, 0f, 0f)
        val up = Vec3f(0f, 1f, 0f)
        val forward = Vec3f(0f, 0f, 1f)
        val zero = Vec3f(0f, 0f, 0f)
    }
}
