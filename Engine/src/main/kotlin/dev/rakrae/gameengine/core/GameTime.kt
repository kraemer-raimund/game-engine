package dev.rakrae.gameengine.core

class GameTime {

    internal fun onTick() {
        tickTime = elapsedTime
        ticksSinceStartup++
        deltaTime = (currentTimeMillis - previousFrameTimeMillis) / 1000.0f
        scaledDeltaTime = deltaTime * timeScale
        previousFrameTimeMillis = currentTimeMillis
    }

    internal fun onRender() {
        frameTime = elapsedTime
        framesSinceStartup++

        val millis = currentTimeMillis
        if (millis - lastTimeMillis >= 1000) {
            currentFps = framesInCurrentSecond
            framesInCurrentSecond = 0
            lastTimeMillis = millis
        } else {
            framesInCurrentSecond++
        }
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

        /**
         * Number of logical ticks since the start of the game.
         */
        var ticksSinceStartup = 0
            private set

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

        private var lastTimeMillis = currentTimeMillis
    }
}
