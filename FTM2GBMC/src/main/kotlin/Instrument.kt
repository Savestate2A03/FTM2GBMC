class Instrument(val volume: Int, private val arp: Int, val pitch: Int, val duty: Int, val ident: Int, n: String) {
    private var name: String
        set

    init {
        name = n
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append(name).append(" --> ")
        sb.append("V:").append(volume).append(' ')
        sb.append("A:").append(arp).append(' ')
        sb.append("P:").append(pitch).append(' ')
        sb.append("D:").append(duty)
        return sb.toString()
    }

    companion object {

        @Throws(Exception::class)
        fun instrumentBuilder(macroLine: String): Instrument {
            // INST2A03   7     6  -1   2  -1   3 "pitchy"
            // 0          1     2   3   4   5   6 7
            val split = macroLine.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (split[0] != "INST2A03")
                throw Exception("Provided line was not a FamiTracker instrument!")
            val i = Integer.parseInt(split[1])
            val v = Integer.parseInt(split[2])
            val a = Integer.parseInt(split[3])
            val p = Integer.parseInt(split[4])
            val d = Integer.parseInt(split[6])
            return Instrument(v, a, p, d, i, split[7].replace("\\\"".toRegex(), ""))
        }
    }

}
