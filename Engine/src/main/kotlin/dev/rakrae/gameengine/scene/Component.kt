package dev.rakrae.gameengine.scene

import dev.rakrae.gameengine.graphics.Mesh
import dev.rakrae.gameengine.math.Vec3f

sealed class Component

class RenderComponent(val mesh: Mesh, val position: Vec3f) : Component()
