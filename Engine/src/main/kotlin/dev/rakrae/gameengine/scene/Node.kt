package dev.rakrae.gameengine.scene

import dev.rakrae.gameengine.graphics.Mesh
import dev.rakrae.gameengine.math.Vec3f

/**
 * A node within the scene graph, representing an object in the scene.
 */
class Node(val mesh: Mesh, val position: Vec3f)
