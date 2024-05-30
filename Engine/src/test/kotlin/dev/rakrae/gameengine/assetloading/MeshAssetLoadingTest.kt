package dev.rakrae.gameengine.assetloading

import dev.rakrae.gameengine.TestTag
import dev.rakrae.gameengine.assets.*
import dev.rakrae.gameengine.graphics.Mesh
import dev.rakrae.gameengine.math.Vec3f
import dev.rakrae.gameengine.math.Vec4f
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*

@DisplayName("Mesh asset loading")
internal class MeshAssetLoadingTest {

    @Nested
    @DisplayName("OBJ mesh parsing")
    inner class WavefrontObMeshParsingTest {

        @Test
        fun `parses mesh from string in wavefront obj format`() {
            val wavefrontObjString = """
                # Some comment followed by an empty line, both should be ignored.
                
                # vertices
                v 13.37 -200 0
                v 0.0 -0.0 -42
                
                # texture coordinates (u, v, w) within the interval (0.0; 1.0)
                #   v and w are optional and default to 0.
                #   The origin (0, 0) is at the top-left corner, whereas we follow the OpenGL convention
                #   and use the bottom-left corner as the origin.
                vt 0 0 0.69
                vt 0.7
                vt 1 0.4
                
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

            assertThat(mesh.triangles).hasSize(4)

            with(mesh.triangles[0].v0) {
                assertAll(
                    { assertThat(position).isEqualTo(Vec4f(13.37f, -200f, 0f, 1f)) },
                    { assertThat(textureCoordinates).isEqualTo(Vec3f(1f, 0.6f, 0f)) },
                    { assertThat(normal).isEqualTo(Vec3f(0f, 0f, 1.74f)) },
                )
            }

            with(mesh.triangles[0].v2) {
                assertAll(
                    { assertThat(position).isEqualTo(Vec4f(13.37f, -200f, 0f, 1f)) },
                    { assertThat(textureCoordinates).isEqualTo(Vec3f(0.7f, 0f, 0f)) },
                    { assertThat(normal).isEqualTo(Vec3f(-300f, 0.7f, 123.456f)) },
                )
            }

            with(mesh.triangles[1].v0) {
                assertAll(
                    { assertThat(position).isEqualTo(Vec4f(13.37f, -200f, 0f, 1f)) },
                    { assertThat(textureCoordinates).isEqualTo(Vec3f(0f, 0f, 0f)) },
                    { assertThat(normal).isEqualTo(Vec3f(0f, 0f, 0f)) },
                )
            }

            with(mesh.triangles[2].v0) {
                assertAll(
                    { assertThat(position).isEqualTo(Vec4f(0f, -0f, -42f, 1f)) },
                    { assertThat(textureCoordinates).isEqualTo(Vec3f(0f, 0f, 0f)) },
                    { assertThat(normal).isEqualTo(Vec3f(-300f, 0.7f, 123.456f)) },
                )
            }

            with(mesh.triangles[3].v1) {
                assertAll(
                    { assertThat(position).isEqualTo(Vec4f(13.37f, -200f, 0f, 1f)) },
                    { assertThat(textureCoordinates).isEqualTo(Vec3f(1f, 0.6f, 0f)) },
                    { assertThat(normal).isEqualTo(Vec3f(0f, 0f, 0f)) },
                )
            }
        }

        @Test
        fun `throws exception in case of ill-formed wavefront obj input`() {
            assertThatExceptionOfType(WavefrontObjParseException::class.java)
                .isThrownBy { WavefrontObjParser().parse("asdf") }
        }

        @Test
        fun `throws exception when triangle face does not have 3 vertices`() {
            val wavefrontObjString = """
                v 69 420 13.37
                # Only one vertex for the triangle face, but 3 expected.
                f 1
            """.trimIndent()

            assertThatExceptionOfType(WavefrontObjParseException::class.java)
                .isThrownBy { WavefrontObjParser().parse(wavefrontObjString) }
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
                { assertThat(mesh.triangles[0].v0.position).isEqualTo(Vec4f(-1f, 1f, -1f, 1f)) }
            )
        }

        @Test
        fun `throws exception when file does not exist`() {
            assertThatExceptionOfType(AssetLoadingException::class.java)
                .isThrownBy { AssetLoader().loadMesh("/does-not-exist.obj") }
        }
    }

    @Nested
    @DisplayName("Baking tangent and bitangent vectors")
    inner class TangentSpaceBakingTest {

        @Test
        fun `tangent and bitangent are perpendicular to each other and to the normal`() {
            val meshBeforeBakingTangentSpace = Mesh(
                listOf(
                    Mesh.Triangle(
                        v0 = Mesh.Vertex(
                            position = Vec4f(x = 123.45f, y = -200.0f, z = 0.0f, w = 1.0f),
                            textureCoordinates = Vec3f(x = 0.1f, y = 0.6f, z = 0.0f),
                            normal = Vec3f(x = 0.0f, y = 0.0f, z = 1.74f),
                            tangent = Vec3f(x = 0.0f, y = 0.0f, z = 0.0f),
                            bitangent = Vec3f(x = 0.0f, y = 0.0f, z = 0.0f)
                        ),
                        v1 = Mesh.Vertex(
                            position = Vec4f(x = 0.0f, y = -0.0f, z = -42.0f, w = 1.0f),
                            textureCoordinates = Vec3f(x = 0.8f, y = 0.9f, z = 0.0f),
                            normal = Vec3f(x = 0.0f, y = 0.0f, z = 1.74f),
                            tangent = Vec3f(x = 0.0f, y = 0.0f, z = 0.0f),
                            bitangent = Vec3f(x = 0.0f, y = 0.0f, z = 0.0f)
                        ),
                        v2 = Mesh.Vertex(
                            position = Vec4f(x = 69.42f, y = -200.0f, z = 0.0f, w = 1.0f),
                            textureCoordinates = Vec3f(x = 0.5f, y = 0.72f, z = 0.0f),
                            normal = Vec3f(x = -300.0f, y = 0.7f, z = 123.456f),
                            tangent = Vec3f(x = 0.0f, y = 0.0f, z = 0.0f),
                            bitangent = Vec3f(x = 0.0f, y = 0.0f, z = 0.0f)
                        )
                    ), Mesh.Triangle(
                        v0 = Mesh.Vertex(
                            position = Vec4f(x = 13.37f, y = -12.0f, z = 0.0f, w = 1.0f),
                            textureCoordinates = Vec3f(x = 0.0f, y = 1.0f, z = 0.0f),
                            normal = Vec3f(x = 08.0f, y = 0.42f, z = 13.37f),
                            tangent = Vec3f(x = 0.0f, y = 0.0f, z = 0.0f),
                            bitangent = Vec3f(x = 0.0f, y = 0.0f, z = 0.0f)
                        ),
                        v1 = Mesh.Vertex(
                            position = Vec4f(x = 0.0f, y = 7.0f, z = -42.0f, w = 1.0f),
                            textureCoordinates = Vec3f(x = 0.5f, y = 0.0f, z = 0.0f),
                            normal = Vec3f(x = 0.2f, y = 0.0f, z = 5.0f),
                            tangent = Vec3f(x = 0.0f, y = 0.0f, z = 0.0f),
                            bitangent = Vec3f(x = 0.0f, y = 0.0f, z = 0.0f)
                        ),
                        v2 = Mesh.Vertex(
                            position = Vec4f(x = 1.23f, y = 45.67f, z = -89.1f, w = 1.0f),
                            textureCoordinates = Vec3f(x = 1.0f, y = 1.0f, z = 0.0f),
                            normal = Vec3f(x = 1.0f, y = 0.0f, z = 0.0f),
                            tangent = Vec3f(x = 0.0f, y = 0.0f, z = 0.0f),
                            bitangent = Vec3f(x = 0.0f, y = 0.0f, z = 0.0f)
                        )
                    )
                )
            )

            val meshWithTangentAndBitangent = MeshTangentSpaceBaking.bakeTangentSpace(meshBeforeBakingTangentSpace)

            assertThat(meshWithTangentAndBitangent.triangles).allSatisfy { triangle ->
                val vertices = listOf(triangle.v0, triangle.v1, triangle.v2)

                assertThat(vertices).allSatisfy { vertex ->
                    assertThat(vertex.normal.magnitude).isNotEqualTo(0f)
                    assertThat(vertex.tangent.magnitude).isNotEqualTo(0f)
                    assertThat(vertex.bitangent.magnitude).isNotEqualTo(0f)

                    assertThat(vertex.normal dot vertex.tangent)
                        .isEqualTo(0f, withPrecision(0.01f))
                        .describedAs("Normal and tangent are perpendicular.")
                }
            }
        }
    }
}
