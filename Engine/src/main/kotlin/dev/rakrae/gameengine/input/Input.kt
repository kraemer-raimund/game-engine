package dev.rakrae.gameengine.input

object Input {

    internal var inputAdapter: InputAdapter? = null

    val horizontalAxis: Float
        get() = inputAdapter?.horizontalAxisNormalized ?: 0f
}
