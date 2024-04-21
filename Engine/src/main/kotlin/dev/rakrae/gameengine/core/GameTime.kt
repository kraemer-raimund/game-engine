package dev.rakrae.gameengine.core

class GameTime {

    fun onTick() {
        tickTime = elapsedTime
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
         * Elapsed time since the start of the game in seconds. If this needs to remain consistent
         * between calls during the same tick, use [tickTime] instead.
         */
        val elapsedTime get() = (currentTimeMillis - startTimeMillis) / 1000.0f

        /**
         * Elapsed time in seconds at the __start of the current tick__. This time remains the
         * same throughout the duration of the current tick.
         */
        var tickTime = 0.0f
            private set

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
