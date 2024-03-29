package dev.rakrae.gameengine.math

class Vector3f(val x: Float, val y: Float, val z: Float) {

    operator fun plus(vector3f: Vector3f): Vector3f {
        return Vector3f(
            x + vector3f.x,
            y + vector3f.y,
            z + vector3f.z
        )
    }

    operator fun times(scalar: Float): Vector3f {
        return Vector3f(scalar * x, scalar * y, scalar * z)
    }

    operator fun times(scalar: Int): Vector3f {
        return Vector3f(scalar * x, scalar * y, scalar * z)
    }

    companion object {
        val right = Vector3f(1f, 0f, 0f)
        val up = Vector3f(0f, 1f, 0f)
        val forward = Vector3f(0f, 0f, 1f)
        val zero = Vector3f(0f, 0f, 0f)
    }
}
