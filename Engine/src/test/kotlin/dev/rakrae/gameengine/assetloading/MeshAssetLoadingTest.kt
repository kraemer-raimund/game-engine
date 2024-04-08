package dev.rakrae.gameengine.assetloading

import dev.rakrae.gameengine.TestTag
import dev.rakrae.gameengine.assets.AssetLoader
import dev.rakrae.gameengine.assets.WavefrontObjParser
import dev.rakrae.gameengine.math.Vec4f
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*

@DisplayName("Mesh asset loading")
internal class MeshAssetLoadingTest {

    @Nested
    @DisplayName("OBJ mesh parsing")
    inner class WavefrontObMeshParsingTest {

        @Test
        fun `parses mesh from string in wavefront obj format`() {
            val wavefrontObjString = """
                # some comment followed by empty line, both should be ignored
                
                # vertices
                v 13.37 -200 0
                v 0.0 -0.0 -42
                # texture coordinates
                vt 0 0
                vt 1.7 0.2
                vt 1 1.5
                # vertex normals (not guaranteed to be normalized)
                vn 0.0 0 1.74
                vn -300 0.7 123.456
                # Faces in the format "vertex_index/texture_coordinates_index/vertex_normal_index".
                # These indices refer to the respective lists above and are 1-based.
                f 1/3/1 2/1/1 1/2/2
                # Texture coordinates and vertex normals are optional. Here we have only vertex indices.
                f 1 2 1
                # Then texture coordinates (which are optional) are omitted, there must be two
                # slashes ("//") so that it's unambiguous that vertices and vertex normals are provided.
                f 2//2 1//1 1//2
                # Here we have indices for vertices and texture coordinates. Vertex normals are omitted.
                f 2/2 1/3 2/1
            """.trimIndent()

            val wavefrontObjParser = WavefrontObjParser()
            val mesh = wavefrontObjParser.parse(wavefrontObjString)

            assertAll(
                { assertThat(mesh.triangles).hasSize(4) },
                { assertThat(mesh.triangles[0].v1.position).isEqualTo(Vec4f(13.37f, -200f, 0f, 1f)) }
            )
        }
    }

    @Nested
    @Tag(TestTag.INTEGRATION_TEST)
    @DisplayName("OBJ mesh asset loading")
    inner class WavefrontObjAssetLoadingTest {

        @Test
        fun `loads mesh from wavefront obj file`() {
            val wavefrontObjPath = "/assetloading/cube.obj"

            val assetLoader = AssetLoader()
            val mesh = assetLoader.loadMesh(wavefrontObjPath)

            assertAll(
                { assertThat(mesh.triangles).hasSize(12) },
                { assertThat(mesh.triangles[0].v1.position).isEqualTo(Vec4f(-1f, 1f, -1f, 1f)) }
            )
        }
    }
}
