import java.util.Arrays;

public class MacroDuty extends Macro {

    private MacroDuty(int[] values, int loopPoint, int releasePoint, int ident) {
        super(values, loopPoint, releasePoint, ident);
    }

    public static MacroDuty dutyMacroBuilder(String macroLine) throws Exception {
        String[] split = macroLine.split("\\s+");
        if (!split[0].equals("MACRO"))
            throw new Exception("Provided string is not a FamiTracker macro!");
        if (!split[1].equals("4"))
            throw new Exception("Provided macro is not a duty macro!");
        int loopPoint = Integer.parseInt(split[3]);
        int releasePoint = Integer.parseInt(split[4]);
        int ident = Integer.parseInt(split[2]);
        String[] values = Arrays.copyOfRange(split, 7, split.length);
        int[] intValues = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            intValues[i] = Integer.parseInt(values[i]);
            if (intValues[i] > 3)
                throw new Exception("Duty cycle out of range! (greater than 3)");
            if (intValues[i] < 0)
                throw new Exception("Duty cycle out of range! (less than 0)");
        }
        return new MacroDuty(intValues, loopPoint, releasePoint, ident);
    }


}
