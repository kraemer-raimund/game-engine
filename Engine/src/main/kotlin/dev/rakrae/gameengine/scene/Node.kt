package dev.rakrae.gameengine.scene

import dev.rakrae.gameengine.graphics.Material

/**
 * A node within the scene graph, representing an object in the scene.
 */
class Node(
    val renderComponent: RenderComponent,
    val material: Material = Material.default
)
