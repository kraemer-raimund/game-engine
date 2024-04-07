package dev.rakrae.gameengine.assets

import dev.rakrae.gameengine.graphics.Mesh
import java.io.File
import java.io.IOException

class AssetLoader {

    private val wavefrontObjParser = WavefrontObjParser()

    fun loadMesh(file: File): Mesh {
        if (!file.isFile) throw AssetLoadingException("Provided path is not a file: ${file.path}")

        when (file.extension) {
            "obj" -> return parseWavefrontObj(file)
            else -> throw AssetLoadingException("Unable to load mesh from file: ${file.path}")
        }
    }

    private fun parseWavefrontObj(file: File): Mesh {
        try {
            return wavefrontObjParser.parse(file)
        } catch (e: WavefrontObjParseException) {
            throw AssetLoadingException("Unable to load mesh from file: ${file.path}", cause = e)
        }
    }
}

class AssetLoadingException(message: String, cause: Throwable? = null) : IOException(message, cause)
