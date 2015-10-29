public class Macro {
    
    protected final int[] values;
    protected final int loopPoint;
    protected final int releasePoint;
    
    public Macro(int[] values, int loopPoint, int releasePoint) {
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
