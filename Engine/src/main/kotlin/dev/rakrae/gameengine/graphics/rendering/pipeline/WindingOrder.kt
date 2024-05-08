package dev.rakrae.gameengine.graphics.rendering.pipeline

/**
 * After projection, a triangle has a certain order in which the vertices appear on screen. The
 * vertices of a triangle appear either clockwise (CW) or counter-clockwise (CCW) around the
 * triangle's center. The winding order is used to determine which faces are oriented away from the
 * screen and thus don't need to be rendered (i.e., back-face culling).
 *
 * This "winding order" of the triangle depends on 2 factors:
 * 1. in which order the vertices are defined in the mesh
 * 2. which side (front-face or back-face) of the triangle is oriented towards the screen
 *
 * Note: The winding order alone is not sufficient to determine which side can be culled. It
 * depends on the orientation of the triangle (after it has been transformed into clip space)
 * *and* the convention used by the underlying vertex data. By default, we use counter-clockwise
 * winding for the front-face.
 *
 * ### Example
 *
 * Given this triangle:
 * ```
 *       1
 *      /|
 *    /  |
 *  /    |
 * 3 _ _ 2
 * ```
 * In the counter-clockwise convention, we would be looking at a back-face, assuming the vertices
 * are defined in the triangle in the order 1-2-3. The counter-clockwise reading would be 1-3-2.
 *
 * Mathematically, the winding order can be calculated by determining the sign of the triangle's
 * area.
 *
 * @see <a href="https://www.khronos.org/opengl/wiki/Face_Culling#Winding_order">Winding order (OpenGL)</a>
 * @see <a href="https://en.wikipedia.org/wiki/Back-face_culling">Back-face culling (Wikipedia)</a>
 */
enum class WindingOrder {
    CounterClockwise,
    Clockwise
}
