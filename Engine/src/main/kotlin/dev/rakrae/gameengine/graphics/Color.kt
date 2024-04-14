package dev.rakrae.gameengine.graphics

data class Color(
    val r: UByte,
    val g: UByte,
    val b: UByte,
    val a: UByte
) {

    val intValue: UInt
        get() {
            return (r.toUInt() shl 24) + (g.toUInt() shl 16) + (b.toUInt() shl 8) + a.toUInt()
        }

    companion object {
        fun from(intValue: UInt): Color {
            return Color(
                r = (intValue shr 24).toUByte(),
                g = (intValue shr 16).toUByte(),
                b = (intValue shr 8).toUByte(),
                a = intValue.toUByte()
            )
        }
    }
}
