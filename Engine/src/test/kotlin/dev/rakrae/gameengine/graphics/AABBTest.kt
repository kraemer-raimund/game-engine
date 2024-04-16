package dev.rakrae.gameengine.graphics

import dev.rakrae.gameengine.math.Vec2i
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@DisplayName("AABB (Axis-aligned bounding box)")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class AABBTest {

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
