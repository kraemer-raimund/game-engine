package dev.rakrae.gameengine.scene

/**
 * A node within the scene graph, representing an object in the scene.
 */
sealed class Node(val renderComponent: RenderComponent?)

class MeshNode(renderComponent: RenderComponent?) : Node(renderComponent)
