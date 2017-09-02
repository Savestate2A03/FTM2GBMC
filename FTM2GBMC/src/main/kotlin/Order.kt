class Order(val pulse1: Int, val pulse2: Int, val triangle: Int, val noise: Int) {
    companion object {

        @Throws(Exception::class)
        fun orderBuilder(line: String): Order {
            // ORDER 0A : 01 05 03 02 02
            // 0     1  2 3  4  5  6  7
            val split = line.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (split[0] != "ORDER")
                throw Exception("Provided line was not an Order!")
            val pulse1 = Integer.parseInt(split[3], 16)
            val pulse2 = Integer.parseInt(split[4], 16)
            val triangle = Integer.parseInt(split[5], 16)
            val noise = Integer.parseInt(split[6], 16)
            return Order(pulse1, pulse2, triangle, noise)
        }
    }
}
