package com.rk.mykotlingameengine.core

import com.rk.mykotlingameengine.graphics.Display
import java.awt.Dimension

class Engine {

    private val defaultScreenDimension = Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT)
    private val display = Display(TITLE, defaultScreenDimension)
    private val gameLoop = GameLoop(
        onTick = { println("tick") },
        onRender = { println("render") },
    )

    fun start() {
        gameLoop.start()
    }

    companion object {
        private const val DEFAULT_WIDTH = 800
        private const val DEFAULT_HEIGHT = 600
        private const val TITLE = "MyKotlinGameEngine"
    }
}