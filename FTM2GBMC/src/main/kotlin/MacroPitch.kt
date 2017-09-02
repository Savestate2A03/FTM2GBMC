import java.util.Arrays

class MacroPitch(values: IntArray, loopPoint: Int, releasePoint: Int, ident: Int) : Macro(values, loopPoint, releasePoint, ident) {
    companion object {

        @Throws(Exception::class)
        fun pitchMacroBuilder(macroLine: String): MacroPitch {
            val split = macroLine.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (split[0] != "MACRO")
                throw Exception("Provided string is not a FamiTracker macro!")
            if (split[1] != "2")
                throw Exception("Provided macro is not pitch!")
            val loopPoint = Integer.parseInt(split[3])
            val releasePoint = Integer.parseInt(split[4])
            val ident = Integer.parseInt(split[2])
            val values = Arrays.copyOfRange(split, 7, split.size)
            val intValues = IntArray(values.size)
            for (i in values.indices) {
                intValues[i] = Integer.parseInt(values[i])
            }
            return MacroPitch(intValues, loopPoint, releasePoint, ident)
        }
    }

}
