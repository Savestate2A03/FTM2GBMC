public class Macro {
    
    private final int[] values;
    private final int loopPoint;
    private final int releasePoint;
    private final int ident;
    
    public Macro(int[] values, int loopPoint, int releasePoint, int ident) {
        this.values = values;
        this.loopPoint = loopPoint;
        this.releasePoint = releasePoint;
        this.ident = ident;
    }
    
    public int getIdent() {
        return ident;
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
