package dev.rakrae.gameengine.assets

import dev.rakrae.gameengine.graphics.Mesh
import java.io.IOException

class AssetLoader {

    private val wavefrontObjParser = WavefrontObjParser()

    fun loadMesh(assetPath: String): Mesh {
        val fileExtension = assetPath.substringAfterLast('.', "")
        val mesh = when (fileExtension) {
            "obj" -> parseWavefrontObj(assetPath)
            else -> throw AssetLoadingException("Unable to load mesh from file: $assetPath")
        }
        return MeshTangentSpaceBaking.bakeTangentSpace(mesh)
    }

    private fun parseWavefrontObj(assetPath: String): Mesh {
        try {
            val wavefrontObjString = javaClass.getResource(assetPath)!!.readText()
            return wavefrontObjParser.parse(wavefrontObjString)
        } catch (e: Throwable) {
            throw AssetLoadingException("Unable to load mesh from file: $assetPath", cause = e)
        }
    }
}

class AssetLoadingException(message: String, cause: Throwable? = null) : IOException(message, cause)
