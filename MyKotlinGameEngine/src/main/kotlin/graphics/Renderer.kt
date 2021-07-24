package com.rk.mykotlingameengine.graphics

import com.rk.mykotlingameengine.core.GameTime
import com.rk.mykotlingameengine.core.IGame
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class Renderer(screenSize: ScreenSize) {

    private val screen = Bitmap(screenSize.width, screenSize.height)

    private val sprite1 = generateRandomSprite()
    private val sprite2 = generateRandomSprite()
    private val sprite3 = generateRandomSprite()

    fun render(game: IGame): Bitmap {
        clear(screen)

        for (i in 0 until 100) {
            val frequencyInHertz = 0.5
            val magnitudeX = 200
            val magnitudeY = 100
            val x = (
                    sin((GameTime.elapsedTime + 0.015 * i) * frequencyInHertz * 2 * PI) * magnitudeX
                    ).toInt()
            val y = (
                    cos((GameTime.elapsedTime + 0.015 * i) * frequencyInHertz * 2 * PI) * magnitudeY
                    ).toInt()
            draw(screen, sprite1, x, y)
            draw(screen, sprite2, -x + 300, y + 250)
            draw(screen, sprite3, x + 250, -y + 100)
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