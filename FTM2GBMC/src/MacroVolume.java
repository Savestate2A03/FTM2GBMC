
import java.util.Arrays;

public class MacroVolume {
    
    private final int[] values;
    private final int loopPoint;
    private final int releasePoint;
    
    public MacroVolume(int[] values, int loopPoint, int releasePoint) {
        this.values = values;
        this.loopPoint = loopPoint;
        this.releasePoint = releasePoint;
    }
    
    public int[] getValues() {
        return values;
    }
    
    public int getLoop() {
        return loopPoint;
    }
    
    public int getRelease() {
        return releasePoint;
    }
    
    public static MacroVolume volumeMacroBuilder(String macroLine) throws Exception {
        // example macro
        // MACRO       0   5   0  24   0 : 12 12 11 11 11 11 12 12 12 12 12 12 11 12 12 12 12 11 11 11 11 11 11 11 5 5 5 5 5 5 5 5 5 5 5 4 4 4 4 3
        // 0           1   2   3  4    5 6 7+
        String[] split = macroLine.split("\\s+");
        if (!split[0].equals("MACRO"))
            throw new Exception("Provided string is not a FamiTracker macro!");
        if (!split[1].equals("0"))
            throw new Exception("Provided macro is not volume!");
        int loopPoint    = Integer.parseInt(split[3]);
        int releasePoint = Integer.parseInt(split[4]);
        String[] values = Arrays.copyOfRange(split, 7, split.length);
        int[] intValues = new int[values.length];
        for (int i=0; i<values.length; i++) {
            intValues[i] = Integer.parseInt(values[i]);
        }
        return new MacroVolume(intValues, loopPoint, releasePoint);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<values.length; i++){
            if (i == loopPoint)
                sb.append("L ");
            if (i == releasePoint)
                sb.append("R ");
            sb.append(values[i]);
            if (i < values.length-1)
                sb.append(' ');
        }
        return sb.toString();
    }
    
}
