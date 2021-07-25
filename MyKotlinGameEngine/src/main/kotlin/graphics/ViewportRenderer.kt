package com.rk.mykotlingameengine.graphics

import com.rk.mykotlingameengine.core.IGame
import com.rk.mykotlingameengine.math.clamp
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class ViewportRenderer {

    fun render(viewPort: Bitmap, game: IGame) {
        val zBuffer = Buffer2D(viewPort.width, viewPort.height)
        renderFloorAndCeiling(game, viewPort, zBuffer)
        postProcess(viewPort, zBuffer)
    }

    private fun renderFloorAndCeiling(game: IGame, viewport: Bitmap, zBuffer: Buffer2D) {
        val floorBitmap = game.floorTexture.bitmap

        val camera = game.activeCamera
        val cameraPosition = camera.owner.transform.position
        val cameraRotationY = camera.owner.transform.rotationEuler.y

        // Precalculating these since they don't change per pixel. See below for
        // usage, and https://en.wikipedia.org/wiki/Rotation_matrix for formula.
        val rotYCos = cos(cameraRotationY)
        val rotYSin = sin(cameraRotationY)

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
                yDeltaNormalized < 0 -> min(
                    camera.maxDepth,
                    abs((camera.lens + cameraPosition.y) / yDeltaNormalized)
                )
                yDeltaNormalized > 0 -> min(
                    camera.maxDepth,
                    abs((camera.lens - cameraPosition.y) / yDeltaNormalized)
                )
                else -> camera.maxDepth
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
                val xGlobal = xDeltaNormalized * zGlobal * camera.horizontalFovMultiplier

                // Applying the rotation. See https://en.wikipedia.org/wiki/Rotation_matrix
                // for formula and explanation.
                val xAfterRotation = xGlobal * rotYCos - zGlobal * rotYSin
                val zAfterRotation = xGlobal * rotYSin + zGlobal * rotYCos

                val xWithOffset = xAfterRotation + cameraPosition.x
                val zWithOffset = zAfterRotation + cameraPosition.z

                val xInCurrentSquare = xWithOffset.toInt() and floorBitmap.width - 1
                val zInCurrentSquare = zWithOffset.toInt() and floorBitmap.height - 1

                val pixel = floorBitmap.pixels[xInCurrentSquare + zInCurrentSquare * floorBitmap.width]
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