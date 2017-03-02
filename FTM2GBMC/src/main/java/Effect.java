public class Effect {

    private final String type;
    private final int param1;
    private final int param2;

    Effect(String effect) {
        type = String.valueOf(effect.charAt(0)).toUpperCase();
        param1 = Integer.parseInt(effect.substring(1, 2), 16);
        param2 = Integer.parseInt(effect.substring(2, 3), 16);
    }

    public static Effect[] effectsBuilder(String[] effects) {
        int effectsLength = 0;
        for (String effect : effects) {
            if (!effect.equals("..."))
                effectsLength++;
        }
        Effect[] returnEffects = new Effect[effectsLength];
        effectsLength = 0;
        for (String effect : effects) {
            if (!effect.equals("...")) {
                returnEffects[effectsLength] = new Effect(effect);
                effectsLength++;
            }
        }
        return returnEffects;
    }

    public String getType() {
        return type;
    }

    public int getParam(int p) {
        if (p == 0)
            return param1;
        return param2;
    }
}
