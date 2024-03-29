package dev.rakrae.gameengine.core

import dev.rakrae.gameengine.graphics.Texture

interface IGame {

    val floorTexture: Texture
    val activeCamera: Camera

    fun onTick()
}
