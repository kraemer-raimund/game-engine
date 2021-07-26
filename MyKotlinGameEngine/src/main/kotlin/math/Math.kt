package com.rk.mykotlingameengine.math

import kotlin.math.max
import kotlin.math.min

fun clamp(value: Float, minValue: Float, maxValue:Float): Float {
    return min(maxValue, max(minValue, value))
}