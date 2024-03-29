package dev.rakrae.gameengine.core

class FpsCounter {

    fun onTick() {
        framesSinceStartup++

        val currentTimeMillis = System.currentTimeMillis()
        if (currentTimeMillis - lastTimeMillis >= 1000) {
            currentFps = framesInCurrentSecond
            framesInCurrentSecond = 0
            lastTimeMillis = currentTimeMillis
        } else {
            framesInCurrentSecond++
        }
    }

    companion object {

        /**
         * The current frames per second at a granularity of 1 second.
         */
        var currentFps = 0
            private set

        private var framesInCurrentSecond = 0

        /**
         * Number of frames since the start of the game.
         */
        var framesSinceStartup = 0
            private set

        private var lastTimeMillis = System.currentTimeMillis()
    }
}
