package com.rk.mykotlingameengine.graphics

import com.rk.mykotlingameengine.core.GameTime
import com.rk.mykotlingameengine.core.IGame
import com.rk.mykotlingameengine.math.clamp
import kotlin.math.*

class ViewportRenderer {

    fun render(viewPort: Bitmap, game: IGame) {
        val zBuffer = Buffer2D(viewPort.width, viewPort.height)
        renderFloor(game, viewPort, zBuffer)
        postProcess(viewPort, zBuffer)
    }

    private fun renderFloor(game: IGame, viewport: Bitmap, zBuffer: Buffer2D) {
        val floorBitmap = game.floorTexture.bitmap
        val verticalCameraOffset = sin(GameTime.elapsedTime * 2) * 10

        // Basically the camera lens. Think "how quickly does the depth
        // grow from the edges to the center of the screen".
        val depthScale = 30.0f

        // Basically the FOV, but as a scalar rather than an angle.
        // Think "how closely are parallel lines in the 3D world represented
        // by parallel lines on screen".
        val horizontalFovScale = 1.0f

        // The hypothetical infinite distance at the exact screen center.
        val maxDepth = 10000.0f

        for (yViewPort in 0 until viewport.height) {
            // The vertical distance from the screen center for the current pixel.
            val yDelta = yViewPort - viewport.height / 2.0f
            val yDeltaNormalized = yDelta / viewport.height

            // The depth in global coordinates relative to the camera. When reading the
            // division, imagine "3D depth per normalized 2D coordinate on the screen",
            // i. e. how much further away is a pixel that is located a 10th of the
            // screen size further up.
            // In the screen center, we would be dividing by 0, meaning infinite depth.
            // Below and above the screen center, respectively, the depth grows with
            // smaller distance from the screen center.
            // The screen center can be offset to correspond to vertical camera movement.
            val zGlobal = when {
                yDeltaNormalized < 0 -> min(maxDepth, abs((depthScale + verticalCameraOffset) / yDeltaNormalized))
                yDeltaNormalized > 0 -> min(maxDepth, abs((depthScale - verticalCameraOffset) / yDeltaNormalized))
                else -> maxDepth
            }

            for (xViewport in 0 until viewport.width) {
                // The horizontal distance from the screen center for the current pixel.
                val xDelta = xViewport - viewport.width / 2.0f
                val xDeltaNormalized = xDelta / viewport.width

                // Scaling the horizontal screen coordinates with the depth at that pixel.
                // The higher the depth, the bigger the global distance from the screen
                // center.
                // Think "how many virtual 1 cm cubes would fit into this physical 1 cm
                // on screen".
                val xGlobal = xDeltaNormalized * zGlobal * horizontalFovScale

                val xInCurrentSquare = xGlobal.toInt() and floorBitmap.width - 1
                val yInCurrentSquare = zGlobal.toInt() and floorBitmap.height - 1

                val pixel = floorBitmap.pixels[xInCurrentSquare + yInCurrentSquare * floorBitmap.width]
                viewport.pixels[xViewport + yViewPort * viewport.width] = pixel
                zBuffer.pixels[xViewport + yViewPort * viewport.width] = zGlobal
            }
        }
    }

    private fun postProcess(viewPort: Bitmap, zBuffer: Buffer2D) {
        shadeWithDepth(viewPort, zBuffer)
    }

    private fun shadeWithDepth(viewPort: Bitmap, zBuffer: Buffer2D) {
        // How strong is the hypothetical light which is attached to the camera?
        val lightIntensity = 5000.0f

        for (i in 0 until viewPort.pixels.size) {
            val color = viewPort.pixels[i]
            val r = (color shr 16) and 0xFF
            val g = (color shr 8) and 0xFF
            val b = (color) and 0xFF

            val currentDepth = zBuffer.pixels[i]

            // How quickly does the light fade with increasing depth?
            // (Depending on the exponent, this is faster than Math.pow().)
            val depthWithExponentialFalloff = currentDepth * currentDepth

            val brightness = clamp(
                (lightIntensity / (depthWithExponentialFalloff)),
                0.0f,
                1.0f
            )
            val rShaded = (r * brightness).toInt()
            val gShaded = (g * brightness).toInt()
            val bShaded = (b * brightness).toInt()

            val resultingPixel = (rShaded shl 16) + (gShaded shl 8) + bShaded
            viewPort.pixels[i] = resultingPixel
        }
    }
}