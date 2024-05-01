package dev.rakrae.gameengine.platform

import dev.rakrae.gameengine.input.InputAdapter
import java.awt.event.KeyEvent
import java.awt.event.KeyListener

internal class AwtInputAdapter : InputAdapter, KeyListener {

    override val horizontalAxisNormalized: Float
        get() {
            return when {
                isAKeyPressed -> -1f
                isDKeyPressed -> 1f
                else -> 0f
            }
        }

    private var isAKeyPressed = false
    private var isDKeyPressed = false

    override fun keyPressed(e: KeyEvent?) {
        if (e?.keyCode == KeyEvent.VK_A) {
            println("A")
            isAKeyPressed = true
        }
        if (e?.keyCode == KeyEvent.VK_D) {
            isDKeyPressed = true
            println("D")
        }
    }

    override fun keyReleased(e: KeyEvent?) {
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
