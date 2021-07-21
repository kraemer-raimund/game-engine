package com.rk.mykotlingameengine.graphics

class Bitmap(width: Int = 0, height: Int = 0) {

    private var pixels: Array<Int> = Array(width * height) { 0 }
}