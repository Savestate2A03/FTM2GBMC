import java.util.Arrays;

public class MacroArp extends Macro {

    private final int arpType; // 0 is Absolute, 1 is Relative, 2 is Fixed

    public MacroArp(int[] values, int arpType, int loopPoint, int releasePoint, int ident) {
        super(values, loopPoint, releasePoint, ident);
        this.arpType = arpType;
    }

    public static MacroArp arpMacroBuilder(String macroLine) throws Exception {
        String[] split = macroLine.split("\\s+");
        if (!split[0].equals("MACRO"))
            throw new Exception("Provided string is not a FamiTracker macro!");
        if (!split[1].equals("1"))
            throw new Exception("Provided macro is not an arpeggio!");
        int loopPoint = Integer.parseInt(split[3]);
        int releasePoint = Integer.parseInt(split[4]);
        int arpType = Integer.parseInt(split[5]);
        int ident = Integer.parseInt(split[2]);
        String[] values = Arrays.copyOfRange(split, 7, split.length);
        int[] intValues = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            intValues[i] = Integer.parseInt(values[i]);
        }
        return new MacroArp(intValues, arpType, loopPoint, releasePoint, ident);
    }

    public int getArpType() {
        return arpType;
    }

}
