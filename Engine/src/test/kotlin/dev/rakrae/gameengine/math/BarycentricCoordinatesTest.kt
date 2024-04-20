package dev.rakrae.gameengine.math

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.offset
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

@DisplayName("Barycentric Coordinates")
internal class BarycentricCoordinatesTest {

    @Nested
    @DisplayName("Cartesian to Barycentric")
    inner class CalculateBarycentricCoordinatesTest {

        @Test
        fun `point within triangle`() {
            val point = Vec2i(3, 7)
            val triangle = Triangle2i(
                Vec2i(-4, -2),
                Vec2i(11, -8),
                Vec2i(17, 35)
            )

            val barycentric = BarycentricCoordinates.of(point, triangle)

            assertAll(
                { assertThat(barycentric.a1).isCloseTo(0.63729f, offset(0.0001f)) },
                { assertThat(barycentric.a2).isCloseTo(0.10279f, offset(0.0001f)) },
                { assertThat(barycentric.a3).isCloseTo(0.25991f, offset(0.0001f)) }
            )
        }
    }

    @Nested
    @DisplayName("Is point in triangle?")
    inner class PointWithinTriangleTest {

        @Test
        fun `point within triangle`() {
            val point = Vec2i(3, 7)
            val triangle = Triangle2i(
                Vec2i(-4, -2),
                Vec2i(11, -8),
                Vec2i(17, 35)
            )

            val barycentric = BarycentricCoordinates.of(point, triangle)

            assertTrue(barycentric.isWithinTriangle)
        }

        @Test
        fun `point outside of triangle`() {
            val point = Vec2i(3000, 7000)
            val triangle = Triangle2i(
                Vec2i(-4, -2),
                Vec2i(11, -8),
                Vec2i(17, 35)
            )

            val barycentric = BarycentricCoordinates.of(point, triangle)

            assertFalse(barycentric.isWithinTriangle)
        }
    }
}
