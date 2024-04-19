package dev.rakrae.gameengine.graphics

import dev.rakrae.gameengine.math.Vec2i
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@DisplayName("AABB (Axis-aligned bounding box)")
internal class AABBTest {

    @Nested
    @DisplayName("AABB for given points")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class CalculateAABBTest {

        @Test
        fun `determines correct AABB for the given points`() {
            val points = listOf(
                Vec2i(-42, -69),
                Vec2i(13, -1337),
                Vec2i(0, 0),
                Vec2i(900, 0)
            )

            assertThat(AABB2i.calculateBoundingBox(points))
                .isEqualTo(
                    AABB2i(
                        Vec2i(-42, -1337),
                        Vec2i(900, 0),
                    )
                )
        }
    }

    @Nested
    @DisplayName("Clamping")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class ClampingTest {

        private fun clampWithinOtherTestArgs(): Stream<Arguments> {
            return Stream.of(
                arguments(
                    "Completely enclosed by the bounds, result is the original AABB.",
                    AABB2i(Vec2i(-2, -2), Vec2i(2, 2)),
                    AABB2i(Vec2i(-40, -40), Vec2i(40, 40)),
                    AABB2i(Vec2i(-2, -2), Vec2i(2, 2))
                ),
                arguments(
                    "Completely enclosing the bounds, gets clamped to the bounds.",
                    AABB2i(Vec2i(-20, -20), Vec2i(20, 20)),
                    AABB2i(Vec2i(-13, -13), Vec2i(7, 7)),
                    AABB2i(Vec2i(-13, -13), Vec2i(7, 7))
                ),
                arguments(
                    "Overlapping with the bounds, gets partially clamped to the bounds.",
                    AABB2i(Vec2i(-20, -20), Vec2i(20, 20)),
                    AABB2i(Vec2i(-13, -13), Vec2i(70, 70)),
                    AABB2i(Vec2i(-13, -13), Vec2i(20, 20))
                ),
                arguments(
                    "Completely outside of the bounds, gets clamped to closest edge.",
                    AABB2i(Vec2i(-200, -200), Vec2i(-100, 200)),
                    AABB2i(Vec2i(100, -100), Vec2i(200, 70)),
                    AABB2i(Vec2i(100, -100), Vec2i(100, 70))
                ),
                arguments(
                    "Completely outside of the bounds, gets clamped to closest corner.",
                    AABB2i(Vec2i(1337, 1337), Vec2i(1342, 1342)),
                    AABB2i(Vec2i(10, 10), Vec2i(20, 20)),
                    AABB2i(Vec2i(20, 20), Vec2i(20, 20))
                )
            )
        }

        @ParameterizedTest
        @MethodSource("clampWithinOtherTestArgs")
        fun `clamps this AABB within bounds of another AABB`(
            description: String,
            toClamp: AABB2i,
            bounds: AABB2i,
            expected: AABB2i
        ) {
            assertThat(toClamp.clampWithin(bounds))
                .isEqualTo(expected)
                .describedAs(description)
        }
    }
}
