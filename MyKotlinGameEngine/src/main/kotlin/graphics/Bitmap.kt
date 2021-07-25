package com.rk.mykotlingameengine.graphics

class Bitmap(val width: Int, val height: Int) {

    val pixels: IntArray = IntArray(width * height)

    fun clear() {
        for (i in 0 until width * height) {
            pixels[i] = 0
        }
    }
}