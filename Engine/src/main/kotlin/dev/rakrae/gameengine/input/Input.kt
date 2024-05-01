package dev.rakrae.gameengine.input

object Input {

    internal var inputAdapter: InputAdapter? = null

    val verticalAxisNormalized: Float
        get() = inputAdapter?.verticalAxisNormalized ?: 0f

    val horizontalAxisNormalized: Float
        get() = inputAdapter?.horizontalAxisNormalized ?: 0f
}
