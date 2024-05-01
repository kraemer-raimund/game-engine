package dev.rakrae.gameengine.core

import dev.rakrae.gameengine.graphics.Bitmap

interface Window {
    fun displayPixels(bitmap: Bitmap)
}
