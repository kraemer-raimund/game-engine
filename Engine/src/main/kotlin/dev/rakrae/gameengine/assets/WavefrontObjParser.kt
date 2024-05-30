package dev.rakrae.gameengine.assets

import dev.rakrae.gameengine.graphics.Mesh
import dev.rakrae.gameengine.math.Vec3f
import dev.rakrae.gameengine.math.Vec4f

internal class WavefrontObjParser {

    /**
     * Parse a mesh from a string in Wavefront OBJ format. This file format is relatively
     * easy to parse:
     *
     * ## Vertices
     *
     * Vertices are encoded in lines starting with "v", followed by the x/y/z coordinates.
     *
     * ## Faces/polygons
     *
     * Faces are encoded in lines starting with "f", followed by 3 (for triangles) or more
     * (1-based) indices of the vertex, texture coordinates, and vertex normal within the
     * respective list.
     * Negative indices are allowed and may be used to index the list from the end.
     * For example, a face might be defined as "f 1219/1276/37 1220/1274/39 5783/1275/39",
     * where 1219, 1220, and 5783 would be the 1-based indices of the respective vertices,
     * 1276, 1274, and 1275 would be the 1-based indices of the respective UV coordinates,
     * and 37, 39, and 39 would be the 1-based indices of the respective vertex normal vectors.
     * Face normals are implied by counter-clockwise order of vertices per polygon.
     *
     * Furthermore, it encodes texture (UV) coordinates, vertex normals, and some more data in a
     * similar way.
     */
    fun parse(wavefrontObj: String): Mesh {
        val vertexPositions = mutableListOf<VertexPosition>()
        val uvs = mutableListOf<TextureCoordinates>()
        val normals = mutableListOf<VertexNormal>()
        val faces = mutableListOf<PolygonFace>()

        wavefrontObj.lineSequence().forEach { rawLine ->
            val line = rawLine.trim()
            if (line.isBlank() || line.isComment()) return@forEach

            val values = line
                .split(" ")
                .map { it.trim() }
                .drop(1) // Skip the line marker, like "v", "vt", etc.

            when {
                line.startsWith("v ") -> vertexPositions.add(parseVertexCoordinates(values))
                line.startsWith("vt ") -> uvs.add(parseTextureCoordinates(values))
                line.startsWith("vn ") -> normals.add(parseVertexNormal(values))
                line.startsWith("f ") -> faces.add(parsePolygonFace(values))
                line.startsWith("o") -> {} // The object's name; ignored for now.
                line.startsWith("s") -> {} // Smoothing groups; ignored for now.
                else -> throw WavefrontObjParseException("Ill-formed line encountered in Wavefront OBJ input:\n$line")
            }
        }

        val triangles: List<Mesh.Triangle> = assembleTriangles(faces, vertexPositions, uvs, normals)

        return Mesh(triangles)
    }

    /**
     * Using the indices in the face/triangle definitions, index the lists of respective vertex data
     * to build up the list of all triangles in the mesh.
     */
    private fun assembleTriangles(
        faces: List<PolygonFace>,
        vertexPositions: List<VertexPosition>,
        uvs: List<TextureCoordinates>,
        normals: List<VertexNormal>
    ): List<Mesh.Triangle> {
        return faces.map { face ->
            if (face.vertices.size != 3) throw WavefrontObjParseException(
                "Non-triangulated polygon encountered while parsing Wavefront OBJ. While non-triangle polygons " +
                        "are allowed by the file format, the current parser implementation assumes triangles only."
            )
            val vertices = face.vertices.map {
                Mesh.Vertex(
                    position = vertexPositions[it.posIndex],
                    textureCoordinates = uvs.getOrElse(it.uvIndex) { Vec3f.zero },
                    normal = normals.getOrElse(it.normalIndex) { Vec3f.zero },
                    // Tangent and bitangent are not provided by the Wavefront OBJ file format, and
                    // can be calculated in a separate step if required. We set them to 0 by default.
                    tangent = Vec3f.zero,
                    bitangent = Vec3f.zero
                )
            }
            Mesh.Triangle(vertices[0], vertices[1], vertices[2])
        }
    }

    private fun parseVertexCoordinates(values: List<String>): VertexPosition {
        return VertexPosition(
            values[0].toFloat(),
            values[1].toFloat(),
            values[2].toFloat(),
            // The w coordinate is optional and defaults to 1.0.
            values.getOrNull(3)?.toFloat() ?: 1.0f
        )
    }

    private fun parseTextureCoordinates(values: List<String>): TextureCoordinates {
        return TextureCoordinates(
            values[0].toFloat(),
            // OBJ uses top-left as 0.0 in texture coordinates, but we follow OpenGL's example
            // in using bottom-left, so we need to invert the v coordinate.
            // The v coordinate is optional and defaults to 0.0.
            if (values.size >= 2) 1f - values[1].toFloat() else 0f,
            // The w coordinate is optional and defaults to 0.0.
            if (values.size >= 3) values[2].toFloat() else 0f
        )
    }

    private fun parseVertexNormal(values: List<String>): VertexNormal {
        return VertexNormal(
            values[0].toFloat(),
            values[1].toFloat(),
            values[2].toFloat()
        )
    }

    private fun parsePolygonFace(values: List<String>): PolygonFace {
        return PolygonFace(
            values.map {
                val indices = it.split("/")

                val posIndex = indices[0].toInt()

                // UV index is optional and may be omitted like so "7//1" (specifying only vertex position index
                // and normal index), or by specifying only the vertex position index (e.g., "7").
                // We use -1 to indicate an invalid/missing index.
                val uvIndex = if (indices.size >= 2 && indices[1].isNotBlank()) indices[1].toInt() else -1

                // Normal index is optional. We use -1 to indicate an invalid/missing index.
                val normalIndex = if (indices.size >= 3) indices[2].toInt() else -1

                // Correct for 1-based indices used in the Wavefront OBJ format, unless they are
                // already -1 to indicate a missing index.
                VertexIndices(
                    posIndex - 1,
                    if (uvIndex >= 0) uvIndex - 1 else -1,
                    if (normalIndex >= 0) normalIndex - 1 else -1
                )
            }
        )
    }

    private fun String.isComment(): Boolean {
        return this.startsWith("#")
    }
}

private typealias VertexPosition = Vec4f
private typealias TextureCoordinates = Vec3f
private typealias VertexNormal = Vec3f

private class VertexIndices(val posIndex: Int, val uvIndex: Int, val normalIndex: Int)
private class PolygonFace(val vertices: List<VertexIndices>)

class WavefrontObjParseException(message: String) : RuntimeException(message)
