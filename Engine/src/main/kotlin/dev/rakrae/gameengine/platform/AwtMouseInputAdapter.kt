package dev.rakrae.gameengine.platform

import dev.rakrae.gameengine.core.GameLifeCycleReceiver
import dev.rakrae.gameengine.core.GameTime
import dev.rakrae.gameengine.input.AxisPairProvider
import dev.rakrae.gameengine.math.Vec2f
import dev.rakrae.gameengine.math.Vec2i
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionListener

internal class AwtMouseInputAdapter : AxisPairProvider, MouseMotionListener, GameLifeCycleReceiver {

    override val axisPair: Vec2f
        get() = Vec2f(horizontalAxis, verticalAxis)

    private var horizontalAxis: Float = 0f
    private var verticalAxis: Float = 0f

    private var lastMousePosition: Vec2i = Vec2i(0, 0)
    private var unconsumedMouseEvent: MouseEvent? = null

    override suspend fun onTick() {
        if (unconsumedMouseEvent != null) {
            val mousePosition = Vec2i(unconsumedMouseEvent?.x ?: 0, unconsumedMouseEvent?.y ?: 0)

            val horizontalDelta = mousePosition.x - lastMousePosition.x
            val scaledHorizontalDelta = horizontalDelta * GameTime.tickTime
            horizontalAxis = (scaledHorizontalDelta / 1000f).coerceIn(-1f, 1f)

            val verticalDelta = mousePosition.y - lastMousePosition.y
            val scaledVerticalDelta = verticalDelta * GameTime.tickTime
            verticalAxis = (scaledVerticalDelta / 1000f).coerceIn(-1f, 1f)

            lastMousePosition = mousePosition
            unconsumedMouseEvent = null
        } else {
            horizontalAxis = 0f
            verticalAxis = 0f
        }
    }

    override suspend fun onStart() = Unit
    override suspend fun onPause() = Unit
    override suspend fun onResume() = Unit
    override suspend fun onStop() = Unit

    override fun mouseDragged(e: MouseEvent?) {
        // Event ignored.
    }

    override fun mouseMoved(e: MouseEvent?) {
        unconsumedMouseEvent = e
    }
}
