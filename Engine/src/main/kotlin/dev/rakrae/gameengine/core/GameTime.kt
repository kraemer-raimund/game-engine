package dev.rakrae.gameengine.core

class GameTime {

    fun onTick() {
        tickTime = elapsedTime
        deltaTime = (currentTimeMillis - previousFrameTimeMillis) / 1000.0f
        scaledDeltaTime = deltaTime * timeScale
        previousFrameTimeMillis = currentTimeMillis
    }

    fun onRender() {
        frameTime = elapsedTime
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
         * Elapsed time in seconds at the __start of the current render frame__. This time remains
         * the same throughout the duration of the current render frame, and is independent of
         * the logical tick rate.
         */
        var frameTime = 0.0f
            private set

        /**
         * The time difference since the last frame in seconds.
         */
        var deltaTime = 0.0f
            private set

        /**
         * The time difference since the last frame in seconds, scaled by an arbitrary [timeScale]
         * factor.
         *
         * This can be helpful, e.g., for creating slow motion or speed-up effects that should
         * affect the whole game.
         */
        var scaledDeltaTime = 0.0f
            private set

        /**
         * An arbitrary timescale factor that affects [scaledDeltaTime].
         *
         * This can be helpful, e.g., for creating slow motion or speed-up effects that should
         * affect the whole game.
         */
        var timeScale = 1.0f

        /**
         * Timestamp at the previous frame in seconds.
         */
        private var previousFrameTimeMillis = startTimeMillis
    }
}
