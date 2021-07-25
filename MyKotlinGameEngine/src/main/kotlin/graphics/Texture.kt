package com.rk.mykotlingameengine.graphics

import javax.imageio.ImageIO

class Texture(filePath: String) {

    val bitmap = loadBitmap(filePath)

    private fun loadBitmap(filePath: String): Bitmap {
        val imageUrl = this::class.java.classLoader.getResource(filePath)
        val bufferedImage = ImageIO.read(imageUrl)
        val width = bufferedImage.width
        val height = bufferedImage.height
        val pixels = IntArray(width * height)
        bufferedImage.getRGB(0, 0, width, height, pixels, 0, width)
        val bitmap = Bitmap(width, height)
        for (i in pixels.indices) {
            bitmap.pixels[i] = pixels[i] and 0x00FFFFFF
        }
        return bitmap
    }
}