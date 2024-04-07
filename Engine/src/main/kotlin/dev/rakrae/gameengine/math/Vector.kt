package dev.rakrae.gameengine.math

data class Vec2f(val x: Float, val y: Float)

data class Vec3f(val x: Float, val y: Float, val z: Float) {

    operator fun plus(vector: Vec3f): Vec3f {
        return Vec3f(
            x + vector.x,
            y + vector.y,
            z + vector.z
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

data class Vec4f(val x: Float, val y: Float, val z: Float, val w: Float)
