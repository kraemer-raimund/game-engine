package com.rk.mykotlingameengine.graphics

import com.rk.mykotlingameengine.core.GameTime
import com.rk.mykotlingameengine.core.IGame
import kotlin.random.Random

class Renderer(screenSize: ScreenSize) {

    private val screen = Bitmap(screenSize.width, screenSize.height)

    private val sprite1 = generateRandomSprite()

    fun render(game: IGame): Bitmap {
        clear(screen)

        val numberOfSprites = (screen.width / sprite1.width) + 2

        for (i in 0 until numberOfSprites) {
            val speed = 200.0f
            val totalWidth = numberOfSprites * sprite1.width
            val x = (GameTime.elapsedTime * speed + i * sprite1.width) % totalWidth - sprite1.width
            val y = (screen.height - sprite1.height) / 2
            draw(screen, sprite1, x.toInt(), y)
        }

        return screen
    }

    private fun clear(bitmap: Bitmap) {
        for (i in 0 until bitmap.width * bitmap.height) {
            bitmap.pixels[i] = 0
        }
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