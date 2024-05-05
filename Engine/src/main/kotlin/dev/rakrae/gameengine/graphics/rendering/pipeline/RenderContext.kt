package dev.rakrae.gameengine.graphics.rendering.pipeline

import dev.rakrae.gameengine.graphics.Bitmap
import dev.rakrae.gameengine.graphics.Buffer2f

/**
 * Passed through the render pipeline to provide the pipeline stages with relevant information
 * about the context in which a primitive is being rendered.
 *
 * Examples:
 * - Screen size and aspect ratio, which might change between frames but remain the same within
 *   a single frame.
 * - The framebuffer and z buffer that remain the same throughout the whole render pipeline
 *   for the current frame.
 * - Rendering and performance settings (like back face culling enabled/disabled) that remain the
 *   same for the current frame and are passed into the render pipeline (rather than accessed
 *   globally from within the render pipeline).
 * - World space coordinates (or matrices for calculating them) which might otherwise be
 *   lost in later pipeline stages where everything is already being processed in normalized
 *   device coordinates (NDC) or viewport coordinates.
 */
internal data class RenderContext(
    val framebuffer: Bitmap,
    val zBuffer: Buffer2f,
    val wComponents: WComponents
) {

    /**
     * The w components of the current triangle's vertices in clip space, i.e., after applying the
     * projection matrix, but before the perspective divide. These are the values that are used in
     * the perspective divide for each respective vertex, and they are required for perspective-
     * correct barycentric interpolation in viewport space.
     */
    data class WComponents(
        val triangle1W: Float,
        val triangle2W: Float,
        val triangle3W: Float
    )
}
