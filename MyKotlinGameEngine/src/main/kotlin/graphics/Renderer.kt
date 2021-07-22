package com.rk.mykotlingameengine.graphics

import com.rk.mykotlingameengine.core.IGame
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class Renderer(screenSize: ScreenSize) {

    private val screen = Bitmap(screenSize.width, screenSize.height)
    private val startTimeSeconds = System.currentTimeMillis() / 1000.0
    private val elapsedTimeSeconds get() = (System.currentTimeMillis() / 1000.0) - startTimeSeconds

    private val sprite1 = generateRandomSprite()
    private val sprite2 = generateRandomSprite()
    private val sprite3 = generateRandomSprite()

    fun render(game: IGame): Bitmap {
        val frequencyInHertz = 0.5
        val magnitude = 100
        val x = (
            sin(elapsedTimeSeconds * frequencyInHertz * 2 * PI) * magnitude
                ).toInt()
        val y = (
            cos(elapsedTimeSeconds * frequencyInHertz * 2 * PI) * magnitude
                ).toInt()
        draw(screen, sprite1, x, y)
        draw(screen, sprite2, -x + 100, y + 200)
        draw(screen, sprite3, x + 250, -y + 100)
        return screen
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
        val sprite = Bitmap(256, 256)
        for (i in 0 until sprite.width * sprite.height) {
            sprite.pixels[i] = Random.nextInt()
        }
        return sprite
    }
}