package dev.rakrae.gameengine.scene

/**
 * A node within the scene graph, representing an object in the scene.
 */
abstract class Node

abstract class TransformNode(val transform: Transform) : Node()

class MeshNode(val renderComponent: RenderComponent) : TransformNode(
    Transform(
        position = renderComponent.position,
        rotationEulerRad = renderComponent.rotationEulerRad,
        scale = renderComponent.scale
    )
)
