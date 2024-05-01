package dev.rakrae.gameengine.platform

import dev.rakrae.gameengine.input.InputAdapter
import java.awt.event.KeyEvent
import java.awt.event.KeyListener

internal class AwtInputAdapter : InputAdapter, KeyListener {

    override val verticalAxisNormalized: Float
        get() {
            return when {
                isWKeyPressed -> 1f
                isSKeyPressed -> -1f
                else -> 0f
            }
        }

    override val horizontalAxisNormalized: Float
        get() {
            return when {
                isAKeyPressed -> -1f
                isDKeyPressed -> 1f
                else -> 0f
            }
        }

    private var isWKeyPressed = false
    private var isSKeyPressed = false
    private var isAKeyPressed = false
    private var isDKeyPressed = false

    override fun keyPressed(e: KeyEvent?) {
        if (e?.keyCode == KeyEvent.VK_W) {
            isWKeyPressed = true
        }
        if (e?.keyCode == KeyEvent.VK_S) {
            isSKeyPressed = true
        }
        if (e?.keyCode == KeyEvent.VK_A) {
            isAKeyPressed = true
        }
        if (e?.keyCode == KeyEvent.VK_D) {
            isDKeyPressed = true
        }
    }

    override fun keyReleased(e: KeyEvent?) {
        if (e?.keyCode == KeyEvent.VK_W) {
            isWKeyPressed = false
        }
        if (e?.keyCode == KeyEvent.VK_S) {
            isSKeyPressed = false
        }
        if (e?.keyCode == KeyEvent.VK_A) {
            isAKeyPressed = false
        }
        if (e?.keyCode == KeyEvent.VK_D) {
            isDKeyPressed = false
        }
    }

    override fun keyTyped(e: KeyEvent?) {
        // Nothing to do.
    }
}
