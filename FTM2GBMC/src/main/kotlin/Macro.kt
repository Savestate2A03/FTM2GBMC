open class Macro(val values: IntArray, val loop: Int, val release: Int, val ident: Int) {

    override fun toString(): String {
        val sb = StringBuilder()
        for (i in values.indices) {
            if (i == loop)
                sb.append("L ")
            if (i == release)
                sb.append("R ")
            sb.append(values[i])
            if (i < values.size - 1)
                sb.append(' ')
        }
        return sb.toString()
    }

}
