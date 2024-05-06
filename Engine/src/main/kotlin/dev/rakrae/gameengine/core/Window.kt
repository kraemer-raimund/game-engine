package dev.rakrae.gameengine.core

import dev.rakrae.gameengine.graphics.Bitmap
import dev.rakrae.gameengine.graphics.ScreenSize

interface Window {

    val size: ScreenSize
    fun displayPixels(bitmap: Bitmap)
}
