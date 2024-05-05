package dev.rakrae.gameengine.graphics

import dev.rakrae.gameengine.math.Vec2f

class Material(
    val color: Color = Color(255u, 255u, 255u, 255u),
    val glossiness: Float = 0f,
    val albedo: Texture? = null,
    val normal: Texture? = null,
    val uvScale: Vec2f = Vec2f(1f, 1f),
    val uvOffset: Vec2f = Vec2f(0f, 0f)
) {

    companion object {
        val default = Material()
    }
}
