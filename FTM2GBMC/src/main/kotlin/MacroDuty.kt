import java.util.Arrays

class MacroDuty(values: IntArray, loopPoint: Int, releasePoint: Int, ident: Int) : Macro(values, loopPoint, releasePoint, ident) {
    companion object {

        @Throws(Exception::class)
        fun dutyMacroBuilder(macroLine: String): MacroDuty {
            val split = macroLine.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (split[0] != "MACRO")
                throw Exception("Provided string is not a FamiTracker macro!")
            if (split[1] != "4")
                throw Exception("Provided macro is not a duty macro!")
            val loopPoint = Integer.parseInt(split[3])
            val releasePoint = Integer.parseInt(split[4])
            val ident = Integer.parseInt(split[2])
            val values = Arrays.copyOfRange(split, 7, split.size)
            val intValues = IntArray(values.size)
            for (i in values.indices) {
                intValues[i] = Integer.parseInt(values[i])
                if (intValues[i] > 3)
                    throw Exception("Duty cycle out of range! (greater than 3)")
                if (intValues[i] < 0)
                    throw Exception("Duty cycle out of range! (less than 0)")
            }
            return MacroDuty(intValues, loopPoint, releasePoint, ident)
        }
    }


}
