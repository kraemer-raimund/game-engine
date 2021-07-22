package com.rk.mykotlingameengine.graphics

import com.rk.mykotlingameengine.core.IGame
import kotlin.random.Random

class Renderer(private val screenSize: ScreenSize) {

    fun render(game: IGame): Bitmap {
        val screen = Bitmap(screenSize.width, screenSize.height)
        draw(screen, generateRandomSprite(), -10, -200)
        draw(screen, generateRandomSprite(), 240, 150)
        draw(screen, generateRandomSprite(), 300, 300)
        return screen
    }

    /**
     * Draw [sprite] onto [canvas] at the given offset.
     */
    private fun draw(canvas: Bitmap, sprite: Bitmap, spriteOffsetX: Int, spriteOffsetY: Int) {
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

    private fun generateRandomSprite(): Bitmap {
        val sprite = Bitmap(256, 256)
        for (i in 0 until sprite.width * sprite.height) {
            sprite.pixels[i] = Random.nextInt()
        }
        return sprite
    }
}