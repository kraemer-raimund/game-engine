package dev.rakrae.gameengine.graphics.rendering

import dev.rakrae.gameengine.graphics.Bitmap
import dev.rakrae.gameengine.math.Vec2i

class SpriteRenderer {

    /**
     * Draw [sprite] onto [canvas] at the given offset.
     */
    fun draw(canvas: Bitmap, sprite: Bitmap, offset: Vec2i) {
        for (yOnSprite in 0..<sprite.height) {
            val yOnCanvas = offset.y + yOnSprite
            if (yOnCanvas !in 0..<canvas.height) {
                continue
            }

            for (xOnSprite in 0..<sprite.width) {
                val xOnCanvas = xOnSprite + offset.x
                if (xOnCanvas !in 0..<canvas.width) {
                    continue
                }

                val pixelColor = sprite.getPixel(xOnSprite, yOnSprite)
                if (pixelColor.a > 0u) {
                    canvas.setPixel(xOnCanvas, yOnCanvas, pixelColor)
                }
            }
        }
    }
}
