import java.util.Arrays

class MacroVolume(values: IntArray, loopPoint: Int, releasePoint: Int, ident: Int) : Macro(values, loopPoint, releasePoint, ident) {
    companion object {

        @Throws(Exception::class)
        fun volumeMacroBuilder(macroLine: String): MacroVolume {
            val split = macroLine.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (split[0] != "MACRO")
                throw Exception("Provided string is not a FamiTracker macro!")
            if (split[1] != "0")
                throw Exception("Provided macro is not volume!")
            val loopPoint = Integer.parseInt(split[3])
            val releasePoint = Integer.parseInt(split[4])
            val ident = Integer.parseInt(split[2])
            val values = Arrays.copyOfRange(split, 7, split.size)
            val intValues = IntArray(values.size)
            for (i in values.indices) {
                intValues[i] = Integer.parseInt(values[i])
            }
            return MacroVolume(intValues, loopPoint, releasePoint, ident)
        }
    }

}
