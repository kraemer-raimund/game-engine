package com.rk.mykotlingameengine.core

/**
 * An object representing an entity in the 3D world.
 */
class Actor {

    val transform = Transform(this)

    private val _components: MutableList<Component> = mutableListOf(transform)
    val components: List<Component> get() = _components

    fun addComponent(component: Component) {
        _components.add(component)
    }
}