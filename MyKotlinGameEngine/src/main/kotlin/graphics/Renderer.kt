package com.rk.mykotlingameengine.graphics

class Renderer {

    /**
     * Draw [sprite] onto [canvas] at the given offset.
     */
    fun draw(canvas: Bitmap, sprite: Bitmap, spriteOffsetX: Int, spriteOffsetY: Int) {
        for (yOnSprite in 0 until sprite.height) {
            val yOnCanvas = spriteOffsetY + yOnSprite;
            if (yOnCanvas !in 0 until canvas.height) {
                continue
            }

            for (xOnSprite in 0 until sprite.width) {
                val xOnCanvas = xOnSprite + spriteOffsetX
                if (xOnCanvas !in 0 until canvas.width) {
                    continue
                }

                val indexInCanvas = xOnCanvas + canvas.width * yOnCanvas
                val indexInSprite = xOnSprite + sprite.width * yOnSprite
                canvas.pixels[indexInCanvas] = sprite.pixels[indexInSprite]
            }
        }
    }
}