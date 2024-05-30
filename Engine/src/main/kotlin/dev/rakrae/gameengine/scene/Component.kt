package dev.rakrae.gameengine.scene

import dev.rakrae.gameengine.graphics.Material
import dev.rakrae.gameengine.graphics.Mesh
import dev.rakrae.gameengine.graphics.rendering.BuiltinShaders
import dev.rakrae.gameengine.graphics.rendering.pipeline.DeferredShader
import dev.rakrae.gameengine.graphics.rendering.pipeline.FragmentShader
import dev.rakrae.gameengine.graphics.rendering.pipeline.VertexShader
import dev.rakrae.gameengine.math.Mat4x4f
import dev.rakrae.gameengine.math.Vec3f
import kotlin.math.cos
import kotlin.math.sin

sealed class Component

class RenderComponent(
    val mesh: Mesh,
    position: Vec3f,
    rotationEulerRad: Vec3f = Vec3f.zero,
    scale: Vec3f = Vec3f.one,
    val material: Material = Material.default,
    val vertexShader: VertexShader = BuiltinShaders.Material.standardPBR.vertexShader,
    val fragmentShader: FragmentShader = BuiltinShaders.Material.standardPBR.fragmentShader,
    val deferredShader: DeferredShader? = null
) : Component() {

    val transformMatrix by lazy { translationMatrix * rotationMatrix * scaleMatrix }

    private val translationMatrix = Mat4x4f(
        1f, 0f, 0f, position.x,
        0f, 1f, 0f, position.y,
        0f, 0f, 1f, position.z,
        0f, 0f, 0f, 1f
    )

    private val rotationMatrix by lazy {
        val rot = rotationEulerRad
        val rotX = Mat4x4f(
            1f, 0f, 0f, 0f,
            0f, cos(rot.x), -sin(rot.x), 0f,
            0f, sin(rot.x), cos(rot.x), 0f,
            0f, 0f, 0f, 1f
        )
        val rotY = Mat4x4f(
            cos(rot.y), 0f, sin(rot.y), 0f,
            0f, 1f, 0f, 0f,
            -sin(rot.y), 0f, cos(rot.y), 0f,
            0f, 0f, 0f, 1f
        )
        val rotZ = Mat4x4f(
            cos(rot.z), -sin(rot.z), 0f, 0f,
            sin(rot.z), cos(rot.z), 0f, 0f,
            0f, 0f, 1f, 0f,
            0f, 0f, 0f, 1f
        )
        rotZ * rotY * rotX
    }

    private val scaleMatrix = Mat4x4f(
        scale.x, 0f, 0f, 0f,
        0f, scale.y, 0f, 0f,
        0f, 0f, scale.z, 0f,
        0f, 0f, 0f, 1f
    )
}
