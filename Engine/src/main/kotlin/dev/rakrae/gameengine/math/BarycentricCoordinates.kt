package dev.rakrae.gameengine.math

/**
 * Barycentric coordinates describe a point relative to a triangle's corners.
 * Intuitively, they assign a weight to each of the three vertices, where the resulting
 * "center of mass" is the point P. The calculation, however, might not seem very
 * intuitive at first glance. See also the links below for detailed explanations
 * and formulas.
 *
 * The point P lies within the triangle if all three coordinates are > 0,
 * or exactly on an edge (or vertex) if one (resp. two) of them are = 0.
 * The coordinates are normalized if their sum is = 1.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Barycentric_coordinate_system">Barycentric coordinates (Wikipedia)</a>
 * @see <a href="https://en.wikipedia.org/wiki/Barycentric_coordinate_system#Conversion_between_barycentric_and_Cartesian_coordinates">Conversion between barycentric and Cartesian coordinates (Wikipedia)</a>
 * @see <a href="https://www.geogebra.org/m/c8DwbVTP">Interactive explanation (GeoGebra)</a>
 */
data class BarycentricCoordinates(
    val a1: Float,
    val a2: Float,
    val a3: Float
) {

    companion object {
        fun of(point: Vec2i, triangle: Triangle2i): BarycentricCoordinates {
            val p = point
            val v1 = triangle.v1
            val v2 = triangle.v2
            val v3 = triangle.v3

            val a1 = ((v2.y - v3.y) * (p.x - v3.x) + (v3.x - v2.x) * (p.y - v3.y)).toFloat() /
                    ((v2.y - v3.y) * (v1.x - v3.x) + (v3.x - v2.x) * (v1.y - v3.y)).toFloat()
            val a2 = ((v3.y - v1.y) * (p.x - v3.x) + (v1.x - v3.x) * (p.y - v3.y)).toFloat() /
                    ((v2.y - v3.y) * (v1.x - v3.x) + (v3.x - v2.x) * (v1.y - v3.y)).toFloat()
            val a3 = 1f - a1 - a2

            return BarycentricCoordinates(a1, a2, a3)
        }
    }
}
