class Effect internal constructor(effect: String) {

    val type: String = effect[0].toString().toUpperCase()
    private val param1: Int = Integer.parseInt(effect.substring(1, 2), 16)
    private val param2: Int = Integer.parseInt(effect.substring(2, 3), 16)

    fun getParam(p: Int): Int {
        return if (p == 0) param1 else param2
    }

    companion object {

        fun effectsBuilder(effects: Array<String>): Array<Effect?> {
            var effectsLength = effects.count { it != "..." }
            val returnEffects = arrayOfNulls<Effect>(effectsLength)
            effectsLength = 0
            for (effect in effects) {
                if (effect != "...") {
                    returnEffects[effectsLength] = Effect(effect)
                    effectsLength++
                }
            }
            return returnEffects
        }
    }
}
