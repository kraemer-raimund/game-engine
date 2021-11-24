package com.rk.mykotlingameengine.core

class GameTime {

    fun onTick() {
        deltaTime = (currentTimeMillis - previousFrameTimeMillis) / 1000.0f
        previousFrameTimeMillis = currentTimeMillis
    }

    companion object {
        /**
         * Timestamp at the start of the game in milliseconds.
         */
        private val startTimeMillis = System.currentTimeMillis()

        /**
         * Current timestamp in milliseconds.
         */
        private val currentTimeMillis get() = System.currentTimeMillis()

        /**
         * Elapsed time since start of the game in seconds.
         */
        val elapsedTime get() = (currentTimeMillis - startTimeMillis) / 1000.0f

        /**
         * Time difference since the last frame in seconds.
         */
        var deltaTime = 0.0f
            private set

        /**
         * Timestamp at the previous frame in seconds.
         */
        private var previousFrameTimeMillis = startTimeMillis
    }
}