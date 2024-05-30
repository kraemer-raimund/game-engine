package dev.rakrae.gameengine.graphics

data class Color(
    val r: UByte,
    val g: UByte,
    val b: UByte,
    val a: UByte
) {

    constructor(r: Int, g: Int, b: Int, a: Int)
            : this(r.toUByte(), g.toUByte(), b.toUByte(), a.toUByte())

    val asIntARGB: UInt
        get() {
            return (a.toUInt() shl 24) + (r.toUInt() shl 16) + (g.toUInt() shl 8) + b.toUInt()
        }

    operator fun plus(color: Color): Color {
        return Color(
            (r.normalized() + color.r.normalized()).remapToColor(),
            (g.normalized() + color.g.normalized()).remapToColor(),
            (b.normalized() + color.b.normalized()).remapToColor(),
            (a.normalized() + color.a.normalized()).remapToColor()
        )
    }

    operator fun times(color: Color): Color {
        return Color(
            (r.normalized() * color.r.normalized()).remapToColor(),
            (g.normalized() * color.g.normalized()).remapToColor(),
            (b.normalized() * color.b.normalized()).remapToColor(),
            (a.normalized() * color.a.normalized()).remapToColor()
        )
    }

    operator fun times(value: Float): Color {
        return Color(
            (value * r.normalized()).remapToColor(),
            (value * g.normalized()).remapToColor(),
            (value * b.normalized()).remapToColor(),
            (value * a.normalized()).remapToColor()
        )
    }

    override fun toString(): String {
        return "Color(" +
                "r=0x${r.toTwoDigitHexString()}, " +
                "g=0x${g.toTwoDigitHexString()}, " +
                "b=0x${b.toTwoDigitHexString()}, " +
                "a=0x${a.toTwoDigitHexString()})"
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun UByte.toTwoDigitHexString(): String {
        return this
            .toHexString(HexFormat.UpperCase)
            .padStart(2, '0')
    }

    private fun UByte.normalized(): Float {
        return this.toInt() / 255f
    }

    private fun Float.remapToColor(): UByte {
        return (this * 255).coerceIn(0f, 255f).toInt().toUByte()
    }

    companion object {
        val white = Color(255u, 255u, 255u, 255u)
        val black = Color(0u, 0u, 0u, 255u)
        val red = Color(255u, 0u, 0u, 255u)
        val green = Color(0u, 255u, 0u, 255u)
        val blue = Color(0u, 0u, 255u, 255u)
        val yellow = Color(255u, 255u, 0u, 255u)

        fun fromIntARGB(intValue: UInt): Color {
            return Color(
                a = (intValue shr 24).toUByte(),
                r = (intValue shr 16).toUByte(),
                g = (intValue shr 8).toUByte(),
                b = intValue.toUByte()
            )
        }

        fun lerp(color1: Color, color2: Color, t: Float): Color {
            val r = (1 - t) * color1.r.toInt() + t * color2.r.toInt()
            val g = (1 - t) * color1.g.toInt() + t * color2.g.toInt()
            val b = (1 - t) * color1.b.toInt() + t * color2.b.toInt()
            return Color(
                (r.toInt().coerceIn(0, 255)).toUByte(),
                (g.toInt().coerceIn(0, 255)).toUByte(),
                (b.toInt().coerceIn(0, 255)).toUByte(),
                255u
            )
        }
    }
}
