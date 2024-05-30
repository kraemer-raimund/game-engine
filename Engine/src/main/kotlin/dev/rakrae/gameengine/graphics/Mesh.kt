package dev.rakrae.gameengine.graphics

import dev.rakrae.gameengine.math.Vec3f
import dev.rakrae.gameengine.math.Vec4f

data class Mesh(val triangles: List<Triangle>) {

    data class Triangle(
        val v0: Vertex,
        val v1: Vertex,
        val v2: Vertex
    )

    data class Vertex(
        val position: Vec4f,
        val textureCoordinates: Vec3f,
        val normal: Vec3f,
        val tangent: Vec3f,
        val bitangent: Vec3f
    )
}
