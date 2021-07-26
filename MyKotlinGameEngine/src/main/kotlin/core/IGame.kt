package com.rk.mykotlingameengine.core

import com.rk.mykotlingameengine.graphics.Texture

interface IGame {

    val floorTexture: Texture
    val activeCamera: Camera

    fun onTick()
}