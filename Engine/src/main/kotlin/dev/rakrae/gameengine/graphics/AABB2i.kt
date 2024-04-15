package dev.rakrae.gameengine.graphics

import dev.rakrae.gameengine.math.Vec2i

/**
 * An axis-aligned bounding box (AABB) in 2 dimensions with integer coordinates, as used for
 * example for the rectangle of pixels around a set of points on the screen.
 */
data class AABB2i(val min: Vec2i, val max: Vec2i)