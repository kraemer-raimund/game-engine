package dev.rakrae.gameengine.graphics

import dev.rakrae.gameengine.core.IGame
import dev.rakrae.gameengine.math.clamp
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * Renders the scene into the viewport to be displayed on screen.
 *
 * ### Inspiration
 * The main rendering technique used here is similar to [Mode 7](https://en.wikipedia.org/wiki/Mode_7),
 * a sort of "fake 3D" that was originally created for early game consoles.
 * The particular implementation is mainly inspired by a game jam live stream
 * from 2011 by Minecraft creator Notch, although heavily modified and less
 * "game jamy". The live stream can be found on YouTube as an archived
 * re-upload named ["Notch Coding Prelude of the Chambered"](https://www.youtube.com/watch?v=GQO3SSlsgJM).
 * I highly recommend this and his other game jam series as a learning resource.
 *
 * This might be heavily changed or wholly replaced over time as I experiment
 * with different rendering techniques.
 */
class ViewportRenderer {

    fun render(viewport: Bitmap, game: IGame) {
        val zBuffer = Buffer2D(viewport.width, viewport.height)
        renderFloorAndCeiling(game, viewport, zBuffer)
        postProcess(viewport, zBuffer)
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

    private fun postProcess(viewport: Bitmap, zBuffer: Buffer2D) {
        shadeWithDepth(viewport, zBuffer)
    }

    private fun shadeWithDepth(viewport: Bitmap, zBuffer: Buffer2D) {
        // How strong is the hypothetical light which is attached to the camera?
        val lightIntensity = 5000.0f

        for (i in 0 until viewport.pixels.size) {
            val color = viewport.pixels[i]
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
            viewport.pixels[i] = resultingPixel
        }
    }
}
