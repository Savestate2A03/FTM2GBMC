import java.util.Arrays;

public class MacroPitch extends Macro {

    private MacroPitch(int[] values, int loopPoint, int releasePoint, int ident) {
        super(values, loopPoint, releasePoint, ident);
    }

    public static MacroPitch pitchMacroBuilder(String macroLine) throws Exception {
        String[] split = macroLine.split("\\s+");
        if (!split[0].equals("MACRO"))
            throw new Exception("Provided string is not a FamiTracker macro!");
        if (!split[1].equals("2"))
            throw new Exception("Provided macro is not pitch!");
        int loopPoint = Integer.parseInt(split[3]);
        int releasePoint = Integer.parseInt(split[4]);
        int ident = Integer.parseInt(split[2]);
        String[] values = Arrays.copyOfRange(split, 7, split.length);
        int[] intValues = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            intValues[i] = Integer.parseInt(values[i]);
        }
        return new MacroPitch(intValues, loopPoint, releasePoint, ident);
    }

}
