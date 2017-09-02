import java.util.Arrays

class MacroArp(values: IntArray, val arpType: Int // 0 is Absolute, 1 is Relative, 2 is Fixed
               , loopPoint: Int, releasePoint: Int, ident: Int) : Macro(values, loopPoint, releasePoint, ident) {
    companion object {

        @Throws(Exception::class)
        fun arpMacroBuilder(macroLine: String): MacroArp {
            val split = macroLine.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (split[0] != "MACRO")
                throw Exception("Provided string is not a FamiTracker macro!")
            if (split[1] != "1")
                throw Exception("Provided macro is not an arpeggio!")
            val loopPoint = Integer.parseInt(split[3])
            val releasePoint = Integer.parseInt(split[4])
            val arpType = Integer.parseInt(split[5])
            val ident = Integer.parseInt(split[2])
            val values = Arrays.copyOfRange(split, 7, split.size)
            val intValues = IntArray(values.size)
            for (i in values.indices) {
                intValues[i] = Integer.parseInt(values[i])
            }
            return MacroArp(intValues, arpType, loopPoint, releasePoint, ident)
        }
    }

}
