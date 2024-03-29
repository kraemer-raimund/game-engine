package dev.rakrae.gameengine.samplegame

import dev.rakrae.gameengine.core.*
import dev.rakrae.gameengine.graphics.Texture
import dev.rakrae.gameengine.math.Vector3f
import kotlin.math.cos
import kotlin.math.sin

class SampleGame : IGame {

    override val floorTexture = Texture("textures/floor.png")
    private var lastFpsTimestamp = GameTime.elapsedTime
    private var playerActor = Actor()
    override var activeCamera = Camera(playerActor)

    override fun onTick() {
        if (GameTime.elapsedTime - lastFpsTimestamp >= 1.0f) {
            println("${FpsCounter.currentFps} FPS")
            lastFpsTimestamp = GameTime.elapsedTime
        }

        activeCamera.owner.transform.apply {
            position = Vector3f(
                sin(GameTime.elapsedTime * 2) * 10,
                cos(GameTime.elapsedTime * 15) * 2,
                GameTime.elapsedTime * 20
            )
            rotationEuler = Vector3f.up * sin(GameTime.elapsedTime * 0.5f) * 2f
        }
    }
}
