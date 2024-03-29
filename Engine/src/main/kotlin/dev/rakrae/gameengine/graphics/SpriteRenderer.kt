package dev.rakrae.gameengine.graphics

import dev.rakrae.gameengine.core.IGame
import kotlin.random.Random

class SpriteRenderer {

    private val sprite1 = generateRandomSprite()

    fun render(canvas: Bitmap, game: IGame) {
    }

    /**
     * Draw [sprite] onto [canvas] at the given offset.
     */
    private fun draw(canvas: Bitmap, sprite: Bitmap, spriteOffsetX: Int, spriteOffsetY: Int) {
        for (yOnSprite in 0 until sprite.height) {
            val yOnCanvas = spriteOffsetY + yOnSprite
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
                val pixel = sprite.pixels[indexInSprite]
                if (pixel > 0) {
                    canvas.pixels[indexInCanvas] = pixel
                }
            }
        }
    }

    private fun generateRandomSprite(): Bitmap {
        val sprite = Bitmap(64, 64)
        for (i in 0 until sprite.width * sprite.height) {
            sprite.pixels[i] = Random.nextInt()
        }
        return sprite
    }
}
