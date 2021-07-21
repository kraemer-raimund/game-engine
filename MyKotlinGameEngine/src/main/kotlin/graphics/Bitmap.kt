package com.rk.mykotlingameengine.graphics

class Bitmap(val width: Int, val height: Int) {

    val pixels: Array<Int> = Array(width * height) { 0 }
}